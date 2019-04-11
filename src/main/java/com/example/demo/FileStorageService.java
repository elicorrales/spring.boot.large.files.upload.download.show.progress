package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path storageLocation;
	
	
	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProps) throws IOException {
		this.storageLocation =
				Paths.get(fileStorageProps.getUploadDir())
					.toAbsolutePath().normalize();
	
		if (!Files.exists(this.storageLocation)) {
			Files.createDirectory(this.storageLocation);
		}
	}

	public FilesUploadsStatuses storeFiles(List<MultipartFile> files) throws IOException {

		FilesUploadsStatuses statuses = new FilesUploadsStatuses();
		
		files.stream().forEach((file) -> {
			String fileName = file.getOriginalFilename();
			if  (fileName.contains("..")) {
				String message = "Sorry! This file contains invalid path";
				statuses.addFileStatus(fileName, message);
			} else {
				Path targetLocation = this.storageLocation.resolve(fileName);
				try {
					Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
					statuses.addFileStatus(fileName, "Uploaded:"+file.getSize());
				} catch (Exception e) {
					e.printStackTrace();
					statuses.addFileStatus(fileName, e.getMessage());
				}
			}
		}); 

		return statuses;
	}


	public FilesUploadsStatuses storeFiles(String sessionId, HttpServletRequest request) throws Exception {

		System.err.println("\tEvaluating incoming upload request....");

		FilesUploadsStatuses statuses = new FilesUploadsStatuses();
		
		ServletFileUpload upload = new ServletFileUpload();

		Path targetDirectory = this.storageLocation.resolve(sessionId);
		if (!Files.exists(targetDirectory)) {
			Files.createDirectory(targetDirectory);
		}
		
		FileItemIterator iter = upload.getItemIterator(request);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			String fileName = item.getName();
			if (!item.getFieldName().equals("file")) {
				throw new RuntimeException("Missing 'file' parameter in body");
			}
			System.err.println("\t\tname:"+fileName);
			if  (fileName.contains("..")) {
				statuses.addFileStatus(fileName,"Sorry! This file contains invalid path");
			}
			InputStream in = item.openStream();
			Path targetLocation = targetDirectory.resolve(fileName);
			OutputStream out = new FileOutputStream(targetLocation.toFile());
			if (!item.isFormField()) {
				System.err.println("\t\tbegin copying input file stream to output file stream....");
				byte[] buffer = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead=in.read(buffer))!=-1) {
					out.write(buffer, 0, bytesRead);
				}
				System.err.println("\t\tDone copying ");
			} else {
				System.err.println("form field from input stream: " + Streams.asString(in));
			}

			try {in.close(); } catch (Exception e) {}
			try {out.close(); } catch (Exception e) {}
			System.err.println("\t\tclosed streams");
			statuses.addFileStatus(fileName, "uploaded");
		}


		return statuses;
	}


	public FilesUploadsStatuses getUploadStatus(String sessionId) throws Exception {

		System.err.println("\tEvaluating incoming upload request....");

		FilesUploadsStatuses statuses = new FilesUploadsStatuses();
		
		Path targetDirectory = this.storageLocation.resolve(sessionId);
		
		if (!Files.exists(this.storageLocation.resolve(sessionId))) {
			statuses.setOverallMessage("Directory " + targetDirectory + " does NOT EXIST");
			return statuses;
		}

		File dir = targetDirectory.toFile();
		int numFiles = dir.list().length;
		if (numFiles > 0 ) {
			Stream.of(dir.list()).forEach((f) -> {
				File file = new File(dir.getPath()+"/"+f);
				statuses.addFileStatus(file.getName(), ""+file.length());
			});
		} else {
			statuses.setOverallMessage("Directory " + targetDirectory + " is EMPTY");
		}

		return statuses;
	}






}
