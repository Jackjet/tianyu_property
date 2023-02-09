package com.vguang.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理
 * @author wang
 * date 2017-05-31
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {
	private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

	/** 
     * 400 - Bad Request 
     */  
    @ResponseStatus(HttpStatus.BAD_REQUEST)  
    @ExceptionHandler(HttpMessageNotReadableException.class)  
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {  
        logger.error("参数解析失败", e);  
        return new Response().failure(400, "不能解析json格式数据");  
    }  
  
    /** 
     * 405 - Method Not Allowed 
     */  
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)  
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)  
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {  
        logger.error("不支持当前请求方法", e);  
        return new Response().failure(405, "请求方法不被支持");  
    }  
  
    /** 
     * 415 - Unsupported Media Type 
     */  
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)  
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)  
    public Object handleHttpMediaTypeNotSupportedException(Exception e) {  
        logger.error("不支持当前媒体类型", e);  
        return new Response().failure(415, "内容类型不被支持");  
    }  
  
    /** 
     * 500 - 服务器内部错误 
     */  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  
    @ExceptionHandler(Exception.class)  
    public Object handleException(Exception e) {  
        logger.error("服务运行异常", e);
        return new Response().failure(500, e.getMessage());  
    }
    
    
}
