package com.vguang.controller.org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vguang.service.IRecordsService;

@Controller
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@RequestMapping("/orgman/Records")
public class OrgRecordsController {
	private Logger log = LoggerFactory.getLogger(OrgRecordsController.class);
	@Autowired
	private IRecordsService RecordsService ;
	
	
	/**
	 * 查询操作历史记录
	 * @param sessionid
	 * @param CarfNumber
	 * @param DeviceName
	 * @param State
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "query_operationrecords", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object query_operationrecords(
			@RequestParam String sessionid,
			@RequestParam(required = false) String CardNumber,
			@RequestParam(required = false) String DeviceName,
			@RequestParam(required = false) Integer State,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询操作历史记录");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("CardNumber", CardNumber);
		params.put("DeviceName", DeviceName);
		params.put("State", State);
		Integer totle = RecordsService.queryCountOperationRecords(params);
		List<Map<String, Object>> DistributionRecords = null;
		if (totle > 0) {
			DistributionRecords = RecordsService.queryOperationRecords(params);
		}
		map.put("totle", totle);
		map.put("DistributionRecords", DistributionRecords);

		return map;
	}
	
	/**
	 * 查询黑名单记录
	 * @param sessionid
	 * @param CarfNumber
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "query_blacklist", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object query_blacklist(
			@RequestParam String sessionid,
			@RequestParam(required = false) String CardNumber,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询黑名单记录");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("CardNumber", CardNumber);
		Integer totle = RecordsService.queryCountblacklist(params);
		List<Map<String, Object>> BlackLists = null;
		if (totle > 0) {
			BlackLists = RecordsService.queryblacklist(params);
		}
		map.put("totle", totle);
		map.put("BlackLists", BlackLists);
		return map;
	}
	
	
	/**
	 * 查询通行记录
	 * @param sessionid
	 * @param CardNumber
	 * @param DeviceNumber
	 * @param VerificationResult
	 * @param StartTime
	 * @param EndTime
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "query_authpassrecord", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object query_authpassrecord(
			@RequestParam String sessionid,
			@RequestParam(required = false) String CardNumber,
			@RequestParam(required = false) String DeviceNumber,
			@RequestParam(required = false) String VerificationResult,
			@RequestParam(required = false) String StartTime,
			@RequestParam(required = false) String EndTime,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询通行记录");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("CardNumber", CardNumber);
		params.put("DeviceNumber", DeviceNumber);
		params.put("VerificationResult", VerificationResult);
		params.put("StartTime", StartTime);
		params.put("EndTime", EndTime);
		Integer totle = RecordsService.queryCountauthpassrecord(params);
		List<Map<String, Object>> Records = null;
		if (totle > 0) {
			Records = RecordsService.queryauthpassrecord(params);
		}
		map.put("totle", totle);
		map.put("Records", Records);
		return map;
	}
	
}
