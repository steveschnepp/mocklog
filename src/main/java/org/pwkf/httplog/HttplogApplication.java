package org.pwkf.httplog;

import org.pwkf.httplog.controller.MockDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class HttplogApplication {
	
	@Autowired
	MockDispatcherServlet dispatcherServlet;

	// We don't care about Spring Boot MVC
	@Bean(name = "dispatcherServlet")
	public DispatcherServlet getDispatcherServlet() {
		return dispatcherServlet;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(HttplogApplication.class, args);
	}

}
