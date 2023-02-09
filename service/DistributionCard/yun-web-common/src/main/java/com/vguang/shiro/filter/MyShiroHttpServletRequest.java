package com.vguang.shiro.filter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.session.Session;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.util.WebUtils;

public class MyShiroHttpServletRequest extends ShiroHttpServletRequest{

	public MyShiroHttpServletRequest(HttpServletRequest wrapped, ServletContext servletContext, boolean httpSessions) {
		super(wrapped, servletContext, httpSessions);
	}

	@Override
	public String getRequestedSessionId() {
//		return super.getRequestedSessionId();
		System.out.println("===getRequestedSessionId===");
		String requestedSessionId = null;
        if (isHttpSessions()) {
            requestedSessionId = super.getRequestedSessionId();
        } else {
            Object sessionId = getAttribute(REFERENCED_SESSION_ID);
            if (sessionId != null) {
                requestedSessionId = sessionId.toString();
            }
        }

        return requestedSessionId;
	}
	
	@Override
	public HttpSession getSession(boolean create) {
		// return super.getSession(create);
		System.out.println("===Request getSession===");
		HttpSession httpSession;

		if (isHttpSessions()) {
			System.out.println("Request isHttpSessions():" + true);
			httpSession = super.getSession(false);
			if (httpSession == null && create) {
				// Shiro 1.2: assert that creation is enabled (SHIRO-266):
				if (WebUtils._isSessionCreationEnabled(this)) {
					httpSession = super.getSession(create);
				} else {
					System.out.println("===创建session异常===");
				}
			}
		} else {
			System.out.println("Request isHttpSessions():" + false);
			if (this.session == null) {
				System.out.println("===初始session为空===");
				boolean existing = getSubject().getSession(false) != null;

				Session shiroSession = getSubject().getSession(create);
				if (shiroSession != null) {
					this.session = new ShiroHttpSession(shiroSession, this, this.servletContext);
					if (!existing) {
						setAttribute(REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
					}
				}
			}
			httpSession = this.session;
		}

		return httpSession;
	}
	

}
