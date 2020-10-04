package me.simplq.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import me.simplq.config.TestConfig;
import me.simplq.controller.QueueController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QueueController.class)
@ContextConfiguration(classes = {TestConfig.class})
public class QueueControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;


    @Test
    public void createQueueTest() throws Exception {

        String content = "{" +
                "\"queueName\": \"AnotherQueue2222\"" +
                "}" ;

   mockMvc.perform(post("/v1/queue", 42L)
            .contentType("application/json")
            .content(content))
            .andExpect(status().isBadRequest());

}

}
