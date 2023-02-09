package com.vguang.shiro.filter;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vguang.system.SpringContextUtil;
import com.vguang.utils.JedisManager;

public class MyShiroFilter extends AbstractShiroFilter {
	private static final Logger log = LoggerFactory.getLogger(MyShiroFilter.class);

	// @Autowired
	private JedisManager jedisManager = (JedisManager) SpringContextUtil.getBean("jedisManager");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws ServletException, IOException {
		// 重写
		HttpServletRequest hsr = ((HttpServletRequest) servletRequest);
		String rsessionid = hsr.getSession().getId();
		log.info("===doFilterInternal===" + rsessionid);
		String wxsid = hsr.getParameter("wxsid");
		String webauthid = hsr.getParameter("webauthid");
		String websid = hsr.getParameter("websid");
		log.info("ShiroFilter接收初始参数：webauthid:{},wxsid:{},websid:{}", webauthid, wxsid, websid);
		
		//小程序第一次请求认证wxsid--uid ==> websuthid--uid
		if((null != webauthid && !"undefined".equals(webauthid))
				&& (null != wxsid && !"undefined".equals(wxsid))){
			if(jedisManager.existsStr(1, wxsid)){
				jedisManager.setValueByStr(1, webauthid, jedisManager.getValueByStr(1, wxsid), -1);
			}
		}

		Throwable t = null;

		try {
			final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
			final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

			final Subject subject = createSubject(request, response);

			//未检查警告
			subject.execute(new Callable() {
				public Object call() throws Exception {
					updateSessionLastAccessTime(request, response);
					log.info("request is NULL:{},response is NULL,chain is NULL{}",request==null,response==null,chain==null);
					executeChain(request, response, chain);
					return null;
				}
			});
		} catch (ExecutionException ex) {
			t = ex.getCause();
		} catch (Throwable throwable) {
			t = throwable;
		}

		if (t != null) {
			if (t instanceof ServletException) {
				throw (ServletException) t;
			}
			if (t instanceof IOException) {
				throw (IOException) t;
			}
			String msg = "ShiroFilter 过滤请求失败.";
			throw new ServletException(msg, t);
		}

	}

	@Override
	protected void updateSessionLastAccessTime(ServletRequest request, ServletResponse response) {
		if (!isHttpSessions()) {
			// 'native' sessions
			Subject subject = SecurityUtils.getSubject();
			log.info("ShiroFilter updateSession：{}", (null == subject));
			if (subject != null) {
				// subject.getPrincipals();
				Session session = subject.getSession(false);
				if (session != null) {
					try {
						log.info("===更新会话===");
						session.touch();
					} catch (Throwable t) {
						log.error("session.touch()方法调用失败，不能更新会话", t);
					}
				}
			}
		}
	}

	public JedisManager getJedisManager() {
		return this.jedisManager;
	}

}
