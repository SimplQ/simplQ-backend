package com.example.restservice.controller;

import com.example.restservice.model.Queue;
import com.example.restservice.model.User;
import com.example.restservice.dao.QueueDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.restservice.service.QueueService;

import java.util.List;

@RestController
public class QueueController {

	private Queue queue;

	@Autowired
	private QueueService queueService;

	@Autowired
	private QueueDAO queueDAO;

	@GetMapping(value ="/v1/queue/{queueId}", produces = "application/json")
	public List<User> getQueueDetails(@PathVariable String queueId ) {
		List<User> userList = queueDAO.fetchQueueData(queueId);
		return userList;
	}

	@PostMapping(path = "v1/queue", consumes = "application/json", produces = "application/json")
	public Queue createQueue(@RequestBody String queueName){
		return queueService.generateQueueId(queueName);
	}
}
