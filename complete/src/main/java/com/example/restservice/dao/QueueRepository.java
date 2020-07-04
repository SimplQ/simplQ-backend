package com.example.restservice.dao;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface QueueRepository extends CrudRepository<Queue, String> {

  Stream<Queue> findByOwnerId(String ownerId);

  Optional<Queue> findByQueueName(String queueName);
}
