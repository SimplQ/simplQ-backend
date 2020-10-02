package com.example.restservice.dao;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface QueueRepository extends JpaRepository<Queue, String> {

  Stream<Queue> findByOwnerId(String ownerId);

  Optional<Queue> findByQueueName(String queueName);
}
