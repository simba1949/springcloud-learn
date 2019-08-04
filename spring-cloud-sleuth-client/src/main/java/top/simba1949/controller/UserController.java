package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:20
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public User getUser(){
		log.info("the quest coming");
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		log.info("X-B3-TraceId is {}, X-B3-SpanId is {}, X-B3-ParentSpanId is {}, X-B3-Sampled  is {}, X-Span-Name is {}",
				request.getHeader("X-B3-TraceId"),
				request.getHeader("X-B3-SpanId"),
				request.getHeader("X-B3-ParentSpanId"),
				request.getHeader("X-B3-Sampled"),
				request.getHeader("X-Span-Name")
		);
		User user = new User();
		User result = userService.getUser(user);
		return result;
	}
}
