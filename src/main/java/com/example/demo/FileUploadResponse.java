package com.example.demo;

public class FileUploadResponse {

	private final String fileName;
	private final String fileDownloadUri;
	private final String contentType;
	private final long size;

	public FileUploadResponse(String fileName, String fileDownloadUri, String contentType, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.contentType = contentType;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public String getContentType() {
		return contentType;
	}

	public long getSize() {
		return size;
	}
	

}
