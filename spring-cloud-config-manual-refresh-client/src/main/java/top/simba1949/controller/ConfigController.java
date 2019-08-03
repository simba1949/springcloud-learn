package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RefreshScope 刷新该类的配置信息数据
 *
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RefreshScope
@RestController
@RequestMapping("config")
public class ConfigController {

	@Value("${name}")
	private String name;

	@GetMapping
	public String getFromValue(){
		System.out.println("分布式配置文件客户端");
		return name;
	}
}
