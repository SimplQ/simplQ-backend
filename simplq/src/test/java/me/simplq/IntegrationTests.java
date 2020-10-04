package me.simplq;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.simplq.config.TestConfig;
import me.simplq.controller.model.queue.CreateQueueResponse;
import me.simplq.controller.model.queue.QueueDetailsResponse;
import me.simplq.controller.model.token.TokenDetailResponse;
import me.simplq.dao.QueueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestConfig.class})
class IntegrationTests {

  @Autowired private QueueRepository queueRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Value("${auth.token}")
  private String authToken;

  @Test
  void endToEndScenarioTest() throws Exception {

    // POST queue
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
    mockMvc
        .perform(get("/v1/queues").header("Authorization", authToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // GET myTokens
    mockMvc
        .perform(get("/v1/tokens").header("Authorization", authToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }
}
