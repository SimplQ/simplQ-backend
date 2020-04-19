package com.example.restservice.controller;

import com.example.restservice.model.Queue;
import com.example.restservice.model.User;
import dao.QueueDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QueueController {

	private Queue queue;

	private QueueDAO queueDAO;

	@GetMapping(value ="/v1/queue/{queueId}", produces = "application/json")
	public List<User> getQueueDetails(@PathVariable String queueId ) {
		queueDAO = new QueueDAO();
		List<User> userList = queueDAO.fetchQueueData(queueId);
		System.out.println(queueId);
		return userList;


	}





}
