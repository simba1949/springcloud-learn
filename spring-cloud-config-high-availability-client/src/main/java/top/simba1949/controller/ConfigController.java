package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RestController
@RequestMapping("config")
public class ConfigController {

	@Value("${from}")
	private String from;

	@GetMapping
	public String getFromValue(){
		System.out.println("分布式配置文件客户端");
		return from;
	}
}
