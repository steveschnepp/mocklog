package org.pwkf.httplog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping(value = "")
public class MainController {

    @GetMapping
	String root() {
	log.info("ok");
	return "ok";
	}
}
