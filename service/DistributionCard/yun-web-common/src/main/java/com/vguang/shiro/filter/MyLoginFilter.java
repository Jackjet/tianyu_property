package com.vguang.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyLoginFilter extends FormAuthenticationFilter {
	private static final Logger log = LoggerFactory.getLogger(MyLoginFilter.class);
	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		// return super.onPreHandle(request, response, mappedValue);

		return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response, mappedValue);
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		// 重写使登陆成功后不跳转
//		super.onLoginSuccess(token, subject, request, response);
		return false;
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		return super.onLoginFailure(token, e, request, response);
	}

	/**
	 * 登录接口必须走这里
	 */
	@Override
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		// return super.isLoginRequest(request, response);
		String requestUrl = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
		log.info("登录接口必须走这里=="+requestUrl);
		boolean flag = false;
		switch (requestUrl) {
		case "/wxapp/webloginconfirm":
			flag = true;
			break;
		case "/wxapp/orgloginconfirm":
			flag = true;
			break;
		case "/man/accountlogin":
			flag = true;
			break;
		case "/orgman/orgaccountlogin":
			flag = true;
			break;
		case "/orgman/device/checkdeviceidentity":
			flag = true;
			break;
		case "/orgman/device/updatedeviceidentity":
			flag = true;
			break;
					
		case "/tencent/login":
			flag = true;
			break;
		case "/facilitator/ordinaryuser/login":
			flag = true;
			log.info("登录接口必须走这里11=="+requestUrl);
			break;	
		default:
			flag = pathsMatch(getLoginUrl(), request);
		}

		System.out.println("loginURL:" + getLoginUrl());
		System.out.println("requestURL:" + requestUrl);
		return flag;
	}

//	@Override
//	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//		// return super.isAccessAllowed(request, response, mappedValue);
//		
//		Subject subject = getSubject(request, response);
//		boolean flag = subject.isAuthenticated();
//		
//		return flag || (!isLoginRequest(request, response) && isPermissive(mappedValue));
//	}

}
