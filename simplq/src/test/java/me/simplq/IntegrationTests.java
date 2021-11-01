package me.simplq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import me.simplq.config.TestConfig;
import me.simplq.constants.QueueStatus;
import me.simplq.constants.TokenStatus;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.PatchQueueRequest;
import me.simplq.controller.model.queue.PatchQueueResponse;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
import me.simplq.controller.model.token.TokenDeleteResponse;
import me.simplq.controller.model.token.TokenDetailResponse;
import me.simplq.dao.DeviceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles({"test"})
class IntegrationTests {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private Long initialPhoneNumber = 9400000000L;

  @Test
  void endToEndScenarioTest() throws Exception {

    // Create queue
    var createQueueResponse = createQueueCall("FirstQueue");
    assertEquals("FirstQueue", createQueueResponse.getQueueName());

    // GET queue by id
    var queueDetailsResponse = getQueueById(createQueueResponse.getQueueId());
    assertEquals(queueDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    assertEquals(queueDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

    // POST token
    var createTokenResponse = callCreateToken(createQueueResponse.getQueueId());
    var anotherTokenResponse = callCreateToken(createQueueResponse.getQueueId());

    assertEquals(createTokenResponse.getQueueId(), createQueueResponse.getQueueId());
    assertEquals(createTokenResponse.getQueueName(), createQueueResponse.getQueueName());

    // GET token by id
    MvcResult getTokenResult =
        mockMvc
            .perform(get("/v1/token/" + createTokenResponse.getTokenId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    TokenDetailResponse tokenDetailsResponse =
        objectMapper.readValue(
            getTokenResult.getResponse().getContentAsString(), TokenDetailResponse.class);
    assertEquals(tokenDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    assertEquals(tokenDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

    // DELETE Token
    MvcResult deleteTokenResult =
        mockMvc
            .perform(delete("/v1/token/" + tokenDetailsResponse.getTokenId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    TokenDeleteResponse tokenDeleteResponse =
        objectMapper.readValue(
            deleteTokenResult.getResponse().getContentAsString(), TokenDeleteResponse.class);
    assertEquals(tokenDetailsResponse.getTokenId(), tokenDeleteResponse.getTokenId());
    assertEquals(TokenStatus.REMOVED, tokenDeleteResponse.getTokenStatus());
    assertEquals(tokenDetailsResponse.getQueueName(), tokenDeleteResponse.getQueueName());

    // GET myQueues
    MvcResult myQueuesResult =
        mockMvc
            .perform(get("/v1/queues"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    MyQueuesResponse myQueuesResponse =
        objectMapper.readValue(
            myQueuesResult.getResponse().getContentAsString(), MyQueuesResponse.class);
    var queue =
        myQueuesResponse.getQueues().stream()
            .filter(queue1 -> queue1.getQueueId().equals(createQueueResponse.getQueueId()))
            .findFirst()
            .get();
    assertEquals(createQueueResponse.getQueueName(), queue.getQueueName());

    // GET myTokens
    mockMvc
        .perform(get("/v1/tokens"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    var queueUpdated = getQueueById(createQueueResponse.getQueueId());
    assertEquals(queueUpdated.getTokens().size(), 1);
    assertEquals(queueUpdated.getRemovedTokens().size(), 1);
    assertQueueContainsToken(queueUpdated.getTokens(), anotherTokenResponse.getTokenId());
    assertQueueContainsToken(queueUpdated.getRemovedTokens(), createTokenResponse.getTokenId());

    MvcResult deleteQueueResult =
        mockMvc
            .perform(delete("/v1/queue/" + createQueueResponse.getQueueId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    QueueStatusResponse queueStatusResponseDeleted =
        objectMapper.readValue(
            deleteQueueResult.getResponse().getContentAsString(), QueueStatusResponse.class);
    assertEquals(createQueueResponse.getQueueId(), queueStatusResponseDeleted.getQueueId());
    assertEquals(QueueStatus.DELETED, queueStatusResponseDeleted.getStatus());

    // GET myQueues
    MvcResult myQueuesResultDeleted =
        mockMvc
            .perform(get("/v1/queues"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    MyQueuesResponse myQueuesResponseDeleted =
        objectMapper.readValue(
            myQueuesResultDeleted.getResponse().getContentAsString(), MyQueuesResponse.class);
    assertEquals(
        0,
        myQueuesResponseDeleted.getQueues().stream()
            .filter(queue1 -> queue1.getQueueId().equals(createQueueResponse.getQueueId()))
            .count());
  }

  @Test
  void deviceRegistrationScenarioTest() throws Exception {
    // Initially device not present

    // Link user to companion device
    mockMvc.perform(put("/v1/owner/link?deviceId=1234")).andExpect(status().isOk()).andReturn();

    // Duplicate linking is handled gracefully
    mockMvc.perform(put("/v1/owner/link?deviceId=1234")).andExpect(status().isOk()).andReturn();

    // Device is now present
    var device = deviceRepository.findById("1234").get();
    assertEquals("test-user-id", device.getOwner().getId());

    // Second device is added
    mockMvc.perform(put("/v1/owner/link?deviceId=5678")).andExpect(status().isOk()).andReturn();

    // Now two devices are present
    assertEquals(2, deviceRepository.count());

    // Remove first device
    mockMvc.perform(patch("/v1/owner/unlink?deviceId=1234")).andExpect(status().isOk()).andReturn();

    // Now only one device is present.
    assertEquals(1, deviceRepository.count());
  }

  private void assertQueueContainsToken(List<QueueDetailsResponse.Token> queue, String tokenId) {
    Assertions.assertTrue(
        queue.stream()
            .map(QueueDetailsResponse.Token::getTokenId)
            .collect(Collectors.toList())
            .contains(tokenId));
  }

  @SneakyThrows
  private QueueDetailsResponse getQueueById(String queueId) {
    MvcResult getQueueResult =
        mockMvc
            .perform(get("/v1/queue/" + queueId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    return objectMapper.readValue(
        getQueueResult.getResponse().getContentAsString(), QueueDetailsResponse.class);
  }

  @SneakyThrows
  private CreateQueueResponse createQueueCall(String queueName) {
    String createQueueRequest = "{ \"queueName\": \"" + queueName + "\" }";

    MvcResult createQueueResult =
        mockMvc
            .perform(
                post("/v1/queue", 42L).contentType("application/json").content(createQueueRequest))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    return objectMapper.readValue(
        createQueueResult.getResponse().getContentAsString(), CreateQueueResponse.class);
  }

  @Test
  void maxQueueCapacityScenarioTest() throws Exception {
    var createQueueResponse = createQueueCall("MaxQueueCapacityTestQueue");

    // Patch queue with MAX_QUEUE_CAPACITY
    var patchQueueRequest = new PatchQueueRequest(10, null, null);

    var patchQueueRequestJson = objectMapper.writeValueAsBytes(patchQueueRequest);

    MvcResult patchResult =
        mockMvc
            .perform(
                patch("/v1/queue/" + createQueueResponse.getQueueId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(patchQueueRequestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var patchResponse =
        objectMapper.readValue(
            patchResult.getResponse().getContentAsString(), PatchQueueResponse.class);
    assertEquals(patchResponse.getMaxQueueCapacity(), 10);

    var queueDetailAtStart = getQueueById(createQueueResponse.getQueueId());
    assertEquals(10, queueDetailAtStart.getMaxQueueCapacity());
    assertEquals(10, queueDetailAtStart.getSlotsLeft());

    // Successfully add ten tokens
    IntStream.range(0, 10)
        .forEach(
            i -> {
              callCreateToken(createQueueResponse.getQueueId());
              System.out.println(i);
            });

    var queueDetailAtFull = getQueueById(createQueueResponse.getQueueId());
    assertEquals(10, queueDetailAtFull.getMaxQueueCapacity());
    assertEquals(0, queueDetailAtFull.getSlotsLeft());

    // Fail on 11th token
    makeCreateTokenCall(createQueueResponse.getQueueId()).andExpect(status().is4xxClientError());
  }

  @SneakyThrows
  private ResultActions makeCreateTokenCall(String queueId) {
    return mockMvc.perform(
        post("/v1/token", 42L)
            .contentType("application/json")
            .content(createTokenRequest(queueId)));
  }

  @SneakyThrows
  private TokenDetailResponse callCreateToken(String queueId) {
    var createTokenResult =
        makeCreateTokenCall(queueId)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    return objectMapper.readValue(
        createTokenResult.getResponse().getContentAsString(), TokenDetailResponse.class);
  }

  private String createTokenRequest(String queueId) {
    return "{ \"contactNumber\": \""
        + getNewNumber()
        + "\","
        + "\"name\": \"user name\","
        + "\"queueId\": \""
        + queueId
        + "\"}";
  }

  private String getNewNumber() {
    return "+" + String.valueOf(initialPhoneNumber++);
  }
}
