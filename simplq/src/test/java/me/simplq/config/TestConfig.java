package me.simplq.config;

import me.simplq.service.QueueService;
import me.simplq.service.TokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@TestConfiguration
@EnableJpaRepositories("me.simplq.dao")
@ContextConfiguration(classes = {QueueService.class, TokenService.class})
public class TestConfig {}
