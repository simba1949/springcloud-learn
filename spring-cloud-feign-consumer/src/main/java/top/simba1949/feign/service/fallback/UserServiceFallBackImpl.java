package top.simba1949.feign.service.fallback;

import org.springframework.stereotype.Component;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.UserService;

import java.util.List;

/**
 * 服务降级逻辑，需要实现对应的 Feign 接口
 *
 * @author SIMBA1949
 * @date 2019/7/21 15:19
 */
@Component
public class UserServiceFallBackImpl implements UserService {
	@Override
	public List<UserDTO> userList() {
		System.err.println("君不见黄河之水天上来");
		return null;
	}

	@Override
	public UserDTO get(UserDTO userDTO) {
		return null;
	}

	@Override
	public String insert(UserDTO userDTO) {
		return null;
	}

	@Override
	public String update(UserDTO userDTO) {
		return null;
	}

	@Override
	public String delete(Integer id) {
		return null;
	}
}
