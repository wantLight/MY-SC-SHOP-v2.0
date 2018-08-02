package com.imooc.myo2o.interceptor.shop;

import com.imooc.myo2o.entity.PersonInfo;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 店家管理系统登陆验证拦截器
 *
 * power by:xyzzg
 */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {
	//用户操作发生前
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Object userObj = request.getSession().getAttribute("user");
		if (userObj != null) {
			PersonInfo user = (PersonInfo) userObj;
			if (user != null && user.getUserId() != null
					&& user.getUserId() > 0 && user.getEnableStatus() == 1
					&& user.getShopOwnerFlag() == 1)
				//拦截器返回true，用户操作得以正常执行
				return true;
		}
		//不满足登陆验证，直接跳转到登陆界面
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<script>");
		out.println("window.open ('" + request.getContextPath()
				+ "/shop/ownerlogin','_self')");
		out.println("</script>");
		out.println("</html>");
		return false;
	}
}
