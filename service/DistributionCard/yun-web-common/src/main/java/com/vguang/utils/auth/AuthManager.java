/**
 * 
 */
package com.vguang.utils.auth;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.vguang.utils.GsonUtil;
import com.vguang.utils.StringUtil;

/**
 * @author Dingjz
 *
 * @date 2018年7月9日
 */
@Component
public class AuthManager {
	private static final Logger log = LoggerFactory.getLogger(AuthManager.class);
	@Autowired
	AuthJni authJni;
	Gson gson = GsonUtil.getGson();
	private static final String orgAuth = "1234567887654321";

	/**
	 * 获得当前密钥
	 * 
	 * @param orgid
	 * @return
	 */
	public HashMap<String, Object> getCurAuth(String orgAuth) {
		// 取密钥的时间
		long beginTime = System.currentTimeMillis() / 1000;
		// 密钥的BUFFER
		byte[] authBytes = new byte[16];
		// 密钥结束时间
		byte[] endTimeBytes = new byte[8];
		// 组织密钥，传倒密钥池里得到真正能用的密钥
		byte[] orgAuthBytes = orgAuth.getBytes();
		log.info(StringUtil.logStr("准备调用get_current_key"));
		int result = authJni.get_current_key(beginTime, authBytes, endTimeBytes, orgAuthBytes);
		log.info(StringUtil.logStr("调用get_current_key的result:{},authBytes为:{}"), result, authBytes);
		if (result <= 0) {// <0失败 >0密钥池号
			log.info(StringUtil.logStr("调用get_current_key失败"));
			return null;
		}
		log.info(StringUtil.logStr("调用get_current_key成功"));

		String auth = StringUtil.dec2Hex(authBytes);
		log.info(StringUtil.logStr("auth:{}"), auth);
		long endTime = StringUtil.byte2Long(endTimeBytes);
		HashMap<String, Object> map = new HashMap<>();
		map.put("authKey", auth);
		map.put("endTime", endTime);
		map.put("authNum", result);
		log.info(StringUtil.logStr(gson.toJson(map)));
		return map;
	}

	public HashMap<String, Object> getNextAuth(String orgAuth) {
		// 取密钥的时间
		long beginTime = System.currentTimeMillis() / 1000;
		// 密钥的BUFFER
		byte[] authBytes = new byte[16];
		// 密钥结束时间
		byte[] endTimeBytes = new byte[8];
		// 组织密钥，传倒密钥池里得到真正能用的密钥
		byte[] orgAuthBytes = orgAuth.getBytes();
		log.info(StringUtil.logStr("准备调用get_next_key"));
		int result = authJni.get_next_key(beginTime, authBytes, endTimeBytes, orgAuthBytes);
		log.info(StringUtil.logStr("调用get_next_key的result:{},authBytes为:{}"), result, authBytes);
		if (result <= 0) {// -1 时间 -2 密钥
			log.info(StringUtil.logStr("调用get_next_key失败"));
			return null;
		}
		log.info(StringUtil.logStr("调用get_next_key成功"));

		String auth = StringUtil.dec2Hex(authBytes);
		log.info(StringUtil.logStr("auth:{}"), auth);
		long endTime = StringUtil.byte2Long(endTimeBytes);
		HashMap<String, Object> map = new HashMap<>();
		map.put("authKey", auth);
		map.put("endTime", endTime);
		map.put("authNum", result);
		log.info(StringUtil.logStr(gson.toJson(map)));
		return map;
	}

	// private getResultMap(String )
	/**
	 * 获得当前密钥
	 * 
	 * @param orgid
	 * @return
	 */
	public HashMap<String, Object> getV104CurAuth(String orgAuth,long beginTime) {
		log.info("API调用获取合成秘钥");
		// 取密钥的时间
		//long beginTime = System.currentTimeMillis() / 1000;
		// 密钥的BUFFER
		byte[] authBytes = new byte[16];
		// 密钥结束时间
		byte[] endTimeBytes = new byte[8];
		// 组织密钥，传倒密钥池里得到真正能用的密钥
		byte[] orgAuthBytes = orgAuth.getBytes();
		log.info("orgAuth++"+orgAuth);
		log.info("beginTime++"+beginTime);
		log.info("authBytes++"+authBytes);
		log.info("endTimeBytes++"+endTimeBytes);
		log.info("orgAuthBytes++"+orgAuthBytes);
		log.info(StringUtil.logStr("准备调用get_current_key==="+authJni));
		AuthJni AuthJni = new AuthJni();
		int result = AuthJni.get_current_key(beginTime, authBytes, endTimeBytes, orgAuthBytes);
		log.info(StringUtil.logStr("调用get_current_key的result:{},authBytes为:{}"), result, authBytes);
		if (result <= 0) {// <0失败 >0密钥池号
			log.info(StringUtil.logStr("调用get_current_key失败"));
			return null;
		}
		log.info(StringUtil.logStr("调用get_current_key成功"));

		String auth = StringUtil.dec2Hex(authBytes);
		log.info(StringUtil.logStr("auth:{}"), auth);
		long endTime = StringUtil.byte2Long(endTimeBytes);
		HashMap<String, Object> map = new HashMap<>();
		map.put("auth", auth);
		map.put("endTime", endTime);
		map.put("authNum", result);
		log.info(StringUtil.logStr(gson.toJson(map)));
		return map;
	}

}
