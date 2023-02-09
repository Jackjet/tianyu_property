package com.vguang.controller.org;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@Controller
@RequestMapping("/exception")
public class ExceptionController {
	private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);
	/**
	 * 会话失效异常：长时间不登录、收藏页面进入、没有websid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("invalidses")
	@ResponseBody
	@CrossOrigin
	public Object invalidSessionException(HttpServletRequest request, HttpServletResponse response){
		System.out.println("会话失效处理");
		response.setStatus(601);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		map.put("code", 601);
		map.put("message", "会话失效,请重新登录");
		
		return map;
	}
	
	@RequestMapping("lockedaccount")
	@ResponseBody
	@CrossOrigin
	public Object lockedAccountException(HttpServletRequest request, HttpServletResponse response){
		System.out.println("登陆账号被禁用");
		
		response.setStatus(602);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		map.put("code", 602);
		map.put("message", "登陆账号被禁用,请联系管理员");
		
		return map;
	}
	
	@RequestMapping(value = "invalidsession")
    @ResponseBody
    @CrossOrigin
	public Object SessionFailure() {
    	
    	log.info("进入异常");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Result", 602);
		return map;
	}
}
