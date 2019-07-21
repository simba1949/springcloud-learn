package top.simba1949.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SIMBA1949
 * @date 2019/7/13 19:42
 */
@RestController
@RequestMapping("hello")
public class HelloController {

	@GetMapping("{name}")
	public String say(@PathVariable(value = "name") String name){
		System.out.println(name);
		return "Hello " + name;
	}
}
