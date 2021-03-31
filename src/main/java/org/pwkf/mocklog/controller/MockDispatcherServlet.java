package org.pwkf.mocklog.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import lombok.extern.java.Log;

@SuppressWarnings("serial")
@Log
@Component
public class MockDispatcherServlet extends DispatcherServlet {

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
