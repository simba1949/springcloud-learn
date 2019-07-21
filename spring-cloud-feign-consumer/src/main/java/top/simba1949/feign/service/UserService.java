package top.simba1949.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.fallback.UserServiceFallBackImpl;

import java.util.List;

/**
 * GetMapping/PostMapping/PutMapping/DeleteMapping 必须写写全
 * @FeignClient(name = "SPRING-CLOUD-FEIGN-PROVIDER", fallback = UserServiceFallBackImpl.class)
 * 				name 表示服务名
 * 				fallback 表示服务降级逻辑的实现类
 *
 * @author SIMBA1949
 * @date 2019/7/21 13:58
 */
@FeignClient(name = "SPRING-CLOUD-FEIGN-PROVIDER", fallback = UserServiceFallBackImpl.class)
public interface UserService {

	@GetMapping("user/list")
	List<UserDTO> userList();

	/**
	 * 如果需要传输对象，必须使用 @RequestBody，服务提供者使用 Post 请求接收
	 * @param userDTO
	 * @return
	 */
	@GetMapping("user/get")
	UserDTO get(@RequestBody UserDTO userDTO);

	@PostMapping("user")
	String insert(@RequestBody UserDTO userDTO);

	@PutMapping("user")
	String update(@RequestBody UserDTO userDTO);

	@DeleteMapping("user")
	String delete(@RequestParam("id") Integer id);
}
