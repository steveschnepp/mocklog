package org.pwkf.httplog.controller;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping(value = "")
public class MainController {

	@Value("${org.pwkf.httplog.incoming.dir}")
	String incomingDir;

	@Value("${org.pwkf.httplog.outgoing.dir}")
	String outgoingDir;

	@RequestMapping(value = "")
	void handle(HttpServletRequest request, HttpServletResponse response, @Nullable @RequestBody byte[] data) throws Exception {
		log.info("handle");

		String path = request.getPathInfo();
		if (path == null)
			path = "";

		// Sanitize
		{
			String pathParts[] = path.split("/");
			for (int i = 0; i < pathParts.length; i++) {
				String pathPart = pathParts[i];
				if (pathPart.equals(".") || pathPart.equals("..")) {
					// Escape to avoid directory traversal
					pathParts[i] = "__" + pathPart + "__";
				}
			}
		}

		// Create a new directory
		Path dir = Paths.get(incomingDir + path);
		try {
			Files.createDirectories(dir);
		} catch (FileAlreadyExistsException e) {
			log.info(dir + " already exists");
		}

		// Dump the Headers
		UUID requestUUID = UUID.randomUUID();
		String fileName = incomingDir + "/" + path + "/" + requestUUID;
		{

			String fileNameHeaders = fileName + "." + request.getMethod();

			log.info("opening headers " + fileNameHeaders);
			try (FileWriter out = new FileWriter(fileNameHeaders)) {
				try (BufferedWriter writer = new BufferedWriter(out)) {
					Enumeration<String> headerNames = request.getHeaderNames();
					while (headerNames.hasMoreElements()) {
						String headerName = headerNames.nextElement();
						Enumeration<String> headerValues = request.getHeaders(headerName);
						while (headerValues.hasMoreElements()) {
							String headerValue = headerValues.nextElement();
							writer.write(headerName + ": " + headerValue + "\n");
							log.info(headerName + ": " + headerValue);
						}
					}
				}
			}
		}

		// Dump the Body
		if (data != null){
			log.info("opening body " + fileName);
			try (FileOutputStream out = new FileOutputStream(fileName)) {
				out.write(data);
			}
		}

		// Now select what we should send


		return;
	}
}
