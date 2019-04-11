package com.example.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FilesUploadsStatuses {

	private String overallMessage = "All Good";
	
	private final Map<String,FileUploadStatus> fileStatuses = new HashMap<>();

	public String getOverallMessage() {
		return overallMessage;
	}

	public void setOverallMessage(String overallMessage) {
		this.overallMessage = overallMessage;
	}

	public void addFileStatus(String fileName, long size, String message) {
		this.fileStatuses.put(fileName, new FileUploadStatus(fileName,size,message));
	}

	public Map<String,FileUploadStatus> getFileStatuses() {
		return fileStatuses;
	}
/*
	public FileUploadStatus[] getFileStatuses() {
		return fileStatuses.entrySet().toArray(new FileUploadStatus[fileStatuses.entrySet().size()]);
	}
*/
}
