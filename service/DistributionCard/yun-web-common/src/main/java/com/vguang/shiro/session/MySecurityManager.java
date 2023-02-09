package com.vguang.shiro.session;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.vguang.shiro.MyShiroRealm;

public class MySecurityManager extends DefaultWebSecurityManager {
	private static final Logger log = LoggerFactory.getLogger(MySecurityManager.class);
	@Autowired
	private MyShiroRealm shiroRealm;
	
	@Override
	public boolean isHttpSessionMode() {
		return true;
	}

	@Override
	protected Session resolveContextSession(SubjectContext context) throws InvalidSessionException {
		SessionKey key = getSessionKey(context);
		log.info("===Security resolveContextSessionId===");
		
		if (key != null) {
			System.out.println("Key SessionId:" + key.getSessionId());
			return getSession(key);
		}else{
			System.out.println("Key SessionId为空" );
		}
		return null;
	}

	@Override
	protected SessionKey getSessionKey(SubjectContext context) {
		if (WebUtils.isWeb(context)) {
            Serializable sessionId = context.getSessionId();
            ServletRequest request = WebUtils.getRequest(context);
            ServletResponse response = WebUtils.getResponse(context);
            System.out.println("===getSessionKey:" + sessionId);
            
            return new WebSessionKey(sessionId, request, response);
        } else {
            return super.getSessionKey(context);

        }
	}

	@Override
	public Subject login(Subject subject, AuthenticationToken token) throws AuthenticationException {
//		return super.login(subject, token);
		
		log.info("===Security 登录login===");
		AuthenticationInfo info;
        try {
        	//myShiroRealm身份认证
            info = authenticate(token);
        } catch (AuthenticationException ae) {
            try {
                onFailedLogin(token, ae, subject);
            } catch (Exception e) {
                if (log.isInfoEnabled()) {
                    log.info("登录失败抛出异常:{}", e);
                }
            }
            throw ae; //propagate
        }
        
        Gson gson = new Gson();
        log.info("subject是否是旧的：subject:{}, principals:{}", subject==null, gson.toJson(subject.getPrincipals()));
        Subject loggedIn = createSubject(token, info, subject);
        log.info("为何重复登录principal不存在：{}", gson.toJson(loggedIn.getPrincipal()));
        
        //登陆成功后判断记住我功能
        onSuccessfulLogin(token, info, loggedIn);
        log.info("重复登录principal不存在：{}", gson.toJson(loggedIn.getPrincipal()));
        
        //重复登录清除历史缓存
        log.info("加载ShiroRealm:{}", null==shiroRealm);
        if(shiroRealm == null){
        	log.info("没有加载到ShiroRealm");
        }else{
        	Cache<Object, AuthorizationInfo> cache = shiroRealm.getAuthorizationCache();
            if (cache == null && shiroRealm.isAuthorizationCachingEnabled()) {
            }
            
            if (cache != null) {
            	log.info("如果这里没错：{}", gson.toJson(cache.get(loggedIn.getPrincipals())));
                cache.remove(loggedIn.getPrincipals());
            }
        }
        
        return loggedIn;
	}

	@Override
	public Subject createSubject(SubjectContext subjectContext) {
//		return super.createSubject(subjectContext);
		
		//创建subjectContext的一个副本
        SubjectContext context = copy(subjectContext);
        //确保SecurityManager被实例化
        context = ensureSecurityManager(context);
        //解析Session(通常基于一个sessionid), 在放到SubjectFactory之前,把该session放到context
        context = resolveSession(context);

        //Similarly, the SubjectFactory should not require any concept of RememberMe - translate that here first
        //if possible before handing off to the SubjectFactory:
        context = resolvePrincipals(context);

        Subject subject = doCreateSubject(context);
        //保存subject供将来使用,当使用记住我功能时,该subject需要被保存在会话中,不用每次都创建
        save(subject);
        
        log.info("Save Subject:{}", new Gson().toJson(subject.getPrincipal()));
        return subject;
	}

	@Override
	protected SubjectContext resolveSession(SubjectContext context) {
//		return super.resolveSession(context);
		
//		Session ses = context.resolveSession();
		Session ses = context.getSession();
        if (ses == null) {
        	log.info("Security context中不存在ses");
            //如果subject已经存在则从subject中获取
            Subject existingSubject = context.getSubject();
            if (existingSubject != null) {
            	log.info("Security 从subject中获取ses");
                ses = existingSubject.getSession(false);
            }
        }
		
		if (ses != null) {
            log.info("Security 从Context中获取session:{}", ses.getId());
            return context;
        }
        try {
            Session session = resolveContextSession(context);
            if (session != null) {
                context.setSession(session);
            }
        } catch (InvalidSessionException e) {
        	log.error("Security context解析session无效", e);
        	throw new InvalidSessionException("context解析session异常");
        }
        return context;
	}
	
	

	
}
