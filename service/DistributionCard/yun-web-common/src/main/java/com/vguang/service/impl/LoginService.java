package com.vguang.service.impl;

import java.io.IOException;

import javax.annotation.Resource;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.vguang.dao.ICardDao;
import com.vguang.dao.ILoginSessionDao;
import com.vguang.service.ILoginService;
import com.vguang.service.IOrgService;

@Service("loginService")
public class LoginService implements ILoginService {
	@Resource
	private ILoginSessionDao LoginDao;
	private Logger log = LoggerFactory.getLogger(LoginService.class);
	@Override
	public Integer queryAccountIDByAccount(String accountname, String accountpassword) {
		// TODO Auto-generated method stub
		return LoginDao.queryAccountIDByAccount(accountname,accountpassword);
	}
	@Override
	public Integer queryAccountStateByID(Integer accountID) {
		// TODO Auto-generated method stub
		return LoginDao.queryAccountStateByID(accountID);
	}
	@Override
	public Integer queryCardIDByAccount(String accountname, String accountpassword) {
		// TODO Auto-generated method stub
		return LoginDao.queryCardIDByAccount(accountname,accountpassword);
	}
	@Override
	public Integer updateFirstState(Integer firstState, Integer accountID) {
		// TODO Auto-generated method stub
		return LoginDao.updateFirstState(firstState,accountID);
	}
	@Override
	public Integer insertAccount(String PersonID, String accountName, String accountPassword, Integer firstState,
			Integer accountState) {
		// TODO Auto-generated method stub
		return LoginDao.insertAccount(PersonID,accountName,accountPassword,firstState,accountState);
	}
	@Override
	public Integer updateAccountPasswordByID(String accountID,String AccountName, String accountPassword) {
		// TODO Auto-generated method stub
		return LoginDao.updateAccountPasswordByID(accountID,AccountName,accountPassword);
	}
	@Override
	public Integer queryCountAccountByAccountName(String accountName) {
		// TODO Auto-generated method stub
		return LoginDao.queryCountAccountByAccountName(accountName);
	}
	@Override
	public Integer delAccountByCardID(String cardID) {
		// TODO Auto-generated method stub
		return LoginDao.delAccountByCardID(cardID);
	}
	@Override
	public Integer queryPersonIDByAccount(String accountName, String accountPassword) {
		// TODO Auto-generated method stub
		return LoginDao.queryPersonIDByAccount(accountName,accountPassword);
	}
	public int ShortMessage(int appid, String appkey, int templateId, String smsSign, String[] phoneNumbers,
			String[] params, String string, String string2) {
		int flag = -1;
		try {
			  
		    SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
		    SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
		        templateId, params, smsSign, "", "");// 签名参数未提供或者为空时，会使用默认签名发送短信
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
