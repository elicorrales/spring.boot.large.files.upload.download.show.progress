package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
//@RequestMapping("/")
public class FileUploadRestController {

	private  static final Logger logger = LoggerFactory.getLogger(FileUploadRestController.class);

	@Autowired
	private FileStorageService service;
	
	public RootResponse hello() {
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
	@PostMapping("/file")
	public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

		System.err.println("\n\nSTART of  /file upload\n\n\n");

		String fileName = service.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/")
				.path(fileName)
				.toUriString();
		
		return new FileUploadResponse(fileName, fileDownloadUri,
				file.getContentType(), file.getSize());

	}

	/**********************************************************************************************
	 * this approach has all the same failings and problems as the first(above) approach.
	 * the only positive in this approach, that perhaps could be used elsewhere with another way,
	 * is that instead of using Files.copy(), we copy input stream to output stream, using a buffer
	 * of size that we choose.  this means we could add in some monitoring of progress.
	 */
	@PostMapping("/file2")
	public FileUploadResponse uploadFile2(@RequestParam("file") MultipartFile file) throws IOException {

		System.err.println("\n\nSTART of  /file2 upload\n\n\n");

		String fileName = service.storeFile2(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/")
				.path(fileName)
				.toUriString();
		
		return new FileUploadResponse(fileName, fileDownloadUri,
				file.getContentType(), file.getSize());

	}

	

	@PostMapping("/file3")
	public FileUploadResponse uploadFile3(HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.err.println("\n\nSTART of  /file3 upload\n\n\n");

		if (ServletFileUpload.isMultipartContent(request)) {

			System.err.println("\n\n\n file upload version 3 - request is multipart...\n\n\n");

			service.storeFile3(request);
					
		};
	
		return new FileUploadResponse("FileName", "URI", "ContentType", 100);

	}


}
