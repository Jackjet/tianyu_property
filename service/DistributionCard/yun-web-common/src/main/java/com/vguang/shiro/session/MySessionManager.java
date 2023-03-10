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
			
			log.info("Session ??????JSESSIONID???{}", ((HttpServletRequest) request).getSession().getId());
			//getParameter??????form-urlencoded?????????getReader??????/json??????
//			String websid = request.getParameter("websid");
			
			String websid = null;
			String contentType = request.getContentType(); 
			log.info("???????????????{}", contentType);
			if(contentType != null && contentType.contains("application/json")){
				try {
					String line = null;
					StringBuffer json = new StringBuffer();
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null) {
						json.append(line);
					}
					reader.close();
					System.out.println("?????????request:" + json.toString());
					JSONObject xb = new JSONObject(json.toString());
					if(xb.has("websid")){
						websid = (String) xb.get("websid");
					}
					System.out.println("??????json??????websid:" + websid);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}else if(contentType != null && contentType.contains("multipart/form-data")){
				websid = parseFormData((HttpServletRequest) request);
			}else{
				websid = request.getParameter("websid");
				System.out.println("??????form????????????websid:" + websid);
				if (null != websid && "" != websid) {
					
				}else {
					websid = request.getParameter("sessionId");
					System.out.println("??????form????????????sessionId:" + websid);
				}
				
			}

			//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			if (null != websid && "" != websid) {
				if (jedisManager.existsStr(1, websid)) {
					id = jedisManager.getValueByStr(1, jedisManager.getValueByStr(1, websid));
					log.info("SessionManager ???redis??????id:{}", id);
					// ??????cookie
					Cookie cookie = new Cookie("vguang", id.toString());
					cookie.setMaxAge(1800);
					((HttpServletResponse) response).addCookie(cookie);
				} else {
					//1?????????cookie??????spring-shiro?????????name???sessionid,2?????????cookie????????????request??????
					log.info("SessionManager ???cookie??????id");
					id = getSessionId(request, response);
				}
			}else{
				log.info("SessionManager ???request??????id");
				//1?????????cookie??????spring-shiro?????????name???sessionid,2?????????cookie????????????request??????
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
		
		log.info("Session ??????sessionId???:{}", id);
		return id;
	}
	private String parseFormData(HttpServletRequest request){
		// ??????enctype???????????????multipart/form-data
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		// ???????????????????????????????????????????????????????????????????????????
//		factory.setSizeThreshold(yourMaxMemorySize);
//		// ????????????????????????????????????????????????????????????????????????
//		factory.setRepository(yourTempDirectory);
//		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
//		// ??????????????????????????????????????????????????????
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
		    	//???????????????????????????
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
            //?????????????????????Redis????????????????????????
            log.info("===SessionManager doValidate????????????===");
            sessionDAO.update(session);
        } else {
            String msg = getClass().getName() + "?????????????????????????????????  " + ValidatingSession.class.getName() + " ???????????????,  " +
                    "??????????????????????????????????????? " + AbstractValidatingSessionManager.class.getName() + ".doValidate(Session)??????????????????";
            throw new IllegalStateException(msg);
        }
	}

	// ????????????
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

	// ??????
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
	
	//??????
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

        //????????????????????????JSESSIONID??????
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
