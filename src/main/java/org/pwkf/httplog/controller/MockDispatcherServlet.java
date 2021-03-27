package org.pwkf.httplog.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import lombok.extern.java.Log;

@SuppressWarnings("serial")
@Log
@Component
public class MockDispatcherServlet extends DispatcherServlet {
	@Value("${org.pwkf.httplog.incoming.dir}")
	String incomingDir;

	@Value("${org.pwkf.httplog.outgoing.dir}")
	String outgoingDir;

	@Value("${org.pwkf.httplog.outgoing.buffersize:4096}")
	int outgoingBufferSize;

	static {
		log.info("start");
	}

	@Autowired
	MainController mainController;

	public MockDispatcherServlet() {
		super();
	}

	public MockDispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
	}

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("doService");
		
		String path = request.getServletPath();
		path = path.replaceFirst("/", "");
		
		mainController.handle(path, request, response);
	}
}
