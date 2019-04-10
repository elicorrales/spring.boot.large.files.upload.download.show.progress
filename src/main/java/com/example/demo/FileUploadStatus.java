package com.example.demo;

public class FileUploadStatus {

	private final String fileName;
	private final String status;
	
	public FileUploadStatus(String fileName, String status) {
		this.fileName = fileName;
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public String getStatus() {
		return status;
	}

}
