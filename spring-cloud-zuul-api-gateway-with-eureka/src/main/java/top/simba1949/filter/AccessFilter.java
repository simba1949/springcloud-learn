package top.simba1949.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SIMBA1949
 * @date 2019/7/27 22:51
 */
public class AccessFilter extends ZuulFilter {
	/**
	 *  filterType ：过滤器的类型，它决定过滤器在请求的哪个生命周期中执行。
	 *  这里定义 pre ，代表会在请求路由之前执行
	 *
	 * @return
	 */
	@Override
	public String filterType() {
		return "pre";
	}

	/**
	 * filterOrder ： 过滤器的执行顺序。
	 * 当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值来依次执行
	 *
	 * @return
	 */
	@Override
	public int filterOrder() {
		return 0;
	}

	/**
	 * shouldFilter ：判断该过滤器是否需要被执行。
	 *
	 * @return
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * run ： 过滤器的具体逻辑。
	 *
	 * 这里可以通过 currentContext.setSendZuulResponse(false);  令 zuul 过滤该请求，不对其进行路由
	 * 通过 currentContext.setResponseStatusCode(401); 设置返回的错误码
	 * 通过 currentContext.setResponseBody("错误请求"); 对返回的 body 内容进行编辑
	 *
	 * RequestContext currentContext = RequestContext.getCurrentContext();
	 * currentContext.setSendZuulResponse(false);
	 * currentContext.setResponseStatusCode(401);
	 * currentContext.setResponseBody("错误请求");
	 *
	 * @return
	 * @throws ZuulException
	 */
	@Override
	public Object run() throws ZuulException {
		RequestContext currentContext = RequestContext.getCurrentContext();
		HttpServletRequest request = currentContext.getRequest();
		System.out.println("请求方式：" + request.getMethod() + "；请求的 URL:" + request.getRequestURL().toString());
		String token = request.getParameter("token");
		if (null == token){
			currentContext.setSendZuulResponse(false);
			currentContext.setResponseStatusCode(401);
			currentContext.setResponseBody("错误请求");
		}

		return null;
	}
}
