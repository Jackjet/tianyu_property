package com.vguang.shiro.filter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class MyRolesFilter extends AuthorizationFilter {
	private static final Logger log = LoggerFactory.getLogger(MyRolesFilter.class);

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		log.info("===isAccessAllowed===");
//		Subject subject = getSubject(request, response);
		
		Gson gson = new Gson();
		Subject subject = ThreadContext.getSubject();
        if (subject == null) {
        	log.info("Roles Subject需要重新创建");
            subject = (new Subject.Builder()).buildSubject();
            ThreadContext.bind(subject);
        }
        //测试
        Session session = subject.getSession(false);
        List<PrincipalCollection> prins = null;
        if (session != null) {
        	log.info("Subject:session:{}", session.getId());
        	prins = (List<PrincipalCollection>) session.getAttribute(DelegatingSubject.class.getName() + ".RUN_AS_PRINCIPALS_SESSION_KEY");
        }
        log.info("Subject:prins:{}", gson.toJson(prins));
        log.info("Subject:prin:{}", gson.toJson(subject.getPrincipal()));
		
		String[] rolesArray = (String[]) mappedValue;
		if (rolesArray == null || rolesArray.length == 0) {
			return true;
		}
		log.info("请求路径url角色：{}", gson.toJson(rolesArray));
		Set<String> roles = CollectionUtils.asSet(rolesArray);
		log.info("roles====="+roles);
		boolean flag = subject.hasAllRoles(roles);

		log.info("===isAccessAllowed End===" + flag);
		//过滤掉登录的
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		String requestUrl = httpRequest.getRequestURI();
//		 log.info("flag==="+flag);
//		if(requestUrl.contains("/login")) {
//			flag= true;	
//			log.info("flag000000"+flag);
//		}
		return flag;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		log.info("===onAccessDenied===");

//		Subject subject = getSubject(request, response);
		
		Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            subject = (new Subject.Builder()).buildSubject();
            ThreadContext.bind(subject);
        }
        
		if (null == subject) {
			log.info("Subject:{}", (subject == null));
		} else {
			log.info("Subject Sessionid:{}", subject.getSession().getId());
			log.info("Principal:{}", (subject.getPrincipal() == null));

			//如果subject未认证，则重定向到登录url
			if (subject.getPrincipal() == null) {
				log.info("RolesFilter 跳转到登录url");
				saveRequestAndRedirectToLogin(request, response);
			} else {
				log.info("RolesFilter 跳转到未认证url:{}", new Gson().toJson(subject.getPrincipal()));
				log.info("跳转500或601：{}", request.getContentType());
				String unauthorizedUrl = getUnauthorizedUrl();
				if (StringUtils.hasText(unauthorizedUrl)) {
//					WebUtils.issueRedirect(request, response, unauthorizedUrl);
					
					HttpServletResponse resp = ((HttpServletResponse) response);
					resp.setStatus(603);
					JSONObject json = new JSONObject();
					resp.setCharacterEncoding("UTF-8");
					try {
						json.put("success", false);
						json.put("code", 603);
						json.put("message", "授权失败");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					resp.getWriter().print(json);
				} else {
					WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//		return super.onPreHandle(request, response, mappedValue);
		
		return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response, mappedValue);
	}

	
}
