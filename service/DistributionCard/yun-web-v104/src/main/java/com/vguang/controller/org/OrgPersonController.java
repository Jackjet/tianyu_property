package com.vguang.controller.org;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vguang.service.ICardService;
import com.vguang.service.IPersonService;
import com.vguang.service.IRecordsService;
import com.vguang.service.IRuleService;
import com.vguang.service.ISyncService;
import com.vguang.utils.DateTimeUtil;
import com.vguang.utils.HttpUtil;
import com.vguang.utils.messageUtils.MessageUtils;
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@Controller
@RequestMapping("/orgman/person")
public class OrgPersonController {
	private Logger log = LoggerFactory.getLogger(OrgPersonController.class);
	@Autowired
	private IPersonService PersonService;
	@Autowired
	private ISyncService sysService;
	@Autowired
	private IRuleService RuleService;
	@Autowired
	private ICardService CardService;
	@Autowired
	private IRecordsService RecordsService ;
	
	/**
	 * 人员邀请码
	 * @param sessionid
	 * @param power
	 * @param GroupID
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "inviteperson", method = RequestMethod.POST)
	@ResponseBody
	public Object inviteperson(@RequestParam String sessionid,
			@RequestParam int power,
			@RequestParam(required = false) Integer GroupID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("人员邀请码生成power" + power);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 生成二维码图片
		JSONObject params = new JSONObject();
		OutputStream fout = null;
		try {
			if (power == 1) {
				params.put("path", "pages/index/index?power=" + power);
			} else {
				params.put("path", "pages/index/index?power=" + power + "&groupid=" + GroupID);
			}

			params.put("width", 300);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String wAppid = sysService.queryValueByKey("appid");
		String wSecret = sysService.queryValueByKey("secret");
		log.info("页面图片");
		String access_token = HttpUtil.getWxAccessToken(wAppid, wSecret, null);
		InputStream in = HttpUtil.getWxQrCode(access_token, params);
		File file = new File("/usr/local/nginx/html/temp2/visitor/");
		int bytes;
		byte[] buffer = new byte[1024];
		String filePath = null;
		// 图片请求路径
		if (!file.exists() && !file.isDirectory()) {
			log.info("//不存在");
			file.mkdir();
		} else {
			log.info("//目录存在");
		}
		String OrgidName = "inviteperson.png";
		filePath = sysService.queryValueByKey("invitesev");
		String inviteurl = sysService.queryValueByKey("inviteurl");
		filePath = filePath + OrgidName;
		String imgurl = inviteurl + OrgidName;
		String Url = HttpUtil.writeQrcode(in, filePath);
		log.info("写二维码后获得的String  Url==="+Url);
		map.put("imgurl", imgurl);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	
	/**
	 * 查询所有人员
	 * @param sessionid
	 * @param FullName
	 * @param PersonStatus
	 * @param GroupID
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "queryallperson", method = RequestMethod.POST)
	@ResponseBody
	public Object queryallperson(@RequestParam String sessionid, 
			@RequestParam(required = false) String FullName,
			@RequestParam(required = false) String PersonStatus,
			@RequestParam(required = false) Integer GroupID,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询所有人员业主:" + FullName);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("FullName", FullName);
		params.put("PersonStatus", PersonStatus);
		params.put("GroupID", GroupID);
		// 获取设备条数
		Integer totle = PersonService.queryCountPerson(params);
		List<Map<String, Object>> Persons = null;
		if (totle > 0) {
			Persons = PersonService.queryPerson(params);
		}
		map.put("totle", totle);
		map.put("Persons", Persons);
		return map;
	}
	
	/**
	 * 人员审批通知
	 * @param sessionid
	 * @param PersonID
	 * @param PersonStatus
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "personapproval", method = RequestMethod.POST)
	@ResponseBody
	public Object personapproval(@RequestParam String sessionid,
			@RequestParam int PersonID,
			@RequestParam int PersonStatus,
			@RequestParam(required = false) String reason,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("人员审批PersonID:" + PersonID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 走审批状态
		Integer row = PersonService.updatePersonStatusByPersonID(PersonStatus, PersonID);
		// 获取被审批人手机号码,用于短信通知
		String Phone = PersonService.queryPhoneByPersonID(PersonID);
		if (Phone == null) {
			log.info("当然人员未留手机号,跳过通知");
		} else {
			//获取当前时间
			String time = DateTimeUtil.nowDayhms();
			if (PersonStatus == 0) {
				log.info("审批通过,走成功通知");// 以下为模板,需要生成新的

				int templateId = 1032456;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[3];//

				contents[0] = "通过";
				contents[1] = time;
				contents[2] = "欢迎加入";
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
						phoneNumbers, contents);

			} else {
				log.info("审批拒绝,走失败通知");

				int templateId = 1032456;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[3];//

				contents[0] = "未通过";
				contents[1] = time;
				contents[2] = reason;
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
						phoneNumbers, contents);
			}
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 人员移除
	 * @param sessionid
	 * @param PersonID
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "delperson", method = RequestMethod.POST)
	@ResponseBody
	public Object delperson(@RequestParam String sessionid,
			@RequestParam int PersonID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("人员移除PersonID:" + PersonID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 验证人员是否为登陆管理员
		Integer AccountID = PersonService.queryAccountIDByPersonID(PersonID);
		if (AccountID != null) {
			result = -2;
			msg = "登陆角色不可删除";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}

		log.info("开始循环删除人员相关");
		// 移除权限
		Integer row = RuleService.delRuleByPersonID(PersonID);
		// 移除分组
		row = PersonService.delUserGroupByPersonID(PersonID);
		// 获取当前人员关联的卡号
		Integer[] Cardids = CardService.queryCardIDsByPersonID(PersonID);
		for (int i = 0; i < Cardids.length; i++) {
			Integer cardID = Cardids[i];
			 row = CardService.delete_card(cardID.toString());
		}
	
		// 移除人员
		 row  = PersonService.delPersonByPersonID(PersonID);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 人员编辑
	 * @param sessionid
	 * @param PersonID
	 * @param FullName
	 * @param PhoneNum
	 * @param Email
	 * @param PersonStatus
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "updateperson", method = RequestMethod.POST)
	@ResponseBody
	public Object updateperson(@RequestParam String sessionid,
			@RequestParam int PersonID,
			@RequestParam String FullName,
			@RequestParam String PhoneNum,
			@RequestParam String Email,
			@RequestParam int PersonStatus,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("人员编辑PersonID:" + PersonID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 走审批状态
		Integer row = PersonService.updatePersonByPersonID(FullName, PhoneNum, Email, PersonStatus, PersonID);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 查询人员详情
	 * @param sessionid
	 * @param PersonID
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "querypersondetails", method = RequestMethod.POST)
	@ResponseBody
	public Object querypersondetails(@RequestParam String sessionid, 
			@RequestParam Integer PersonID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询人员详情:" + PersonID);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> Persons  = PersonService.querypersondetails(PersonID);
		map.put("Persons", Persons);
		return map;
	}
	
	/**
	 * 移除家庭成员或家庭
	 * @param sessionid
	 * @param UserGroupID
	 * @param GroupID
	 * @param Flag
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "delusergroup", method = RequestMethod.POST)
	@ResponseBody
	public Object delusergroup(@RequestParam String sessionid, 
			@RequestParam(required = false) Integer UserGroupID,
			@RequestParam(required = false) Integer GroupID,
			@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("移除家庭或删除家庭Flag:" + Flag);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		if (Flag == 1) {
			log.info("删除分组");
			Integer row = PersonService.delUserGroup(GroupID);
			log.info("删除分组内人员所有权限");
			List<Map<String, Object>> maps = RuleService.queryPersonIDByGroupID(GroupID);
			for (int i = 0; i < maps.size(); i++) {
				Integer personID = (Integer) maps.get(i).get("PersonID");
				Integer integer = RuleService.delRuleByPersonID(personID);
			}
		} else {
			log.info("移除部门人员");
			Integer row = PersonService.delUserGroupByUserGroupID(UserGroupID);
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	
	/**
	 * 编辑家庭信息
	 * @param sessionid
	 * @param GroupID
	 * @param UserGroupName
	 * @param UserGroupDesc
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "updatedepartments", method = RequestMethod.POST)
	@ResponseBody
	public Object updatedepartments(@RequestParam String sessionid, 
			@RequestParam Integer GroupID,
			@RequestParam String UserGroupName,
			@RequestParam(required = false) String UserGroupDesc,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("修改家庭信息UserGroupName:" + UserGroupName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer row= PersonService.updateUserGroupByGroupID(GroupID,UserGroupName,UserGroupDesc);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 查询分组信息(或二级---三级分组信息等)
	 * @param sessionid
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "queryallusergroup", method = RequestMethod.POST)
	@ResponseBody
	public Object queryallusergroup(@RequestParam String sessionid, 
			@RequestParam(required = false) String PhoneNum,
			@RequestParam(required = false) String FullName,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			@RequestParam Integer PersonStatus,
			HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("PhoneNum", PhoneNum);
		params.put("PersonStatus", PersonStatus);
		params.put("GroupPower", 1);
		params.put("FullName", FullName);
		// 获取设备条数
		Integer totle = PersonService.queryCountGroup(params);
		List<Map<String, Object>> Groups = null;
		if (totle > 0) {
			Groups = PersonService.queryGroup(params);
		}
		map.put("totle", totle);
		map.put("Groups", Groups);

		return map;
	}

	/**
	 * 计次乘梯卡发卡选择框
	 * @param sessionid
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "queryAll", method = RequestMethod.POST)
	@ResponseBody
	public Object queryAll(@RequestParam String sessionid,@RequestParam Integer CardType){
		HashMap<String,Object> map =new HashMap<>();
		List<HashMap<String, Object>> hashMaps = PersonService.queryAll(CardType);
		for (int i = 0; i < hashMaps.size(); i++) {
			Integer personID = (Integer) hashMaps.get(i).get("PersonID");
			List<Map<String, Object>> rules = RuleService.queryRuleByPersonID(personID);
			hashMaps.get(i).put("rules",rules);
		}
		map.put("info",hashMaps);
	return map;
	}

//	/**
//	 * 乘梯卡发卡选择框
//	 * @param sessionid
//	 * @return
//	 */
//	@CrossOrigin
//	@RequestMapping(value = "queryAlls", method = RequestMethod.POST)
//	@ResponseBody
//	public Object queryAlls(@RequestParam String sessionid){
//		List<HashMap<String, Object>> hashMaps = PersonService.queryAll();
//		System.out.println(hashMaps);
//		return hashMaps;
//	}
	/**
	 * 编辑家庭信息
	 * @param sessionid
	 * @param GroupID
	 * @param UserGroupName
	 * @param
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "editGroup", method = RequestMethod.POST)
	@ResponseBody
	public Object editGroup(@RequestParam String sessionid,
									@RequestParam Integer GroupID,
									@RequestParam String UserGroupName,
									@RequestParam String FullName,
									@RequestParam String PhoneNum,
									HttpServletRequest request,
									HttpServletResponse response) {
		//根据组ID修改组内家庭地址
		Integer integer = PersonService.updateUserGroupNameByGroupID(GroupID, UserGroupName);
		//根据组ID查询到户主，修改户主的联系方式与电话
		Integer personid = PersonService.queryhouseholderByGroupidAndGroupPower(GroupID, 1);
		Integer PersonStatus = PersonService.queryPersonStatusByPersonID(personid);
		Integer integer1 = PersonService.updatePersonByPersonID(FullName, PhoneNum, null, PersonStatus, personid);
		Map<String, Object> map = new HashMap<>();
		Integer result = 0;
		String msg = "";

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

}
