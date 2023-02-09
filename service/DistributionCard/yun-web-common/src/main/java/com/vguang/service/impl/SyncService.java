package com.vguang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vguang.dao.ISyncDao;
import com.vguang.service.ISyncService;

/**
 * hui
 * @author mi
 *
 */
@Service("syncService")
public class SyncService implements ISyncService {
	@Resource
	private ISyncDao syncDao;
	private Logger log = LoggerFactory.getLogger(SyncService.class);


	@Override
	public List<Map<String, String>> querySysConfigs() {
		return syncDao.querySysConfigs();
	}


	@Override
	public String queryValueByKey(String sysConfigKey) {
		// TODO Auto-generated method stub
		return syncDao.queryValueByKey(sysConfigKey);
	}


	@Override
	public Integer addSysConfigByKey(String sysConfigName, int sysConfigType, String sysConfigKey,
			String sysConfigValue, String sysConfigDesc,Integer sendOutState,String sysGroup) {
		// TODO Auto-generated method stub
		return syncDao.addSysConfigByKey(sysConfigName,sysConfigType,sysConfigKey,sysConfigValue,sysConfigDesc,sendOutState,sysGroup);
	}


	@Override
	public HashMap<String, Object> querySysConfigByKey(String sysConfigKey) {
		// TODO Auto-generated method stub
		return syncDao.querySysConfigByKey(sysConfigKey);
	}


	@Override
	public Integer updateSyscongigByKey(String sysConfigKey, String sysConfigValue) {
		// TODO Auto-generated method stub
		return syncDao.updateSyscongigByKey(sysConfigKey,sysConfigValue);
	}


	@Override
	public String getValueByKey(String key) {
		// TODO Auto-generated method stub
		return syncDao.getValueByKey(key);
	}


	@Override
	public Integer queryCountSysConfigByKey(String sysConfigKey) {
		// TODO Auto-generated method stub
		return syncDao.queryCountSysConfigByKey(sysConfigKey);
	}


	@Override
	public List<HashMap<String, Object>> querySysConfigsbByType(Integer type) {
		// TODO Auto-generated method stub
		return syncDao.querySysConfigsbByType(type);
	}


	@Override
	public Integer updateSendOutStateByKey(Integer sendOutState, String sysConfigKey) {
		// TODO Auto-generated method stub
		return syncDao.updateSendOutStateByKey(sendOutState,sysConfigKey);
	}


	@Override
	public Integer queryCountSysConfigsByparams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return syncDao.queryCountSysConfigsByparams(params);
	}


	@Override
	public List<HashMap<String, Object>> querySysConfigsByparams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return syncDao.querySysConfigsByparams(params);
	}


	
}
