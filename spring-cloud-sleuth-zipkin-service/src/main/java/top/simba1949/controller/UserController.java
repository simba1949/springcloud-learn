package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:10
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
	/**
	 * 需要打印日志，才能看到链路追踪
	 * @param user
	 * @return
	 */
	@PostMapping
	public User getUser(@RequestBody User user, HttpServletRequest request){
		log.warn("the request's data of UserController-getUser is {}", user);
		user.setId(1L);
		user.setUsername("李白");
		user.setBirthday(new Date());
		log.info("X-B3-TraceId is {}, X-B3-SpanId is {}, X-B3-ParentSpanId is {}, X-B3-Sampled  is {}, X-Span-Name is {}",
				request.getHeader("X-B3-TraceId"),
				request.getHeader("X-B3-SpanId"),
				request.getHeader("X-B3-ParentSpanId"),
				request.getHeader("X-B3-Sampled"),
				request.getHeader("X-Span-Name")
		);

		return user;
	}
}
