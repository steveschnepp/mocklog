package org.pwkf.httplog.controller;

import org.springframework.boot.test.context.SpringBootTest;

import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
public class MainControllerTest extends Specification {

	@Unroll
	def "sanitize all the . & .. directories in the path"(String p, String s) {
		expect:
		MainController.sanitizePath(p) == s

		where:
		p 			|| s
		null		|| ""
		"" 			|| ""
		"/" 		|| ""
		"/.." 		|| "/__..__"
		"/.././" 	|| "/__..__/__.__"
		"/.../toto" || "/.../toto"
	}
}
