package org.pwkf.httplog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping(value = "")
public class MainController {

    @GetMapping
    @ResponseBody
	String root() {
	log.info("ok");
	return "ok";
    }
}
