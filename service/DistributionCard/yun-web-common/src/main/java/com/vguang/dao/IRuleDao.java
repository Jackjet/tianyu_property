package com.vguang.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface IRuleDao {

	Integer queryCountRuleByOldRuleID(String oldRuleID);


	Integer queryCountRules(Map<String, Object> params);

	List<Map<String, Object>> queryRules(Map<String, Object> params);
	List<Map<String, Object>> queryFamilyGroupRule(Map<String, Object> params);

	List<Map<String, Object>> querypersonrule(Integer PersonID, Integer deviceType);
	List<Map<String, Object>> queryDeviceNameAndLiftRuleByPersonID(Integer personID);

	String queryDeviceidByRuleid(String powers_ruleid);

	String queryLiftRuleByRuleid(String ruleid);

	Integer delRuleByPersonID(Integer PersonID);

	Integer delRuleByDeviceID(Integer deviceID);


	Integer queryRuleidByDidAndPid(Integer personID, String DeviceID);


	Integer updateRule(Integer ruleid, Integer personID, String DeviceID, String startTime, String endTime,
			String liftRules);


	Integer addRule(Integer personID, Integer DeviceID, String startTime, String endTime, String liftRule);


	List<Map<String, Object>> queryRuleByPersonID(Integer personID);
	List<Map<String, Object>>queryRulesIDByPersonID(Integer personID);

	String queryLiftRuleByDevice(Integer deviceID,Integer PersonID);
	Integer queryCountFamilyGroupRule(Map<String, Object> params);
	List<Map<String, Object>> queryListByPersonID(Integer personID);
	List<Map<String, Object>> queryPersonIDByGroupID(Integer GroupID);
	String[]  queryVisitorEndTimeByVisitorApplyID(Integer VisitorApplyID);
	String[] queryVisitorBeginTimeByVisitorApplyID(Integer VisitorApplyID);

}
