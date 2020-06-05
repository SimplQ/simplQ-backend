package com.example.restservice.dao;

import com.example.restservice.model.Queue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueRepository extends CrudRepository<Queue, String> {}
