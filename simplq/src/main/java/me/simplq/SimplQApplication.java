package me.simplq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class SimplQApplication {
  public static void main(String[] args) {

    SpringApplication.run(SimplQApplication.class, args);
  }
}
