package com.vguang.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vguang.service.ISyncService;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;

public class SysConfig {
	private static final Logger log = LoggerFactory.getLogger(SysConfig.class);
	private static Gson gson = new Gson();
	public static String GetSysConfig(String SysConfigName, int SysConfigType, String SysConfigKey,
		String SysConfigValue, String SysConfigDesc,Integer SendOutState , int OrgSwitch) {
		// 手动注入
		SystemConfigs sys = (SystemConfigs) SpringContextUtil.getBean("sysConfigs");
		log.info("sys==========" + sys);
		Object syncService = SpringContextUtil.getBean("syncService");
		log.info("syncService==========" + syncService);
		String Value = ((ISyncService) syncService).queryValueByKey(SysConfigKey);
//		String Value = sys.getValue(SysConfigKey);
		log.info("Value>>>>>>>>>>>>" + Value);
		Integer row = 0;
		if (OrgSwitch == 1) {
			log.info("OrgSwitch=1");
			if (Value == null) {
				log.info("Value进来了");
				row = ((ISyncService) syncService).addSysConfigByKey(SysConfigName, SysConfigType, SysConfigKey,
						SysConfigValue, SysConfigDesc,SendOutState,null);
				log.info("返回row=====" + row);
				return SysConfigValue;
			} else {
				log.info("已存在  直接返回");
				return Value;
			}

		} else {
			log.info("OrgSwitch=2");
			return Value;
	 }
  }

	
	
}
