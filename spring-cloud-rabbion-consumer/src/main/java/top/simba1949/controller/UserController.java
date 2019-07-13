package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.simba1949.common.UserDTO;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SIMBA1949
 * @date 2019/7/12 15:59
 */
@RestController
@RequestMapping("user")
public class UserController {

	private static final String SERVICE_NAME = "http://SPRING-CLOUD-RABBION-PROVIDER";
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("test")
	public String test(){
		return "SUCCESS";
	}

	@GetMapping("list")
	public List<UserDTO> userList(){
		ResponseEntity<List> resp = restTemplate.getForEntity(SERVICE_NAME + "/user/list", List.class);
		List body = resp.getBody();

		List list = restTemplate.getForObject(SERVICE_NAME + "/user/list", List.class);
		return body;
	}

	@GetMapping("get")
	public UserDTO get(UserDTO userDTO){
		Map map = new HashMap<>(16);
		map.put("id", userDTO.getId());
		map.put("username", userDTO.getUsername());
		// 通过可变参数传递参数
		ResponseEntity<UserDTO> resp = restTemplate.getForEntity(SERVICE_NAME + "/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
		// 通过 Map 传递参数
		ResponseEntity forEntity = restTemplate.getForEntity(SERVICE_NAME + "/user/get?id={id}&username={username}", UserDTO.class, map);

		UserDTO userDTO1 = restTemplate.getForObject(SERVICE_NAME + "/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
		UserDTO userDTO2 = restTemplate.getForObject(SERVICE_NAME + "/user/get?id={id}&username={username}", UserDTO.class, map);
		return null;
	}

	@PostMapping
	public String insert(@RequestBody UserDTO userDTO){
		ResponseEntity<UserDTO> resp = restTemplate.postForEntity(SERVICE_NAME + "/user/", userDTO, UserDTO.class);

		UserDTO dto = restTemplate.postForObject(SERVICE_NAME + "/user/", userDTO, UserDTO.class);

		URI uri = restTemplate.postForLocation(SERVICE_NAME + "/user/", userDTO, UserDTO.class);
		return "SUCCESS";
	}

	@PutMapping
	public String update(@RequestBody UserDTO userDTO){
		restTemplate.put(SERVICE_NAME + "/user/", userDTO, UserDTO.class);
		return "SUCCESS";
	}

	@DeleteMapping
	public String delete(Integer id){
		restTemplate.delete(SERVICE_NAME + "/user?id={1}", id);
		return "SUCCESS";
	}
}
