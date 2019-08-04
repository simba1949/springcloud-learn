package top.simba1949.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.simba1949.common.User;
import top.simba1949.service.fallback.UserServiceImpl;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:21
 */
@FeignClient(value = "spring-cloud-sleuth-zipkin-service", fallback = UserServiceImpl.class)
public interface UserService {
	/**
	 * 获取 user
	 *
	 * 传输对象需要使用 Post 请求
	 * @param user
	 * @return
	 */
	@PostMapping(value = "user")
	User getUser(@RequestBody User user);
}
