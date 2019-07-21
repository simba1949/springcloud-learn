package top.simba1949.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author SIMBA1949
 * @date 2019/7/13 20:51
 */
@Service
public class HelloService {
	private static final String SERVICE_NAME = "http://SPRING-CLOUD-HYSTRIX-PROVIDER";
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "sayFallback")
	public String say(String name) {
		ResponseEntity<String> resp = restTemplate.getForEntity(SERVICE_NAME + "/hello/" + name, String.class);
		String body = resp.getBody();
		return body;
	}

	/**
	 * 异常处理/熔断处理 需要添加 Throwable 对象来接收异常信息
	 *
	 * @param name
	 * @param e
	 * @return
	 */
	public String sayFallback(String name, Throwable e){
		return "Dear " + name +", This is error result : " + e.getMessage();
	}
}
