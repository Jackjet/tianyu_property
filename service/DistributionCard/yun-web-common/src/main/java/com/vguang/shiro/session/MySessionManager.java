package com.vguang.shiro.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.AbstractValidatingSessionManager;
import org.apache.shiro.session.mgt.DelegatingSession;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vguang.utils.JedisManager;

public class MySessionManager extends DefaultWebSessionManager {
	private static final Logger log = LoggerFactory.getLogger(MySessionManager.class);
	@Autowired
	private JedisManager jedisManager;

	@Override
	public Serializable getSessionId(SessionKey key) {
		log.info("SessionManager getSessionId:{}", key.getSessionId());

		Serializable id = key.getSessionId();
		if (id == null && WebUtils.isWeb(key)) {
			ServletRequest request = WebUtils.getRequest(key);
			ServletResponse response = WebUtils.getResponse(key);
			
			log.info("Session 获取JSESSIONID：{}", ((HttpServletRequest) request).getSession().getId());
			//getParameter接收form-urlencoded数据；getReader接收/json数据
//			String websid = request.getParameter("websid");
			
			String websid = null;
			String contentType = request.getContentType(); 
			log.info("请求类型：{}", contentType);
			if(contentType != null && contentType.contains("application/json")){
				try {
					String line = null;
					StringBuffer json = new StringBuffer();
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null) {
						json.append(line);
					}
					reader.close();
					System.out.println("流获取request:" + json.toString());
					JSONObject xb = new JSONObject(json.toString());
					if(xb.has("websid")){
						websid = (String) xb.get("websid");
					}
					System.out.println("获取json格式websid:" + websid);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}else if(contentType != null && contentType.contains("multipart/form-data")){
				websid = parseFormData((HttpServletRequest) request);
			}else{
				websid = request.getParameter("websid");
				System.out.println("获取form表单格式websid:" + websid);
				if (null != websid && "" != websid) {
					
				}else {
					websid = request.getParameter("sessionId");
					System.out.println("获取form表单格式sessionId:" + websid);
				}
				
			}

			//小程序确认登录，已经生成会话，后台浏览器第一次请求不存在小程序的会话，则需要模拟生成伪会话
			if (null != websid && "" != websid) {
				if (jedisManager.existsStr(1, websid)) {
					id = jedisManager.getValueByStr(1, jedisManager.getValueByStr(1, websid));
					log.info("SessionManager 从redis获取id:{}", id);
					// 响应cookie
					Cookie cookie = new Cookie("vguang", id.toString());
					cookie.setMaxAge(1800);
					((HttpServletResponse) response).addCookie(cookie);
				} else {
					//1、先从cookie获取spring-shiro中对应name的sessionid,2、如果cookie没有则从request生成
					log.info("SessionManager 从cookie获取id");
					id = getSessionId(request, response);
				}
			}else{
				log.info("SessionManager 从request获取id");
				//1、先从cookie获取spring-shiro中对应name的sessionid,2、如果cookie没有则从request生成
//				id = getSessionId(request, response);
				
	            id = getUriPathSegmentParamValue(request, ShiroHttpSession.DEFAULT_SESSION_ID_NAME);

	            if (id != null) {
	                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
	                        ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
	            }
	            
		        if (id != null) {
		            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
		            //automatically mark it valid here.  If it is invalid, the
		            //onUnknownSession method below will be invoked and we'll remove the attribute at that time.
		            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
		        }

		        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());

			}
		}
		
