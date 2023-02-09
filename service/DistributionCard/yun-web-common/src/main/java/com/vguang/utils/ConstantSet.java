/**
 * 
 */
package com.vguang.utils;

/**
 * @author Dingjz
 *
 * @date 2018年8月15日
 */
public class ConstantSet {

	public static final String MQTT_VERSION_1 = "20171214";
	public static final String MQTT_VERSION_2 = "20180810";
	public static final String MQTT_VERSION_3 = "20190319";
	public static final String MQTT_VERSION_4 = "20180820";
	public static final String MQTT_VERSION_5 = "20191108";

	public static final int SERNO_REDIS_INDEX = 1;

	public static final int TOTAL_SYNC_TYPE = 1;
	public static final int INCREMENT_SYNC_TYPE = 2;

	public static final int SYNC_STATUS_SUCCESS = 0;
	public static final int SYNC_STATUS_FAILD = -1;

	public static final int ADD_ACTION_TYPE = 1;
	public static final int MOD_ACTION_TYPE = 2;
	public static final int DEL_ACTION_TYPE = 3;

	public static final int RULE_MSG_TYPE = 1;
	public static final int TIMERANGE_MSG_TYPE = 2;
	public static final int AUTH_MSG_TYPE = 3;
	public static final int CARD_MSG_TYPE = 4;
	public static final int FaceRule_MSG_TYPE = 5;
	
	public static final String GRANTTYPE = "authorization_code";
	public static final String AES_KEY = "Nm-#km=IlXER=soP";
	public static final String AES_IV = "oBLEW#UkvmP=WXC=";
	public static final int WX_UID_REDIS_INDEX = 1;
	public static final int WEB_UID_REDIS_INDEX = 1;
	public static final int RSP_SUCCESS_CODE = 0;
	public static final int RSP_FAILURE_CODE = -1;
	public static final int RSP_ERR_CAPTCHA_CHECK = -2;
	public static final int RSP_LOSE_EFF_CODE = -3;
	public static final int RSP_NO_SESSIONID = -100;
	public static final int WXAPP_PARSE_ERR = -101;
	public static final String LOCAL_CAPTCHA_BASE = System.getProperty("captcha.base");
	public static final String LOCAL_QRCODE_BASE = System.getProperty("qrcode.base");
			
}
