package com.vguang.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface ISyncDao {

	
	public List<Map<String, String>> querySysConfigs();

	public String queryValueByKey(String sysConfigKey);

	public Integer addSysConfigByKey(String sysConfigName, int sysConfigType, String sysConfigKey,
			String sysConfigValue, String sysConfigDesc, Integer sendOutState, String sysGroup);

	public HashMap<String, Object> querySysConfigByKey(String sysConfigKey);

	public Integer updateSyscongigByKey(String sysConfigKey, String sysConfigValue);

	public String getValueByKey(String key);

	public Integer queryCountSysConfigByKey(String sysConfigKey);

	public List<HashMap<String, Object>> querySysConfigsbByType(Integer type);

	public Integer updateSendOutStateByKey(Integer sendOutState, String sysConfigKey);

	public Integer queryCountSysConfigsByparams(Map<String, Object> params);

	public List<HashMap<String, Object>> querySysConfigsByparams(Map<String, Object> params);

	
}
