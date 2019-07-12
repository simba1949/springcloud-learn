package top.simba1949.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SIMBA1949
 * @date 2019/7/12 10:06
 */
@RestController
@RequestMapping("hello")
public class HelloController {

	private Logger logger = LoggerFactory.getLogger(HelloController.class);

	@GetMapping
	public String say(String name){
		logger.info("name=" + name);
		return "Hello " + name;
	}
}
