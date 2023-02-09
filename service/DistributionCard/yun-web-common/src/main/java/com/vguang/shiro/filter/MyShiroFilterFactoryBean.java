package com.vguang.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;

public class MyShiroFilterFactoryBean extends ShiroFilterFactoryBean {
	private FilterChainManager filterManager;

	@Override
	public Class getObjectType() {
		return MySpringShiroFilter.class;
	}

	@Override
	protected AbstractShiroFilter createInstance() throws Exception {

		SecurityManager securityManager = getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager属性必须被设置.";
			throw new BeanInitializationException(msg);
		}

		if (!(securityManager instanceof WebSecurityManager)) {
			String msg = "Security manager没有实现WebSecurityManager接口.";
			throw new BeanInitializationException(msg);
		}
		FilterChainManager manager = createFilterChainManager();
		setFilterManager(manager);

		PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
		chainResolver.setFilterChainManager(manager);

		return new MySpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
	}
	
	public FilterChainManager getFilterManager() {
		return filterManager;
	}

	public void setFilterManager(FilterChainManager filterManager) {
		this.filterManager = filterManager;
	}




	//静态内部类
	private static final class MySpringShiroFilter extends MyShiroFilter {

		protected MySpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
			super();

			if (webSecurityManager == null) {
				throw new IllegalArgumentException("WebSecurityManager属性不能为空.");
			}
			setSecurityManager(webSecurityManager);
			if (resolver != null) {
				setFilterChainResolver(resolver);
			}
		}

		@Override
		protected ServletRequest wrapServletRequest(HttpServletRequest orig) {
			
			//重写ShiroHttpServletRequest
			return new MyShiroHttpServletRequest(orig, getServletContext(), isHttpSessions());
		}

	}
	
	

}
