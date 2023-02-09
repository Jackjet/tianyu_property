/**
 * 
 */
package com.vguang.init;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vguang.init.task.CaptchaTask;
import com.vguang.utils.StringUtil;

/**
 * @author Dingjz
 *
 * @date 2018年7月13日
 */
@Component
public class TaskHandler {
	private static final Logger log = LoggerFactory.getLogger(TaskHandler.class);
	@PostConstruct
	public void taskBoot() {
		log.info(StringUtil.logStr("启动验证码图片删除线程"));
		long captchaId = startCaptchaTask();
		log.info(StringUtil.logStr("验证码图片删除线程ID为:{}"),captchaId);
	
	}
	
	private long startCaptchaTask() {
		Thread t = new CaptchaTask();
		t.start();
		return t.getId();
	}
	

}
