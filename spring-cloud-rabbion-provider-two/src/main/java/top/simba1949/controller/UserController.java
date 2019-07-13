package top.simba1949.controller;

import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:30
 */
@RestController
@RequestMapping("user")
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
		return list;
	}

	@GetMapping("get")
	public UserDTO get(UserDTO userDTO){
		return list.get(userDTO.getId());
	}

	@PostMapping
	public String insert(@RequestBody UserDTO userDTO){
		userDTO.setId(list.size());
		list.add(userDTO);
		return "SUCCESS";
	}

	@PutMapping
	public String update(@RequestBody UserDTO userDTO){
		list.remove(userDTO.getId());
		list.add(userDTO.getId(), userDTO);
		return "SUCCESS";
	}

	@DeleteMapping
	public String delete(Integer id){
		list.remove(id);
		list.add(id, null);
		return "SUCCESS";
	}

}
