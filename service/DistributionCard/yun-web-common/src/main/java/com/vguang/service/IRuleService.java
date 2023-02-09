package com.vguang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRuleService {

	Integer queryCountRuleByOldRuleID(String oldRuleID);


	Integer queryCountRules(Map<String, Object> params);

	List<Map<String, Object>> queryRules(Map<String, Object> params);
	List<Map<String, Object>>queryDeviceNameAndLiftRuleByPersonID(Integer personID);
	List<Map<String, Object>> querypersonrule(Integer PersonID, Integer deviceType);

	String queryDeviceidByRuleid(String powers_ruleid);

	String queryLiftRuleByRuleid(String ruleid);

	byte[] Calculation_tk_power(byte[] tk_power, List<String> powers_rule_list);

	Integer delRuleByPersonID(Integer PersonID);

	Integer delRuleByDeviceID(Integer deviceID);

	Integer queryRuleidByDidAndPid(Integer personID, String DeviceID);

	Integer updateRule(Integer ruleid, Integer personID, String DeviceID, String startTime, String endTime,
			String liftRules);


	Integer addRule(Integer personID, Integer DeviceID, String startTime, String endTime, String liftRule);


	List<Map<String, Object>> queryRuleByPersonID(Integer personID);
	List<Map<String, Object>> queryListByPersonID(Integer personID);
	List<Map<String, Object>> queryRulesIDByPersonID(Integer personID);

	String queryLiftRuleByDevice(Integer deviceID,Integer PersonID);
	List<Map<String, Object>> queryFamilyGroupRule(Map<String, Object> params);
	Integer queryCountFamilyGroupRule(Map<String, Object> params);
	List<Map<String, Object>>queryPersonIDByGroupID(Integer GroupID);
	String[] queryVisitorEndTimeByVisitorApplyID(Integer VisitorApplyID);
	String[] queryVisitorBeginTimeByVisitorApplyID(Integer VisitorApplyID);

}
