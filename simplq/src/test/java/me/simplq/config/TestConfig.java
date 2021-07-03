package me.simplq.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import me.simplq.controller.advices.LoggedInUserInfo;
import me.simplq.service.OwnerService;
import me.simplq.service.QueueService;
import me.simplq.service.TokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@TestConfiguration
@EnableJpaRepositories("me.simplq.dao")
@ContextConfiguration(classes = {QueueService.class, TokenService.class, OwnerService.class})
public class TestConfig {
  @Bean
  public Filter mockAuthFilter(LoggedInUserInfo loggedInUserInfo) {
    return new Filter() {
      @Override
      public void doFilter(
          ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
          throws IOException, ServletException {
        loggedInUserInfo.setUserId("test-user-id");
        filterChain.doFilter(servletRequest, servletResponse);
      }
    };
  }
}
