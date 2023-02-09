package com.vguang.controller.org;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.vguang.service.ICardService;
import com.vguang.service.IDeviceService;
import com.vguang.service.IPersonService;
import com.vguang.service.IRuleService;
import com.vguang.service.ISyncService;

@Controller
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@RequestMapping("/orgman/rule")
public class OrgRuleController {
	private Logger log = LoggerFactory.getLogger(OrgCardController.class);
	@Autowired
	private ICardService CardService;
	@Autowired
	private IRuleService RuleService;
	@Autowired
	private ISyncService sysService;
	@Autowired
	private IDeviceService deviceservice;
	@Autowired
	private IPersonService PersonService;
	
	/**
	 * 权限查询
	 * @param sessionid
	 * @param FullName
	 * @param DeviceName
	 * @param PersonID
	 * @param DeviceID
	 * @param DeviceType
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "query_rule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object queryrule(
			@RequestParam String sessionid,
			@RequestParam(required = false) String FullName,
			@RequestParam(required = false) String DeviceName,
			@RequestParam(required = false) Integer PersonID,
			@RequestParam(required = false) Integer DeviceID,
			@RequestParam(required = false) Integer DeviceType, //设备类型 1门禁  2梯控
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询权限数据---"+DeviceType);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("DeviceID", DeviceID);
		params.put("PersonID", PersonID);
		params.put("fullname", FullName);
		params.put("devicename", DeviceName);
		params.put("devicetype", DeviceType);
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		// 获取条数
		Integer totle = RuleService.queryCountRules(params);
		List<Map<String, Object>> Rules = null;
		if (totle > 0) {
			Rules = RuleService.queryRules(params);
//			for (int i = 0; i < Rules.size(); i++) {
//				if(Rules.get(i).get("EndTime")!=null){
//					Rules.get(i).replace("EndTime",TimeUtils.Time((Timestamp) Rules.get(i).get("EndTime")));
//				}
//
//			}
//			for (Map<String, Object> rule : Rules) {
//				Date endTime = (Date) rule.get("EndTime");
//				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String format = simpleDateFormat.format(endTime);
//				rule.put("EndTime",format);
//			}
		}
		log.info("Rules---"+Rules);
		map.put("totle", totle);
		map.put("Rules", Rules);
		return map;
	}

	/**
	 * 权限管理列表
	 * @param sessionid
	 * @param FullName
	 * @param PhoneNum
	 * @param UserGroupName
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "queryRule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object queryRule(
			@RequestParam String sessionid,
			@RequestParam(required = false) String FullName,
			@RequestParam(required = false) String PhoneNum,
			@RequestParam(required = false) String UserGroupName,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("PhoneNum", PhoneNum);
		params.put("UserGroupName",UserGroupName);
		params.put("FullName", FullName);
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("GroupPower", 1);
		params.put("pagesize", pagesize);
		// 获取条数
		Integer totle = RuleService.queryCountFamilyGroupRule(params);
		log.info("获取条数="+totle);
		List<Map<String, Object>> Rules = null;
		// 查询家庭组权限
		if(totle>0){
			Rules = RuleService.queryFamilyGroupRule(params);
			for (int i = 0; i < Rules.size(); i++) {
				Integer PersonID = (Integer) Rules.get(i).get("PersonID");
				List<Map<String, Object>> rule = RuleService.queryDeviceNameAndLiftRuleByPersonID(PersonID);
				Rules.get(i).put("rule", rule);
		}
}
		log.info("Rules---"+Rules);
		map.put("totle", totle);
		map.put("Rules", Rules);
		return map;
	}

	/**
	 * 权限管理列表详情
	 * @param sessionid
	 * @param PersonID
	 * @return
	 */
	@RequestMapping(value = "queryListByPersonID", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object queryListByPersonID(@RequestParam String sessionid, @RequestParam Integer PersonID){
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> Rules = RuleService.queryListByPersonID(PersonID);
		for (int i = 0; i < Rules.size(); i++) {
			//Integer PersonID = (Integer) Rules.get(i).get("PersonID");
			List<Map<String, Object>> rule = RuleService.queryDeviceNameAndLiftRuleByPersonID(PersonID);
			Rules.get(i).put("rule", rule);
		}
		map.put("Rules",Rules);
		return map;
	}
	/**
	 * 权限管理权限编辑
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value = "permissionedit", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object permissionedit(@RequestBody HashMap<String,Object>map1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime=sdf.format(new Date());
		String endTime="2099-12-31";
		Map<String, Object> map = new HashMap<>();
		String msg="";
		Integer result=0;
		//Integer personID = (Integer) map1.get("PersonID");
		Integer GroupID = (Integer) map1.get("GroupID");
		log.info("先删除人员的所有权限--->再添加新的权限");
		//Integer row = RuleService.delRuleByPersonID(personID);
		List<Map<String, Object>> maps = RuleService.queryPersonIDByGroupID(GroupID);
		for (int i = 0; i < maps.size(); i++) {
			Integer personID = (Integer) maps.get(i).get("PersonID");
			log.info("通过分组ID查询所有personID==="+personID);
			Integer integer = RuleService.delRuleByPersonID(personID);
			log.info("是否删除成功----"+integer);
		}
		List<HashMap<String, Object>> Authority = (List<HashMap<String, Object>>) map1.get("Authority");
		for (int i = 0; i < Authority.size(); i++) {
			Integer deviceid = (Integer) Authority.get(i).get("deviceid");
			String LiftRules = (String) Authority.get(i).get("LiftRules");
			for (int j = 0; j < maps.size(); j++) {
				Integer personID = (Integer) maps.get(j).get("PersonID");
				log.info("通过分组ID查询所有personID==="+personID);
				Integer integer = RuleService.addRule(personID, deviceid, startTime, endTime, LiftRules);
			}

		}
		map.put("msg",msg);
		map.put("result",result);
		return map;
	}
	/**
	 * 删除家庭组权限
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value = "deleterule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object deleterule(@RequestParam String sessionid,@RequestParam Integer GroupID) {
	log.info("删除家庭组内成员所有权限");
		Map<String, Object> map = new HashMap<>();
		String msg="";
		Integer result=0;
		List<Map<String, Object>> maps = RuleService.queryPersonIDByGroupID(GroupID);
		for (int i = 0; i < maps.size(); i++) {
			Integer personID = (Integer) maps.get(i).get("PersonID");
			Integer integer = RuleService.delRuleByPersonID(personID);
		}
		map.put("msg",msg);
		map.put("result",result);
		return map;
	}

	/**
	 * 查询当前卡的权限
	 * @param sessionid
	 * @param cardnumber
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "querycardrule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object querycardrule(
			@RequestParam String sessionid,
			@RequestParam String cardnumber,
			@RequestParam(required = false) Integer DeviceType,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询当前卡的权限cardnumber--->" + cardnumber);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		// 查询当前卡号是否存在
		Integer CardState = 0;
		//Integer CardID = CardService.queryCardIDbyNumber(cardnumber, CardState);
		Integer PersonID = CardService.queryPersonIDbyNumber(cardnumber, CardState);
		if (PersonID == null) {
			log.info("当前卡号未绑定人员,或卡是非正常状态");
			result = -2;
			msg = "当前卡号未绑定人员,或卡是非正常状态";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		log.info("开始获取信息-----");
		List<Map<String, Object>> Rules = RuleService.querypersonrule(PersonID,DeviceType);
		log.info("Rules----"+Rules);
		String RuleIdS = null;
		for (int i = 0; i < Rules.size(); i++) {
			String RuleID = Rules.get(i).get("RuleID").toString();
			log.info("RuleID-----" + RuleID);
			if (i == 0) {
				RuleIdS = RuleID;
				log.info("RuleIdS======" + RuleIdS);
			} else {
				RuleIdS = RuleIdS + "," + RuleID;
				log.info("RuleIdS-----" + RuleIdS);
			}

		}
		result =0;
		map.put("RuleIdS", RuleIdS);
		map.put("result", result);
		map.put("Rules", Rules);
		return map;
	}
	
	/**
	 * 增加权限 单人-->多设备
	 * @param sessionid
	 * @param PersonID
	 * @param DeviceIDs
	 * @param LiftRules
	 * @param StartTime
	 * @param EndTime
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "add_rule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object add_rule(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			@RequestParam String DeviceIDs,
			@RequestParam String LiftRules,
			@RequestParam(required = false) String StartTime,
			@RequestParam(required = false) String EndTime,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("增加权限PersonID--->" + PersonID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		// 添加新的权限
		List<String> Deviceid_list = Arrays.asList(DeviceIDs.split(","));
		List<String> LiftRule_list = Arrays.asList(LiftRules.split("%"));
		for (int i = 0; i < Deviceid_list.size(); i++) {
			String DeviceID = Deviceid_list.get(i);
			log.info("DeviceID---->" + DeviceID);
			String LiftRule = LiftRule_list.get(i);
			log.info("LiftRule---->" + LiftRule);
			// 查询重复权限
			Integer Ruleid = RuleService.queryRuleidByDidAndPid(PersonID, DeviceID);
			if (Ruleid == null) {

				Integer row = RuleService.addRule(PersonID, Integer.valueOf(DeviceID), StartTime, EndTime, LiftRule);

			} else {
				log.info("重复权限,直接走修改覆盖掉原有权限");
				Integer row = RuleService.updateRule(Ruleid, PersonID, DeviceID, StartTime, EndTime, LiftRules);

			}

		}
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 权限添加 设备1--->人员多
	 * @param sessionid
	 * @param DeviceID
	 * @param PersonIDS
	 * @param GroupID
	 * @param LiftRule
	 * @param Flag
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "insertrule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object insertrule(
			@RequestParam String sessionid,
			@RequestParam Integer DeviceID,
			@RequestParam Integer[] PersonIDS,
			@RequestParam Integer GroupID,
			@RequestParam String LiftRule,
			@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("增加权限DeviceID--->" + DeviceID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		if (Flag == 2) {
			log.info("按分组添加");
			// 获取分组人员
			PersonIDS = PersonService.queryAllPersonidByGroupID(GroupID);
		}

		// 添加新的权限
		for (int i = 0; i < PersonIDS.length; i++) {
			Integer PersonID = PersonIDS[i];
			log.info("PersonID---->" + PersonID);
			// 查询重复权限
			Integer Ruleid = RuleService.queryRuleidByDidAndPid(PersonID, DeviceID.toString());
			if (Ruleid == null) {

				Integer row = RuleService.addRule(PersonID, Integer.valueOf(DeviceID), null, null, LiftRule);

			} else {
				log.info("重复权限,直接走修改覆盖掉原有权限");
				Integer row = RuleService.updateRule(Ruleid, PersonID, DeviceID.toString(), null, null, LiftRule);

			}

		}
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 权限编辑
	 * @param sessionid
	 * @param DeviceID
	 * @param PersonID
	 * @param RuleID
	 * @param LiftRule
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updaterule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object updaterule(
			@RequestParam String sessionid,
			@RequestParam Integer DeviceID,
			@RequestParam Integer PersonID,
			@RequestParam Integer RuleID,
			@RequestParam String LiftRule,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("权限编辑RuleID--->" + RuleID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		Integer row = RuleService.updateRule(RuleID, PersonID, DeviceID.toString(), null, null, LiftRule);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}


	@RequestMapping(value = "test", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object test(@RequestParam String sessionid,@RequestParam Integer PersonID){
		String power="";
		List<Map<String, Object>> s = RuleService.queryRulesIDByPersonID(PersonID);
		System.out.println("sssss="+s.toString());
		for (Map<String, Object> stringObjectMap : s) {
			Integer ruleID = (Integer) stringObjectMap.get("RuleID");
			power=power+ruleID+",";
		}

		power = power.substring(0,power.length() - 1);
		System.out.println("power==="+power);
		return power;
	}



}
