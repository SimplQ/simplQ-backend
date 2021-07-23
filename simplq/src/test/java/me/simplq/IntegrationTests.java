package me.simplq;

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
import me.simplq.dao.QueueRepository;
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

  @Autowired private QueueRepository queueRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private Long initialPhoneNumber = 9400000000L;

  @Test
  void endToEndScenarioTest() throws Exception {

    // Device not linked for new user
    MvcResult newDeviceStatus =
        mockMvc.perform(get("/v1/me/status?deviceId=12345")).andExpect(status().isOk()).andReturn();
    Assertions.assertEquals("false", newDeviceStatus.getResponse().getContentAsString());

    // Create queue
    var createQueueResponse = createQueueCall("FirstQueue");
    Assertions.assertEquals("FirstQueue", createQueueResponse.getQueueName());

    // GET queue by id
    var queueDetailsResponse = getQueueById(createQueueResponse.getQueueId());
    Assertions.assertEquals(queueDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(
        queueDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

    // POST token
    var createTokenResponse = callCreateToken(createQueueResponse.getQueueId());
    var anotherTokenResponse = callCreateToken(createQueueResponse.getQueueId());

    Assertions.assertEquals(createTokenResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(createTokenResponse.getQueueName(), createQueueResponse.getQueueName());

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
    Assertions.assertEquals(tokenDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(
        tokenDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

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
    Assertions.assertEquals(tokenDetailsResponse.getTokenId(), tokenDeleteResponse.getTokenId());
    Assertions.assertEquals(TokenStatus.REMOVED, tokenDeleteResponse.getTokenStatus());
    Assertions.assertEquals(
        tokenDetailsResponse.getQueueName(), tokenDeleteResponse.getQueueName());

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
    Assertions.assertEquals(createQueueResponse.getQueueName(), queue.getQueueName());

    // GET myTokens
    mockMvc
        .perform(get("/v1/tokens"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    var queueUpdated = getQueueById(createQueueResponse.getQueueId());
    Assertions.assertEquals(queueUpdated.getTokens().size(), 1);
    Assertions.assertEquals(queueUpdated.getRemovedTokens().size(), 1);
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
    Assertions.assertEquals(
        createQueueResponse.getQueueId(), queueStatusResponseDeleted.getQueueId());
    Assertions.assertEquals(QueueStatus.DELETED, queueStatusResponseDeleted.getStatus());

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
    Assertions.assertEquals(
        0,
        myQueuesResponseDeleted.getQueues().stream()
            .filter(queue1 -> queue1.getQueueId().equals(createQueueResponse.getQueueId()))
            .count());

    // Initially device not linked
    MvcResult deviceStatus =
        mockMvc.perform(get("/v1/me/status?deviceId=1234")).andExpect(status().isOk()).andReturn();
    Assertions.assertEquals("false", deviceStatus.getResponse().getContentAsString());

    // Link user to companion device
    mockMvc.perform(put("/v1/me/link?deviceId=1234")).andExpect(status().isOk()).andReturn();

    // Now device is linked
    MvcResult deviceStatus2 =
        mockMvc.perform(get("/v1/me/status?deviceId=1234")).andExpect(status().isOk()).andReturn();
    Assertions.assertEquals("true", deviceStatus2.getResponse().getContentAsString());
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
    var patchQueueRequest = new PatchQueueRequest(10, null);

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
    Assertions.assertEquals(patchResponse.getMaxQueueCapacity(), 10);

    var queueDetailAtStart = getQueueById(createQueueResponse.getQueueId());
    Assertions.assertEquals(10, queueDetailAtStart.getMaxQueueCapacity());
    Assertions.assertEquals(10, queueDetailAtStart.getSlotsLeft());

    // Successfully add ten tokens
    IntStream.range(0, 10)
        .forEach(
            i -> {
              callCreateToken(createQueueResponse.getQueueId());
              System.out.println(i);
            });

    var queueDetailAtFull = getQueueById(createQueueResponse.getQueueId());
    Assertions.assertEquals(10, queueDetailAtFull.getMaxQueueCapacity());
    Assertions.assertEquals(0, queueDetailAtFull.getSlotsLeft());

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
