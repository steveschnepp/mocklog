package org.pwkf.mocklog.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping
public class MainController {

	@Value("${mocklog.incoming.dir}")
	String incomingDir;

	@Value("${mocklog.outgoing.dir}")
	String outgoingDir;

	@Value("${mocklog.outgoing.buffersize:4096}")
	int outgoingBufferSize;

	@RequestMapping(value = "/{path}", consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	void handle(@PathVariable String path, //
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("handle");

		path = sanitizePath(path);

		UUID requestUUID = dumpRequest(request, path);
		response.setHeader("X-Request-UUID", requestUUID.toString());

		// Now select what we should send
		String fileName = outgoingDir + "/" + path;
		{
			String fileNameHeaders = fileName + "." + request.getMethod();
			log.info("SEND - opening headers " + fileNameHeaders);

			File fileHeaders = new File(fileNameHeaders);
			if (fileHeaders.isFile()) {
				try (FileReader in = new FileReader(fileHeaders)) {
					try (BufferedReader reader = new BufferedReader(in)) {
						String line;
						while ((line = reader.readLine()) != null) {
							String[] splittedLine = line.split(": ", 2);

							String headerKey = splittedLine[0];

							String headerValue = (splittedLine.length == 2) ? splittedLine[1] : "";

							log.info("header " + headerKey + "/" + headerValue);

							if (headerKey.equals("__STATUS__")) {
								response.setStatus(Integer.valueOf(headerValue));
								// Do not send this header
								continue;
							}

							response.setHeader(headerKey, headerValue);
						}
					}
				}
			} else {
				log.info("SEND - header file not found :" + fileNameHeaders);
			}
		}

		// Sends the body
		{
			log.info("SEND - opening body " + fileName);

			File file = new File(fileName);
			if (file.isFile()) {
				Path filepath = Paths.get(fileName);
				long filesize = Files.size(filepath);
				response.setHeader("Content-Length", String.valueOf(filesize));

				try (FileInputStream inputStream = new FileInputStream(file)) {
					try (OutputStream outStream = response.getOutputStream()) {
						copyStream(inputStream, outStream, outgoingBufferSize);
					}
				}
			} else {
				log.info("SEND - body file not found :" + fileName);
			}
		}

		response.flushBuffer();

		return;
	}

	private UUID dumpRequest(HttpServletRequest request, String path) throws IOException, FileNotFoundException {
		// Create a new directory
		Path dir = Paths.get(incomingDir + "/" + path);
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

			log.info("RECV - opening headers " + fileNameHeaders);
			try (FileWriter out = new FileWriter(fileNameHeaders)) {
				try (BufferedWriter writer = new BufferedWriter(out)) {
					Enumeration<String> headerNames = request.getHeaderNames();
					while (headerNames.hasMoreElements()) {
						String headerName = headerNames.nextElement();
						Enumeration<String> headerValues = request.getHeaders(headerName);
						while (headerValues.hasMoreElements()) {
							String headerValue = headerValues.nextElement();
							writer.write(headerName + ": " + headerValue + "\n");
							log.info("RECV - " + headerName + ": " + headerValue);
						}
					}
				}
			}
		}

		// Dump the Body
		log.info("RECV - opening body " + fileName);
		try (InputStream inputStream = request.getInputStream()) {
			try (FileOutputStream outStream = new FileOutputStream(fileName)) {
				copyStream(inputStream, outStream, outgoingBufferSize);
			}
		}

		return requestUUID;
	}

	static String sanitizePath(String pathUrl) {
		if (pathUrl == null)
			pathUrl = "";

		// Sanitize
		String pathParts[] = pathUrl.split("/");
		{
			for (int i = 0; i < pathParts.length; i++) {
				String pathPart = pathParts[i];
				if (pathPart.equals(".") || pathPart.equals("..")) {
					// Escape to avoid directory traversal
					pathParts[i] = "__" + pathPart + "__";
				}
			}
		}
		String path = String.join("/", pathParts);

		return path;
	}

	static long copyStream(InputStream is, OutputStream os, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int bytesRead = -1;
		long bytesReadTotal = 0;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = is.read(buffer)) != -1) {
			log.fine("copyStream - bytesReadTotal:" + bytesReadTotal + " bytesRead:" + bytesRead);
			os.write(buffer, 0, bytesRead);
			bytesReadTotal += bytesRead;
		}

		log.info("copyStream - bytesReadTotal:" + bytesReadTotal);
		return bytesReadTotal;
	}
}
