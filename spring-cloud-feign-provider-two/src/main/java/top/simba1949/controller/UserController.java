package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:30
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

	private List<UserDTO> list = new ArrayList<>();

	@PostConstruct
	public void init(){
		for (int i = 0; i < 10; i++){
			UserDTO userDTO = new UserDTO();
			userDTO.setId(i);
			userDTO.setBirthday(new Date(System.currentTimeMillis() + i * (24 * 60 * 60 *1000)));
			userDTO.setUsername("libai-" + i);

			list.add(userDTO);
		}
	}

	@GetMapping("list")
	public List<UserDTO> userList(){
		log.info("spring-cloud-feign-provider-two");
		return list;
	}

	@PostMapping("get")
	public UserDTO get(@RequestBody UserDTO userDTO){
		log.info("spring-cloud-feign-provider-two");
		return list.get(userDTO.getId());
	}

	@PostMapping
	public String insert(@RequestBody UserDTO userDTO){
		log.info("spring-cloud-feign-provider-two");
		userDTO.setId(list.size());
		list.add(userDTO);
		return "SUCCESS";
	}

	@PutMapping
	public String update(@RequestBody UserDTO userDTO){
		log.info("spring-cloud-feign-provider-two");
		list.remove(userDTO.getId());
		list.add(userDTO.getId(), userDTO);
		return "SUCCESS";
	}

	@DeleteMapping
	public String delete(Integer id){
		log.info("spring-cloud-feign-provider-two");
		list.remove(id);
		list.add(id, null);
		return "SUCCESS";
	}

}
