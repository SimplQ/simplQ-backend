package com.example.restservice.dao;

import com.example.restservice.RestServiceApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface QueueRepository extends CrudRepository<Queue, String> {}
