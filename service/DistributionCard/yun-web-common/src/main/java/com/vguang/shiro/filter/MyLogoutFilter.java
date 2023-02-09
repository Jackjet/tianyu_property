package com.vguang.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vguang.utils.JedisManager;

/**
 * @author wangsir
 *
 * 2017年10月12日
 */
public class MyLogoutFilter extends LogoutFilter{
	private static final Logger log = LoggerFactory.getLogger(MyLogoutFilter.class);
	@Autowired
	private JedisManager jedisManager;

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//		return super.preHandle(request, response);
		
		Subject subject = getSubject(request, response);
//        String redirectUrl = getRedirectUrl(request, response, subject) + "?websid=" + request.getParameter("websid");
        
		String websid = request.getParameter("websid");
        StringBuffer targetUrl = new StringBuffer();
        targetUrl.append(((HttpServletRequest) request).getContextPath())
        	.append("/")
        	.append(getRedirectUrl(request, response, subject))
        	.append("?websid=")
        	.append(request.getParameter("websid"))
        	;
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.debug("退出遇到异常,可以忽略{}", ise);
        }
        log.info("logout跳转:{}", null==jedisManager);
//        issueRedirect(request, response, redirectUrl);
//        response.sendRedirect(response.encodeRedirectURL(targetUrl.toString()));
        
    	JSONObject json = new JSONObject();
    	boolean flag = false;
    	//websid--personid
    	if(jedisManager.existsStr(1, websid)){
    		log.info("删除jis会话");
    		jedisManager.delValueByStr(1, websid);
    		flag = true;
    	}
    	
    	json.put("result", flag);
    	
    	response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
        return false;
	}
	
}
