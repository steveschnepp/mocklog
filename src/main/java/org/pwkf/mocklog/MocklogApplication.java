package org.pwkf.mocklog;

import org.pwkf.mocklog.controller.MockDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class MocklogApplication {
	
	@Autowired
	MockDispatcherServlet dispatcherServlet;

	// We don't care about Spring Boot MVC
	@Bean(name = "dispatcherServlet")
	public DispatcherServlet getDispatcherServlet() {
		return dispatcherServlet;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(MocklogApplication.class, args);
	}

}
