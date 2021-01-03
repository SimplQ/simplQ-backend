package me.simplq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.simplq.config.TestConfig;
import me.simplq.constants.QueueStatus;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.MyQueuesResponse;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.queue.QueueStatusResponse;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles({"test", "local"})
class IntegrationTests {

  @Autowired private QueueRepository queueRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void endToEndScenarioTest() throws Exception {

    // Device not linked for new user
    MvcResult newDeviceStatus =
        mockMvc.perform(get("/v1/me/status?deviceId=12345")).andExpect(status().isOk()).andReturn();
    Assertions.assertEquals("false", newDeviceStatus.getResponse().getContentAsString());

    // Create queue
    String createQueueRequest = "{ \"queueName\": \"Queue2222\" }";

    MvcResult createQueueResult =
        mockMvc
            .perform(
                post("/v1/queue", 42L).contentType("application/json").content(createQueueRequest))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    String response = createQueueResult.getResponse().getContentAsString();
    CreateQueueResponse createQueueResponse =
        objectMapper.readValue(response, CreateQueueResponse.class);
    Assertions.assertEquals("Queue2222", createQueueResponse.getQueueName());

    // GET queue by id
    MvcResult getQueueResult =
        mockMvc
            .perform(get("/v1/queue/" + createQueueResponse.getQueueId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    QueueDetailsResponse queueDetailsResponse =
        objectMapper.readValue(
            getQueueResult.getResponse().getContentAsString(), QueueDetailsResponse.class);
    Assertions.assertEquals(queueDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(
        queueDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

    // POST token
    String createTokenRequest =
        "{ \"contactNumber\": \"999999999\","
            + "\"name\": \"user name\","
            + "\"queueId\": \""
            + createQueueResponse.getQueueId()
            + "\"}";

    MvcResult createTokenResult =
        mockMvc
            .perform(
                post("/v1/token", 42L).contentType("application/json").content(createTokenRequest))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    TokenDetailResponse createTokenResponse =
        objectMapper.readValue(
            createTokenResult.getResponse().getContentAsString(), TokenDetailResponse.class);
    Assertions.assertEquals(createTokenResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(createTokenResponse.getQueueName(), createQueueResponse.getQueueName());

    // GET token by id
    MvcResult getTokenResult =
        mockMvc
            .perform(get("/v1/queue/" + createTokenResponse.getQueueId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    TokenDetailResponse tokenDetailsResponse =
        objectMapper.readValue(
            getTokenResult.getResponse().getContentAsString(), TokenDetailResponse.class);
    Assertions.assertEquals(tokenDetailsResponse.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(
        tokenDetailsResponse.getQueueName(), createQueueResponse.getQueueName());

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
    Assertions.assertEquals(myQueuesResponse.getQueues().size(), 1);
    var queue = myQueuesResponse.getQueues().get(0);
    Assertions.assertEquals(createQueueResponse.getQueueName(), queue.getQueueName());
    Assertions.assertEquals(createQueueResponse.getQueueId(), queue.getQueueId());

    // GET myTokens
    mockMvc
        .perform(get("/v1/tokens"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

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
    Assertions.assertEquals(0, myQueuesResponseDeleted.getQueues().size());

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
}