		log.info("Session 生成sessionId后:{}", id);
		return id;
	}
	private String parseFormData(HttpServletRequest request){
		// 判断enctype属性是否为multipart/form-data
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// 当上传文件太大时，因为虚拟机能使用的内存是有限的，所以此时要通过临时文件来实现上传文件的保存
		// 此方法是设置是否使用临时文件的临界值（单位：字节）
//		factory.setSizeThreshold(yourMaxMemorySize);
//		// 与上一个结合使用，设置临时文件的路径（绝对路径）
//		factory.setRepository(yourTempDirectory);
//		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
//		// 设置上传内容的大小限制（单位：字节）
//		upload.setSizeMax(yourMaxRequestSize);

		// Parse the request
		List<?> items = null;
		String websid = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		Iterator<?> iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = (FileItem) iter.next();

		    if (item.isFormField()) {
		    	//如果是普通表单字段
		    	log.info("name:{},value:{}", item.getFieldName(), item.getString());
		    	if("websid".equals(item.getFieldName())){
		    		websid = item.getString();
				    break;
		    	}
		    }
		    
		}
		
		return websid;
	}

	@Override
	protected Session retrieveSessionFromDataSource(Serializable sessionId) throws UnknownSessionException {
		log.info("SessionManager retrieveSessionFromDataSource:{}", sessionId);

		return sessionDAO.readSession(sessionId);
	}

	@Override
	protected void validate(Session session, SessionKey key) throws InvalidSessionException {
		// super.validate(session, key);

		try {
			doValidate(session);
		} catch (ExpiredSessionException ese) {
			onExpiration(session, ese, key);
			throw ese;
		} catch (InvalidSessionException ise) {
			onInvalidation(session, ise, key);
			throw ise;
		}
	}

	@Override
	protected void doValidate(Session session) throws InvalidSessionException {
//		super.doValidate(session);
		log.info("SessionManager doValidate:{}", session.getId());
		if (session instanceof ValidatingSession) {
            ((ValidatingSession) session).validate();
            //会话有效，则对Redis缓存会话进行更新
            log.info("===SessionManager doValidate会话有效===");
            sessionDAO.update(session);
        } else {
            String msg = getClass().getName() + "实现类仅仅支持验证实现  " + ValidatingSession.class.getName() + " 接口的会话,  " +
                    "要么实现该接口，或者重写类 " + AbstractValidatingSessionManager.class.getName() + ".doValidate(Session)方法实现验证";
            throw new IllegalStateException(msg);
        }
	}

	// 身份认证
	@Override
	protected Session createExposedSession(Session session, SessionContext context) {
		// super.createExposedSession(session, context);
		log.info("SessionManager createExposedSession1:session:{},key:{}", session.getId(), context.getSessionId());
		if (!WebUtils.isWeb(context)) {
			return super.createExposedSession(session, context);
		}
		ServletRequest request = WebUtils.getRequest(context);
		ServletResponse response = WebUtils.getResponse(context);
		SessionKey key = new WebSessionKey(session.getId(), request, response);
		Session ses = new DelegatingSession(this, key);

		return ses;
	}

	// 授权
	@Override
	protected Session createExposedSession(Session session, SessionKey key) {
		log.info("SessionManager createExposedSession2:session:{},key:{}", session.getId(), key.getSessionId());
		if (!WebUtils.isWeb(key)) {
			return super.createExposedSession(session, key);
		}

		ServletRequest request = WebUtils.getRequest(key);
		ServletResponse response = WebUtils.getResponse(key);
		SessionKey sessionKey = new WebSessionKey(session.getId(), request, response);
		Session ses = new DelegatingSession(this, sessionKey);

		return ses;
	}
	
	//改写
	private String getUriPathSegmentParamValue(ServletRequest servletRequest, String paramName) {

        if (!(servletRequest instanceof HttpServletRequest)) {
            return null;
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String uri = request.getRequestURI();
        if (uri == null) {
            return null;
        }

        int queryStartIndex = uri.indexOf('?');
        if (queryStartIndex >= 0) { //get rid of the query string
            uri = uri.substring(0, queryStartIndex);
        }

        int index = uri.indexOf(';'); //now check for path segment parameters:
        if (index < 0) {
            //no path segment params - return:
            return null;
        }

        //there are path segment params, let's get the last one that may exist:
        final String TOKEN = paramName + "=";
        uri = uri.substring(index+1); //uri now contains only the path segment params

        //我们只关心最后的JSESSIONID参数
        index = uri.lastIndexOf(TOKEN);
        if (index < 0) {
            //no segment param:
            return null;
        }

        uri = uri.substring(index + TOKEN.length());

        index = uri.indexOf(';'); //strip off any remaining segment params:
        if(index >= 0) {
            uri = uri.substring(0, index);
        }

        return uri; //what remains is the value
    }

}
