package com.vguang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vguang.eneity.Visitor;

public interface IVisitorService {

	Integer queryCountVisitor(Map<String, Object> params);
	Integer queryCountHistoryVisitor(Map<String, Object> params);

	List<Map<String, Object>> queryVisitor(Map<String, Object> params);
	List<Map<String, Object>> queryHistoryVisitor(Map<String, Object> params);
	HashMap<String, Object> queryVisitorapplyByVid(Integer visitorApplyID);

	List<Map<String, Object>> queryVisitorApplyRuleByVisitorApplyID(Integer visitorApplyID);

	Map<String, Object> invitevisitorcode(Integer personID, Integer flag);

	Integer insertvisitor(Visitor visitor);

	Integer insertVisitorApplyRule(Integer deviceID, Integer visitorApplyID, String liftRule, String visitorBeginTime,
			String visitorEndTime);

	Integer updatevisitor(Integer visitorApplyID, String visitorApplyName, String visitorBeginTime,
			String visitorEndTime, Integer visitorStatus, String visitorApplyPhone);

	Integer updateVisitorTimeByVisitorApplyID(Integer visitorApplyID, String visitorBeginTime, String visitorEndTime);

	List<HashMap<String, Object>> getvisitorrule(Integer visitorApplyID);

	Integer delvisitorrule(Integer VisitorApplyRuleID);

	Integer updateVisitorRuleByID(Integer visitorApplyRuleID, Integer deviceID, Integer visitorApplyID, String liftRule,
			String visitorBeginTime, String visitorEndTime);

	Integer deleteVisitorRecordById(Integer VisitorApplyID);
	Integer deleteVisitorAuthorityById(Integer VisitorApplyID);
}
