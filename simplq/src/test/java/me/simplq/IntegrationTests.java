package me.simplq;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.simplq.config.TestConfig;
import me.simplq.controller.model.queue.CreateQueueResponse;
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

  private String queueId;

  @Test
  void createQueueTest() throws Exception {

    String createQueueRequest = "{ \"queueName\": \"Queue2222\" }";

    MvcResult result =
        mockMvc
            .perform(
                post("/v1/queue", 42L)
                    .contentType("application/json")
                    .content(createQueueRequest)
                    .header(
                        "Authorization",
                        "Bearer eyJraWQiOiJMOG1UNExrbm9Sb2FLaWsxeTlURE1IZlQ0RXdUTk5YY2taMlVsK2NreUZZPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1YTFiZWZiYS1lODM1LTRjYTAtYTNiYi05ODNkMjY2M2UwNDAiLCJldmVudF9pZCI6ImM4Yjc5YzBkLTJmNGMtNGMxMi05MjJiLTlhNmM1ZjcxMTU0NyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1OTMyNzA4NzIsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5hcC1zb3V0aGVhc3QtMS5hbWF6b25hd3MuY29tXC9hcC1zb3V0aGVhc3QtMV9pUWRsNUFWckEiLCJleHAiOjE1OTMyNzQ0NzIsImlhdCI6MTU5MzI3MDg3MiwianRpIjoiNjM2YjY3NDYtYTk2OC00ZTcxLWE3ZmEtNGU4NGEwYzU0NDdlIiwiY2xpZW50X2lkIjoiMWY3bzZvNHZoaGhucWpiYjdob21hbHFtdnIiLCJ1c2VybmFtZSI6IjVjOWNkNDI2LWY3ZTMtNDVjYy1hYmUxLWI4NDc0YzdlMTExNSJ9.DOETlW-LnGwpI73XbDOtfpe9jlAhjWAVmHPdVmntUqu2SxI2Y26LIX2vLg0Z2AAltaut2Xxrl2W-yMi4gtbXECACjQeZyfwwx62hwR56pX0S-oAa_JtlsHlmzdvYaGDyGE5CDI3skSbFy_eSQM6xueogJGxRbGBh6W_C9c5lZZNKfiYn8DTL89seSiiXOU1VigsHggcsniBcFz0JYq4SHH17KjGl4RhYDrDC6aMVV3wM1ZiJmZ8MFUYpVfDLBlDCVJSzrZ93vDOsTTLg_xHDFq84J3mVoK5IJ9yFC5oLBWtyT-9h-IZe12Gr1BZpu8MlOEuAOppe0a-3ZI4uHb2y_Q"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    String response = result.getResponse().getContentAsString();
    CreateQueueResponse apiResponse = objectMapper.readValue(response, CreateQueueResponse.class);
    Assertions.assertEquals("Queue2222", apiResponse.getQueueName());
    //store this queue id for further tests
    queueId = apiResponse.getQueueId();

  }
}
