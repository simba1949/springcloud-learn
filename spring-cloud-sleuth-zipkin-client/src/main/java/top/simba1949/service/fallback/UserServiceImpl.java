package top.simba1949.service.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:24
 */
@Slf4j
@Component
public class UserServiceImpl implements UserService {
	@Override
	public User getUser(User user) {
		log.warn("the request's data of fallback is {}", user);
		return null;
	}
}