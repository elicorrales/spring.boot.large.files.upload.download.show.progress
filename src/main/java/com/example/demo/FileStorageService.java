package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path publicFinalFileStorageLocation;
	
	
	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProps) throws IOException {
		this.publicFinalFileStorageLocation =
				Paths.get(fileStorageProps.getUploadDir())
					.toAbsolutePath().normalize();
	
		if (!Files.exists(this.publicFinalFileStorageLocation)) {
			Files.createDirectory(this.publicFinalFileStorageLocation);
		}
	}

	public String storeFile(MultipartFile file) throws IOException {

		String n = file.getName();
		String fileName = file.getOriginalFilename();
		if  (fileName.contains("..")) {
			throw new FileStorageException("Sorry! This file contains invalid path: [ " + fileName + " ] ");
		}
		
		Path targetLocation = this.publicFinalFileStorageLocation.resolve(fileName);
		Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}


	public String storeFile2(MultipartFile file) throws IOException {

		System.err.println("\n\nEvaluating incoming upload request....");

		String n = file.getName();
		String fileName = file.getOriginalFilename();
		if  (fileName.contains("..")) {
			throw new FileStorageException("Sorry! This file contains invalid path: [ " + fileName + " ] ");
		}
	
		Path targetLocation = this.publicFinalFileStorageLocation.resolve(fileName);

		byte[] buffer = new byte[4096];
		
		OutputStream out = new FileOutputStream(targetLocation.toFile());
		InputStream  in  = file.getInputStream();
	
		System.err.println("\n\nbegin copying input file stream to output file stream....");

		int bytesRead = 0;
		while ((bytesRead=in.read(buffer))!=-1) {
			System.err.print(".");
			out.write(buffer, 0, bytesRead);
		}

		System.err.println("\n\nDone copying ");

		in.close();
		out.close();

		//Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}


	public String storeFile3(HttpServletRequest request) throws Exception {

		System.err.println("\n\nEvaluating incoming upload request....");

		Path targetLocation = this.publicFinalFileStorageLocation.resolve("TempFile");

		ServletFileUpload upload = new ServletFileUpload();
		
		FileItemIterator iter = upload.getItemIterator(request);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			System.err.println("name:"+item.getName());
			System.err.println("field:"+item.getFieldName());
			System.err.println("type:"+item.getContentType());
			System.err.println("headers"+item.getHeaders());
			InputStream in = item.openStream();
			OutputStream out = new FileOutputStream(targetLocation.toFile());
			System.err.println("\n\nbegin copying input file stream to output file stream....");
			byte[] buffer = new byte[4096];
			int bytesRead = 0;
			while ((bytesRead=in.read(buffer))!=-1) {
				out.write(buffer, 0, bytesRead);
			}
			System.err.println("\n\nDone copying ");
			in.close();
			out.close();
		}

/*
		InputStream  in  = request.getInputStream();
		OutputStream out = new FileOutputStream(targetLocation.toFile());
	
		System.err.println("\n\nbegin copying input file stream to output file stream....");

		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		while ((bytesRead=in.read(buffer))!=-1) {
			out.write(buffer, 0, bytesRead);
		}

		System.err.println("\n\nDone copying ");

		in.close();
		out.close();
*/

		return "TempFile";
	}




}
