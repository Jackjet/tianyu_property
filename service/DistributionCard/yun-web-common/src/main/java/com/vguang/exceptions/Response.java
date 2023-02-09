package com.vguang.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * controller全局异常实体类
 * @author wang
 *
 */
public class Response {
    
    /**
     * 响应失败
     * @param code
     * @param message
     * @return
     */
    public Map<String, Object> failure(int code, String message){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("success", false);
    	map.put("code", code);
    	map.put("message", message);
    	
    	return map;
    }
    
    /**
     * 响应成功(保留方法)
     * @param code
     * @param message
     * @return
     */
    public Map<String, Object> success(int code, String message){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("success", true);
    	map.put("code", code);
    	map.put("message", message);
    	
    	return map;
    }
  
}
