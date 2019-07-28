package top.simba1949.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.simba1949.filter.AccessFilter;

/**
 * @author SIMBA1949
 * @date 2019/7/27 23:03
 */
@Configuration
public class FilterConfig {
	/**
	 * 在实现自定义过滤器之后，他并不会直接生效，需要为其创建具体的 bean 才能启动该过滤器
	 * @return
	 */
	@Bean
	public AccessFilter accessFilter(){
		return new AccessFilter();
	}
}
