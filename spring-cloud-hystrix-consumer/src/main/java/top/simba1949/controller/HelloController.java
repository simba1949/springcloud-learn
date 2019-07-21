package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.service.HelloService;

/**
 * @author SIMBA1949
 * @date 2019/7/13 20:05
 */
@RestController
@RequestMapping("hello")
public class HelloController {

	@Autowired
	private HelloService helloService;

	@GetMapping("{name}")
	public String say(@PathVariable(value = "name")String name){
		String result = helloService.say(name);
		return result;
	}
}