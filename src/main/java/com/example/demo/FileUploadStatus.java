package com.example.demo;

public class FileUploadStatus {

	private final String name;
	private final long   size;
	private final String status;
	public FileUploadStatus(String name, long size, String status) {
		super();
		this.name = name;
		this.size = size;
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public long getSize() {
		return size;
	}
	public String getStatus() {
		return status;
	}
	
	
}
