package com.example.demo;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
//@RequestMapping("/")
public class FileUploadRestController {


	@Autowired
	private FileStorageService service;
	
	public RootResponse hello(HttpServletRequest request, HttpServletResponse response) {
		return new RootResponse("This is a File Upload Service");
	}


	@GetMapping("/sysprops")
	public Properties sysprops() {
		System.getenv();
		return System.getProperties(); 
	}

	@GetMapping("/env")
	public Map<String,String> env() {
		return System.getenv();
	}


	/**********************************************************************************************
	 * this version will not work for huge files that require giving feedback of upload progress.
	 * the main issue is that it requires 'spring.servlet.multipart.enabled=true' in application.properties.
	 * it means that Spring is handling the actual copying, because it first streams/copies the remote
	 * file into a temp directory, before running the Files.copy() command to copy it to its final
	 * location.
	 * 
	 * Note - if you read nothing else - with this approach ('/file'), by the time you see 
	 * 'START of /file upload' in  Eclipse console, the temp file has already been created.
	 * and it took a looooong time with a 2GB file.
	 * 
	 * at first glance, a possible solution would be to:
	 * 1) configure tomcat to a specific,known temp directory (see 'server.tomcat.basedir'),
	 * 2) have a separate thread the monitors and reports increase in number of bytes of temp file on disk
	 * however, the temp file names are generated, and I haven't figured out to control that..
	 * also, maybe keep modifying the name of the temp directory, unique per file upload request.
	 * but - that's a system property, and changing on the fly may not be such a great idea, if
	 * we're talking about simultaneous file upload requests (say from different clients/users)
	 * also - this means the file is copied twice.  once from remote to temp, once from temp to final.
	 * 
	 * the essential problem is that this request's parameter is a multipart file, which means that
	 * Spring has alread intercepted / handled this before we arrive here.
	 */
	@PostMapping("/simplefileupload")
	public SimpleFileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

		System.err.println("\n\nSTART of  /simplefileupload \n\n\n");

		String name = service.storeFile(file);

		return new SimpleFileUploadResponse(name,file.getSize());
	}


	@PostMapping("/betterfileupload")
	public FilesUploadsStatuses uploadFile2(HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.err.println("\n\nSTART of  /betterfileupload");
		
		UUID uuid = UUID.randomUUID();
		response.addHeader("SESSIONID", uuid.toString());

		System.err.println("\tuuid="+uuid);
		
		FilesUploadsStatuses results = null;

		if (ServletFileUpload.isMultipartContent(request)) {

			System.err.println("\tfile upload version 3 - request is multipart");

			results = service.storeFiles(uuid.toString(),request);
					
		} else {
			throw new RuntimeException("/file3 request is not multipart file");
		};
	
		return results;

	}


	@GetMapping("/sessionid")
	public FilesUploadsIdentifier sessionId() {
		UUID uuid = UUID.randomUUID();
		FilesUploadsIdentifier identifier = new FilesUploadsIdentifier(uuid.toString());
		return identifier;
	}

	@PostMapping("/muchbetterfileupload")
	public FilesUploadsStatuses uploadFile3(HttpServletRequest httpreq, HttpServletResponse httpres) throws Exception {

		System.err.println("\n\nSTART of  /muchbetterfileupload");
		
		
		String sessionId = httpreq.getHeader("SESSIONID");
		if (sessionId == null) {
			throw new RuntimeException("Missing SESSIONID");
		}

		FilesUploadsStatuses statuses = null;
		if (ServletFileUpload.isMultipartContent(httpreq)) {

			System.err.println("\tfile upload version 3 - request is multipart");

			statuses = service.storeFiles(sessionId,httpreq);
	

		} else {
			throw new RuntimeException("/file3 request is not multipart file");
		};
	
		return statuses;

	}


	@GetMapping("/uploadprogress")
	public FilesUploadsStatuses progress(HttpServletRequest httpreq, HttpServletResponse httpres) throws Exception {
		String sessionId = httpreq.getHeader("SESSIONID");
		if (sessionId == null) {
			throw new RuntimeException("Missing SESSIONID");
		}

		FilesUploadsStatuses statuses = service.getUploadStatus(sessionId);
		return statuses;
	}

}
