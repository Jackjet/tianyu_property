package com.vguang.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vguang.utils.SessionFailureException;
import com.vguang.system.SpringContextUtil;
import com.vguang.utils.JedisManager;

/**
 * hui
 * 
 * @author mi
 *
 */
public class HttpServletRequestReplacedFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(HttpServletRequestReplacedFilter.class);

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException, RuntimeException {
//		HttpServletResponse Response = (HttpServletResponse) response;
//		// 指定允许其他域名访问
//		Response.setHeader("Access-Control-Allow-Origin", "*"); // 允许所有
//		// response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1, http://locahost"); // 允许白名单IP
//		// 响应类型
//		Response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		// 预检请求的结果缓存60分钟
//		Response.setHeader("Access-Control-Max-Age", "3600");
//		// 响应头设置
//		Response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//		chain.doFilter(request, response);

		ServletRequest requestWrapper = null;
		Map<String, Object> params = new HashMap<String, Object>();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		log.info("增加web日志=====开始====");
		// 获得URL

		String requestUrl = httpRequest.getRequestURI();
		// 获取所有参数
		Map<String, String[]> map = request.getParameterMap();
		ObjectMapper json = new ObjectMapper();
		String RequestParams = null;
		RequestParams = json.writeValueAsString(map);
		log.info("RequestParams==========="+RequestParams);
		// 获得IP地址
		String RequestIp = ((HttpServletRequest) request).getHeader("X-Real-IP");
		// 获得当前时间
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String RequestTime = formatter.format(date);

		if (requestUrl.endsWith("html")) {
			chain.doFilter(request, response);
		} else {
//			if (!requestUrl.contains("/DistributionCard/") || requestUrl.contains("/getCaptcha")||requestUrl.contains("/login")||requestUrl.contains("/cardupload")||requestUrl.contains("/upload_device")
//					||requestUrl.contains("/upload_rule")||requestUrl.contains("/rl_distributioncard") || requestUrl.contains("/getownerdetails") || requestUrl.contains("/queryvisitorapply")
//					|| requestUrl.contains("/queryvisitorapplyrule")|| requestUrl.contains("/queryvisitorapply")|| requestUrl.contains("/queryfamily")|| requestUrl.contains("/deletevisitorapply")|| requestUrl.contains("/queryallperson")|| requestUrl.contains("/query_rule")|| requestUrl.contains("/queryHistoryVisitorApply")
//					) {
				if (!requestUrl.contains("/DistributionCard/") || requestUrl.contains("/getCaptcha")||requestUrl.contains("/login")||requestUrl.contains("/cardupload")||requestUrl.contains("/upload_device")
						||requestUrl.contains("/upload_rule")||requestUrl.contains("/rl_distributioncard")||requestUrl.contains("/queryvisitorapply")||requestUrl.contains("/querypersondetails")||requestUrl.contains("/queryvisitorapplyrule")||requestUrl.contains("/deletevisitorapply")||requestUrl.contains("/queryHistoryVisitorApply")||requestUrl.contains("/invitevisitorcode")||requestUrl.contains("/inviteperson")||requestUrl.contains("/queryfamily")||requestUrl.contains("/updategroupstatus")||requestUrl.contains("/updatevisitor")
						||requestUrl.contains("/queryreturncard")||requestUrl.contains("/upload")||requestUrl.contains("/open_distributioncard")||requestUrl.contains("/query_card")||requestUrl.contains("/queryallusergroup")||requestUrl.contains("/importExcel")||requestUrl.contains("/permissionedit")
				) {

				log.info("验证码和登陆无需验证");

			} else {
				log.info("跳过");
				String sessionid = httpRequest.getParameter("sessionid");

				if (null == sessionid || "".equals(sessionid)) {
					log.info("参数websid为空");
					throw new SessionFailureException("登陆会话失效");
				} else {
					JedisManager jedisManager = (JedisManager) SpringContextUtil.getBean("jedisManager");
					log.info("jedisManager:{}", null == jedisManager);


					try {
					if (jedisManager.existsStr(1, sessionid)) {

					} else {
						log.info("会话失效");
						throw new SessionFailureException("登陆会话失效");

					}
					String uid = jedisManager.getValueByStr(1, sessionid);
					log.info("过滤器uid====="+uid);
					if(("").equals(uid)) {
						log.info("uid为空");
						throw new SessionFailureException("登陆会话失效");
					}else {
						//更新会话时间
						jedisManager.setValueByStr(1, sessionid, uid, 7200);
					}
					} catch (SessionFailureException e) {
						// TODO Auto-generated catch block
						throw new SessionFailureException("登陆会话失效");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}


		}

		if (request instanceof HttpServletRequest) {
			String contentType = request.getContentType();
			log.info("包装request请求类型：{}", contentType);
			if (null != contentType) {
				log.info("包装request请求类型json或者form：{}", contentType.equals("application/json"));
				if (contentType.contains("application/json") || contentType.contains("multipart/form-data")) {
					requestWrapper = new MAPIHttpServletRequestWrapper((HttpServletRequest) request);
				}
			}
		}
		if (requestWrapper == null) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(requestWrapper, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
