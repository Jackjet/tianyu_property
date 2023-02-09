package com.vguang.controller.org;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vguang.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.vguang.service.IVisitorService;

@Controller
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@RequestMapping("/accesscontrol/visitor")
public class OrgVisitorController {
	private Logger log = LoggerFactory.getLogger(OrgVisitorController.class);
	@Autowired
	private IVisitorService VisitorService;

	/**
	 * 查询访客记录
	 * @param sessionid
	 * @param VisitorApplyName
	 * @param PersonID
	 * @param VisitorBeginTime
	 * @param VisitorEndTime
	 * @param VisitorStatus
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "queryvisitorapply", method = RequestMethod.POST)
	@ResponseBody
	public Object queryvisitorapply(
			@RequestParam String sessionid,
			@RequestParam(required = false) String VisitorApplyName,
			@RequestParam(required = false) String PersonID,
			@RequestParam(required = false) Integer VisitorBeginTime,
			@RequestParam(required = false) Integer VisitorEndTime,
			@RequestParam(required = false) Integer VisitorStatus,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("进入queryvisitorapply接口------------------");
		log.info("查询访客记录");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("VisitorApplyName", VisitorApplyName);
		params.put("PersonID", PersonID);
		params.put("VisitorBeginTime", VisitorBeginTime);
		params.put("VisitorEndTime", VisitorEndTime);
		params.put("VisitorStatus", VisitorStatus);
		// 获取设备条数
		Integer totle = VisitorService.queryCountVisitor(params);
		List<Map<String, Object>> VisitorApplys = null;
		if (totle > 0) {
			VisitorApplys = VisitorService.queryVisitor(params);
			for (int i = 0; i < VisitorApplys.size(); i++) {
				Map<String, Object> stringObjectMap = VisitorApplys.get(i);
				String visitorBeginTime = TimeUtils.Time((Timestamp) stringObjectMap.get("VisitorBeginTime"));
				stringObjectMap.put("VisitorBeginTime",visitorBeginTime);
				String visitorBeginTimes = TimeUtils.Time((Timestamp) stringObjectMap.get("VisitorEndTime"));
				stringObjectMap.put("VisitorEndTime",visitorBeginTimes);
				Integer visitorApplyID = (Integer) stringObjectMap.get("VisitorApplyID");
				List<Map<String, Object>> maps = VisitorService.queryVisitorApplyRuleByVisitorApplyID(visitorApplyID);
				stringObjectMap.put("VisitorApplyRule",maps);
				log.info(maps.toString());
			}
		}
		map.put("totle", totle);
		map.put("VisitorApplys", VisitorApplys);
		return map;
	}
	/**
	 * 删除访客记录
	 **/
	@RequestMapping(value = "deletevisitorapply", method = RequestMethod.POST)
	@ResponseBody
	public Object deletevisitorapply(
			@RequestParam String sessionid,
			@RequestParam(required = false) Integer VisitorApplyID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("删除访客记录");
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 删除访客权限
		Integer s2 = VisitorService.deleteVisitorAuthorityById(VisitorApplyID);
		log.info("删除访客权限为"+s2);
		// 删除访客信息
		Integer s1 = VisitorService.deleteVisitorRecordById(VisitorApplyID);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	/**
	 * 获取访客权限
	 * @param sessionid
	 * @param VisitorApplyID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "queryvisitorapplyrule", method = RequestMethod.POST)
	@ResponseBody
	public Object queryvisitorapplyrule(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyID,
			HttpServletRequest request,
			HttpServletResponse response) {
		System.err.println("VisitorApplyID"+VisitorApplyID);
		log.info("查询访客权限VisitorApplyID:" + VisitorApplyID);
		log.info("查询访客权限VisitorApplyID:" + VisitorApplyID);
		// Map<String, Object> map = new HashMap<>();
		// 获取访客基本信息
		HashMap<String, Object> VisitorApply = VisitorService.queryVisitorapplyByVid(VisitorApplyID);
		Date endTime = (Date) VisitorApply.get("VisitorEndTime");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String format = simpleDateFormat.format(endTime);
				VisitorApply.put("VisitorEndTime",format);
				Date endTimes = (Date) VisitorApply.get("VisitorBeginTime");
				String formats = simpleDateFormat.format(endTimes);
		VisitorApply.put("VisitorBeginTime",formats);
		log.info("基本信息为:"+VisitorApply);
		List<Map<String, Object>> VisitorApplyRules = VisitorService.queryVisitorApplyRuleByVisitorApplyID(VisitorApplyID);
		VisitorApply.put("VisitorApplyRules", VisitorApplyRules);

		return VisitorApply;
	}

	/**
	 * 访客码
	 * @param sessionid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "invitevisitorcode", method = RequestMethod.POST)
	@ResponseBody
	public Object invitevisitorcode(
			@RequestParam String sessionid,
			@RequestParam(required = false) Integer PersonID,
			@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("访客申请码");
		Map<String, Object> map = new HashMap<>();
		map = VisitorService.invitevisitorcode(PersonID, Flag);

		return map;
	}


	/**
	 * 查询历史访客记录
	 */
	@RequestMapping(value = "queryHistoryVisitorApply", method = RequestMethod.POST)
	@ResponseBody
	public Object queryHistoryVisitorApply(
			@RequestParam String sessionid,
			@RequestParam(required = false) String VisitorApplyName,
			@RequestParam(required = false) String PersonID,
			@RequestParam(required = false) String Time,
			@RequestParam(required = false) Integer VisitorEndTime,
			@RequestParam(required = false) Integer VisitorStatus,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("------------------------------------------进入controller");
		log.info("查询历史访客记录");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("VisitorApplyName", VisitorApplyName);
		params.put("PersonID", PersonID);
		params.put("VisitorBeginTime", "%"+Time+"%");
		params.put("VisitorEndTime", VisitorEndTime);
		params.put("VisitorStatus", VisitorStatus);
		// 获取设备条数
		Integer totle = VisitorService.queryCountHistoryVisitor(params);
		List<Map<String, Object>> VisitorApplys = null;
		if (totle > 0) {
			VisitorApplys = VisitorService.queryHistoryVisitor(params);
		}
		map.put("totle", totle);
		map.put("VisitorApplys", VisitorApplys);
		return map;
	}

}
