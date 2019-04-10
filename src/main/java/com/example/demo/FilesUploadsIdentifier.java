package com.example.demo;

public class FilesUploadsIdentifier {

	private final String uuid;
	
	private String message;
	
	public FilesUploadsIdentifier(String uuid) {
		
		this.uuid = uuid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUuid() {
		return uuid;
	}

}
