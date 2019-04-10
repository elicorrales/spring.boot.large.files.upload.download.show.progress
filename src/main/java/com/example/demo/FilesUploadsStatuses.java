package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilesUploadsStatuses {

	private String overallMessage;
	
	public String getOverallMessage() {
		return overallMessage;
	}

	public void setOverallMessage(String overallMessage) {
		this.overallMessage = overallMessage;
	}

	private final Map<String,String> fileStatuses = new HashMap<>();
	
	public void addFileStatus(String fileName, String message) {
		this.fileStatuses.put(fileName, message);
	}

	public Map<String,String> getFileStatuses() {
		return fileStatuses;
	}

}
