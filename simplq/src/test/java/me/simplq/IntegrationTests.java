package me.simplq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestConfig.class})
class IntegrationTests {

  @Autowired private QueueRepository queueRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String authToken = "anonymous";

  @Test
  void endToEndScenarioTest() throws Exception {

    // Create queue
    String createQueueRequest = "{ \"queueName\": \"Queue2222\" }";

    MvcResult createQueueResult =
        mockMvc
            .perform(
                post("/v1/queue", 42L)
                    .contentType("application/json")
                    .header("Authorization", authToken)
                    .content(createQueueRequest))
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
            .perform(
                get("/v1/queue/" + createQueueResponse.getQueueId())
                    .header("Authorization", authToken))
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
                post("/v1/token", 42L)
                    .contentType("application/json")
                    .header("Authorization", authToken)
                    .content(createTokenRequest))
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
            .perform(
                get("/v1/queue/" + createTokenResponse.getQueueId())
                    .header("Authorization", authToken))
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
            .perform(get("/v1/queues").header("Authorization", authToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    MyQueuesResponse myQueuesResponse =
        objectMapper.readValue(
            myQueuesResult.getResponse().getContentAsString(), MyQueuesResponse.class);
    Assertions.assertEquals(myQueuesResponse.getQueues().size(), 1);
    var queue = myQueuesResponse.getQueues().get(0);
    Assertions.assertEquals(queue.getQueueName(), createQueueResponse.getQueueName());
    Assertions.assertEquals(queue.getQueueId(), createQueueResponse.getQueueId());

    // GET myTokens
    mockMvc
        .perform(get("/v1/tokens").header("Authorization", authToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    MvcResult deleteQueueResult =
        mockMvc
            .perform(
                delete("/v1/queue/" + createQueueResponse.getQueueId())
                    .header("Authorization", authToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    QueueStatusResponse queueStatusResponseDeleted =
        objectMapper.readValue(
            deleteQueueResult.getResponse().getContentAsString(), QueueStatusResponse.class);
    Assertions.assertEquals(
        queueStatusResponseDeleted.getQueueId(), createQueueResponse.getQueueId());
    Assertions.assertEquals(queueStatusResponseDeleted.getStatus(), QueueStatus.DELETED);

    // GET myQueues
    MvcResult myQueuesResultDeleted =
        mockMvc
            .perform(get("/v1/queues").header("Authorization", authToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    MyQueuesResponse myQueuesResponseDeleted =
        objectMapper.readValue(
            myQueuesResult.getResponse().getContentAsString(), MyQueuesResponse.class);
    Assertions.assertEquals(myQueuesResponseDeleted.getQueues().size(), 0);
  }
}
