package com.example.demo;

public class SimpleFileUploadResponse {

	private final String message;
	private final long size;

	public SimpleFileUploadResponse(String message, long size) {
		this.message = message;
		this.size = size;
	}

	public String getMessage() {
		return message;
	}

	public long getSize() {
		return size;
	}
}
