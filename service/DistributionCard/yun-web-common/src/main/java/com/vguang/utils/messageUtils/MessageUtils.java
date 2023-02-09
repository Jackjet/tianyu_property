package com.vguang.utils.messageUtils;

import java.io.IOException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.vguang.service.impl.LoginService;

/**
 * 短信通用发送接口
 * @author hui
 *
 */
public class MessageUtils  {
	private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);
	@Autowired
	static LoginService loginService;
	public static int Message(int appid,String appkey,int templateId,String smsSign ,String[] phoneNumbers,String[] contents) {
		log.info("进入utils---------------");
//		Object loginService = SpringContextUtil.getBean("loginService");
//		log.info("loginService===" + loginService);
		String msg ="";
		log.info("发送短信:loginService==="+loginService);
		 int result = ShortMessage(appid, appkey, templateId, smsSign, phoneNumbers, contents, "", "");
		 log.info("result======="+result);
		return result;
	}
	
	
	private static int ShortMessage(int appid, String appkey, int templateId, String smsSign, String[] phoneNumbers,
			String[] contents, String string, String string2) {
		log.info("进入utils2---------------");
		int flag = -1;
		try {
			  
		    SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
		    SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
		        templateId, contents, smsSign, "", "");// 签名参数未提供或者为空时，会使用默认签名发送短信
		    System.out.println(result);
		} catch (HTTPException e) {
		    // HTTP 响应码错误
		    e.printStackTrace();
		} catch (JSONException e) {
		    // JSON 解析错误
		    e.printStackTrace();
		} catch (IOException e) {
		    // 网络 IO 错误
		    e.printStackTrace();
		}
		 flag = 0;
		return flag;
	}
}
