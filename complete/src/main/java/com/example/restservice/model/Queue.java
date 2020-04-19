package com.example.restservice.model;

public class Queue {

	private final long id;
	private final String content;

	public Queue(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
