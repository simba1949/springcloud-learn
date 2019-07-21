package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.UserService;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/21 13:57
 */
@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("list")
	public List<UserDTO> userList(){
		return userService.userList();
	}

	@GetMapping("get")
	public UserDTO get(UserDTO userDTO){
		return userService.get(userDTO);
	}

	@PostMapping
	public String insert(@RequestBody UserDTO userDTO){
		return userService.insert(userDTO);
	}

	@PutMapping
	public String update(@RequestBody UserDTO userDTO){
		return userService.update(userDTO);
	}

	@DeleteMapping
	public String delete(Integer id){
		return userService.delete(id);
	}
}
