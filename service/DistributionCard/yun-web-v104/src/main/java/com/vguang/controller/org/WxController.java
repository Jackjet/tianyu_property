package com.vguang.controller.org;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jhlabs.image.BinaryFilter;
import com.vguang.service.impl.DeviceService;
import com.vguang.utils.*;
import com.vguang.utils.encrypt.HMACUtil;
import org.apache.poi.hssf.record.DVALRecord;
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

import com.google.gson.Gson;
import com.vguang.eneity.Person;
import com.vguang.eneity.Visitor;
import com.vguang.eneity.WxPhone;
import com.vguang.eneity.WxUserInfo;
import com.vguang.service.ICardService;
import com.vguang.service.ILoginService;
import com.vguang.service.IPersonService;
import com.vguang.service.IRuleService;
import com.vguang.service.ISyncService;
import com.vguang.service.IVisitorService;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;
import com.vguang.utils.encrypt.AESUtil;
import com.vguang.utils.messageUtils.MessageUtils;

import redis.clients.jedis.Jedis;

@Controller
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@RequestMapping("/wx")
public class WxController {
	private Logger log = LoggerFactory.getLogger(WxController.class);
	@Autowired
	private IVisitorService VisitorService;
	@Autowired
	private CaptchaManager captchaManager;
	Gson gson = GsonUtil.getGson();
	@Autowired
	private ISyncService syncService;
	@Autowired
	private ILoginService loginService;
	@Autowired
	private JedisManager jedisManager;
	@Autowired
	private ICardService CardService;
	@Autowired
	private IPersonService PersonService;
	@Autowired
	private IRuleService RuleService;
	@Autowired
	private DeviceService deviceService;

	/**
	 * 小程序登陆
	 * @param code
	 * @param encryptedData
	 * @param iv
	 * @param appId
	 * @param model
	 * @param pixelRatio
	 * @param windowWidth
	 * @param windowHeight
	 * @param language
	 * @param version
	 * @param platform
	 * @param system
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Object checkUser(
			@RequestParam(required = false) String code,
			@RequestParam(required = false) String encryptedData,
			@RequestParam(required = false) String iv,
			@RequestParam String appId,
			@RequestParam(required = false) String model,
			@RequestParam(required = false) String pixelRatio,
			@RequestParam(required = false) String windowWidth,
			@RequestParam(required = false) String windowHeight,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String version,
			@RequestParam(required = false) String platform,
			@RequestParam(required = false) String system,
			//@RequestParam(required = false) Integer personid,
			HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, JSONException {
		Map<String, Object> map = new HashMap<>();
		Integer offlinecode = 0;
		//String sessionid = request.getParameter("sessionid");
		String sessionid = StringUtil.randStrs(36);
		log.info("小程序登录:code：{},iv:{},en:{},wxsid:{}", code, iv, encryptedData, sessionid);
		String uid = null;
		// 获取IP
		String remoteAddr = request.getRemoteAddr();
		String forwarded = request.getHeader("X-Forwarded-For");
		String ip = request.getHeader("X-Real-IP");
		// String ip = HttpUtil.getIP(remoteAddr, forwarded, realIp);
		log.info("addr:{}, forward:{}, ip:{}", remoteAddr, forwarded, ip);

		String msg = "";
		boolean result = false;

		// 初始化配置文件,获取小程序对应的唯一id、密钥
		String SpAppId = appId;
		String wxspSecret = null;
		wxspSecret = syncService.queryValueByKey("secret");
		String grant_type = syncService.queryValueByKey("secret");
		log.info("SpAppId:{}, wxspsecret:{}", SpAppId, wxspSecret);
		String openid = null;
		WxUserInfo wxuserinfo = null;
		Map<String, Object> Personmap = new HashMap<>();

		log.info("普通微信登陆");
		
		// 获取微信登陆人员参数
		try {
			// 通过参数信息获取密钥session_key、expires_in、openid
			StringBuilder sb = new StringBuilder();
			sb.append("appid=").append(SpAppId).append("&secret=").append(wxspSecret).append("&js_code=").append(code)
					.append("&grant_type=").append(grant_type);
			String seskeyToken = HttpUtil.sendGet("https://api.weixin.qq.com/sns/jscode2session", sb.toString());
			log.info("小程序获取session_key：{}", seskeyToken);
			JSONObject skey = JsonUtil.str2json(seskeyToken);
			String sesKey = skey.getString("session_key");
			 openid = skey.getString("openid");
			String userinfo = AESUtil.decryptWithBase64(encryptedData, sesKey, iv);
			log.info("小程序解密用户信息:{}", userinfo);
			if (null != userinfo && userinfo.length() > 0) {
				Gson gson = new Gson();
				wxuserinfo = gson.fromJson(userinfo, WxUserInfo.class);
				// 添加到WxInfo
				WxPhone wxphone = new WxPhone(model, pixelRatio, windowWidth, windowHeight, version, platform, system);
				wxuserinfo.setWxphone(wxphone);
			} else {
				log.error("===小程序解密失败===");
			}
		} catch (Exception e) {
			log.error("小程序登录异常：" + e);
		}
		log.info("wxuserinfo===" + wxuserinfo);
		log.info("wxuserinfo===" + wxuserinfo.getOpenId()+"lwb查看日志"+wxuserinfo.getNickName());
		wxuserinfo.setIpaddress(ip);
		//openid = wxuserinfo.getOpenId();
		log.info("openid====" + openid);
		// 判断微信人员是否存在
		String WxUnionID = wxuserinfo.getUnionId();
		Integer personid = PersonService.queryPersonIDByWxUnionID(openid);
		log.info("-------------------------------------------------------------------------------------------------------------------------------------------"+personid);
		String nickName = wxuserinfo.getNickName();;
		if (personid == null) {
			log.info("新增人员");
			String unionid = wxuserinfo.getUnionId();
			Integer PersonStatus = 3;
			Person person = new Person(nickName, PersonStatus,openid);
			Integer row = PersonService.addStrangerPerson(person);
			personid = person.getPersonID();
			log.info("新增微信数据表");
			wxuserinfo.setPersonid(personid);
			if(wxuserinfo.getOpenId()==null){
				log.info("openid为空,添加openid");
				wxuserinfo.setOpenId(openid);
			}
			log.info("添加wxuserinfo前-------------openid="+wxuserinfo.getOpenId());
			row = PersonService.addWxUser(wxuserinfo);
		} else {
			log.info("当前人员已登录,更新微信数据表");
			if(wxuserinfo.getOpenId()==null){
				log.info("openid为空,添加openid");
				wxuserinfo.setOpenId(openid);
			}
			Integer row = PersonService.modWxUser(wxuserinfo);
		}
		// 获取人员基本信息
		Integer PersonStatus = PersonService.queryPersonStatusByPersonID(personid);
		if (PersonStatus == 0) {
			log.info("正常状态人员,获取个人信息");
			String FullName = PersonService.queryFullNameByPersonID(personid);
			// 获取家庭信息
			List<Map<String, Object>> UserGroups = PersonService.queryUserGroupByPersonID(personid);
			map.put("FullName", FullName);
			map.put("UserGroups", UserGroups);
			map.put("PersonStatus", PersonStatus);
			map.put("PersonID", personid);
		} else {
			log.info("非正常状态,直接返回状态码");
			msg = "当前陌生人状态,请扫物业人员二维码或家庭二维码录入个人信息在进行登录";
			map.put("PersonStatus", PersonStatus);
		}

		result = true;
		jedisManager.setValueByStr(1, sessionid, personid.toString(), 7200);
		map.put("AvatarUrl", wxuserinfo.getAvatarUrl());
		map.put("sessionid", sessionid);
		map.put("NickName", nickName);
		map.put("personid", personid);
		map.put("result", result);
		return map;

	}
	
	/**
	 * 人员录入
	 * @param sessionid
	 * @param PersonID
	 * @param FullName
	 * @param Email
	 * @param PhoneNum
	 * @param GroupPower
	 * @param UserGroupName
	 * @param GroupID
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "insertperson", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object insertrule(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			@RequestParam String FullName,
			@RequestParam(required = false) String Email,
			@RequestParam String PhoneNum,
			@RequestParam Integer GroupPower,
			@RequestParam(required = false) String UserGroupName,
			@RequestParam(required = false) Integer GroupID,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("人员录入FullName--->" + FullName);
		String decodeFullName = URLDecoder.decode((new String(FullName.getBytes("ISO8859-1"), "UTF-8")), "UTF-8");
		String decodeUserGroupName = URLDecoder.decode((new String(UserGroupName.getBytes("ISO8859-1"), "UTF-8")), "UTF-8");
		log.info("人员录入decodeFullName--->" + decodeFullName);
		log.info("人员录入decodeUserGroupName--->" + decodeUserGroupName);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		// 判断分组是否重复
		Integer groupID = PersonService.queryGroupIDByUserGroupName(decodeUserGroupName);
		if (groupID != null) {
			result = -2;
			msg = "家庭名称重复";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}

		// 修改人员信息,并将状态改为待审批
		Integer PersonStatus = 2;// 0 正常 1失败 2申请中 3 陌生人状态
		Integer row = PersonService.updatePersonByPersonID(decodeFullName, PhoneNum, Email, PersonStatus, PersonID);
		Integer Status = 1;// 成员状态 1带审批 2成功 3失败
		if (GroupPower == 1) {
			log.info("户主录入,新增家庭分组");
			// 获取最大GrouoID
			GroupID = PersonService.queryMaxGroupID();
			Integer ParentUserGroupID = -1;
			String UserGroupDesc = null;
			row = PersonService.insertGroup(null, ParentUserGroupID, decodeUserGroupName, UserGroupDesc, GroupID,
					GroupPower, Status);
			
			 ParentUserGroupID = GroupID;
			 UserGroupDesc = null;
			row = PersonService.insertGroup(PersonID, ParentUserGroupID, decodeUserGroupName, UserGroupDesc, GroupID,
					GroupPower, Status);
			
			
			// 短信通知管理员进行审批
			//获取管理员手机号
			String Phone = syncService.queryValueByKey("adminphone");
			String time = DateTimeUtil.nowDayhms();
			int templateId = 1032894;
			String[] phoneNumbers = new String[1];
			phoneNumbers[0] = Phone;
			String[] contents = new String[2];//

			contents[0] = decodeFullName;
			contents[1] = time;
			row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技", phoneNumbers,
					contents);
			
			

		} else {
			log.info("成员录入,加入家庭分组");
			// 获取户主id
			GroupPower = 1; //
			Integer householderID = PersonService.queryhouseholderByGroupidAndGroupPower(GroupID, GroupPower);
			if (householderID == null) {
				result = -2;
				msg = "当前家庭没有户主,请优先分配户主";
				map.put("result", result);
				map.put("msg", msg);
				return map;
			}

			Integer ParentUserGroupID = GroupID;
			String UserGroupDesc = null;
			row = PersonService.insertGroup(PersonID, ParentUserGroupID, decodeUserGroupName, UserGroupDesc, GroupID,
					GroupPower, Status);
			// 短信通知户主进行审批
			// 获取户主手机号
			String Phone = PersonService.queryPhoneByPersonID(householderID);
			if (Phone == null) {
				log.info("当然人员未留手机号,跳过通知");
				result = -2;
				msg = "申请的户主未留手机号,跳过短信通知,请主动联系";
				map.put("result", result);
				map.put("msg", msg);
				return map;
			} else {
				String time = DateTimeUtil.nowDayhms();
				int templateId = 1032894;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[2];//

				contents[0] = decodeFullName;
				contents[1] = time;
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技", phoneNumbers,
						contents);
			}
			

		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 查询业主权限
	 * @param sessionid
	 * @param PersonID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getownerrule", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object getownerrule(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询业主权限PersonID---"+PersonID);
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> Rules = RuleService.queryRuleByPersonID(PersonID);
		map.put("Rules", Rules);
		return map;
	}
	
	/**
	 * 查询业主详情
	 * @param sessionid
	 * @param PersonID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getownerdetails", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object getownerdetails(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询业主详情PersonID---" + PersonID);
		Map<String, Object> map = new HashMap<>();
		// 获取个人信息
		HashMap<String, Object> Person = PersonService.queryPersonByPersonID(PersonID);
		// 获取家庭信息
		List<Map<String, Object>> UserGroups = PersonService.queryUserGroupByPersonID(PersonID);
		// 获取权信息
		List<Map<String, Object>> Rules = RuleService.queryRuleByPersonID(PersonID);
		map.put("Rules", Rules);
		map.put("UserGroups", UserGroups);
		map.put("Person", Person);
		return map;
	}
	
	
	/**
	 * 查询人员家庭详情
	 * @param sessionid
	 * @param PersonID
	 * @param GroupID
	 * @param Flag
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "queryfamily", method = RequestMethod.POST)
	@ResponseBody
	public Object queryfamily(
			@RequestParam String sessionid,
			@RequestParam(required = false) Integer PersonID,
			@RequestParam(required = false) Integer GroupID,
			@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询业主家庭详情PersonID---" + PersonID);
		//List<Map<String, Object>> map = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> Persons = new ArrayList<Map<String, Object>>();
		Integer[] Personids = PersonService.queryAllPersonidByGroupID(GroupID);

		if (Flag == 1) {
			log.info("根据人员查询数据-->一条数据");
			HashMap<String, Object> Person = PersonService.queryPersonDetailedByPersonID(PersonID);
			Persons.add(0, Person);
		} else {
			log.info("根据分组查询数据-->一条数据");

			for (int i = 0; i < Personids.length; i++) {

				PersonID = Personids[i];
				log.info("+++++++++++"+PersonID);
				HashMap<String, Object> Person = PersonService.queryPersonDetailedByPersonID(PersonID);
				Persons.add(i, Person);
			}

		}
		map.put("Persons", Persons);
		return map;
	}
	
	/**
	 * 户主审批
	 * @param sessionid
	 * @param UserGroupID
	 * @param Status
	 * @param
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updategroupstatus", method = RequestMethod.POST)
	@ResponseBody
	public Object updategroupstatus(
			@RequestParam String sessionid,
			@RequestParam Integer UserGroupID,
			@RequestParam Integer PersonID,//户主id
			@RequestParam Integer PassivePersonID,//成员id
			@RequestParam Integer Status,
			//@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("户主审批UserGroupID---" + UserGroupID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer row = PersonService.updateGroupStatusByUserGroupID(UserGroupID, Status);
		// 获取人员的手机号码
		String Phone = PersonService.queryPhoneByPersonID(PassivePersonID);
		if (Phone != null) {
			log.info("短信通知审批结果Phone:" + Phone);
			//获取当前分组名称
			String UserGroupName= PersonService.queryUserGroupNameByUserGroupID(UserGroupID);
			if (Status == 2) {
				log.info("发送成功短信,并同步户主权限");
				List<Map<String, Object>> Rule = RuleService.queryRuleByPersonID(PersonID);
				for (int i = 0; i < Rule.size(); i++) {
					Integer DeviceID = (Integer) Rule.get(i).get("DeviceID");
					String LiftRule = Rule.get(i).get("LiftRule").toString();
					row = RuleService.addRule(PassivePersonID, DeviceID, null, null, LiftRule);
				}
				log.info("发送成功短信");

				int templateId = 1033605;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[3];//

				contents[0] = UserGroupName;
				contents[1] = "通过";
				contents[2] = "欢迎加入";
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
						phoneNumbers, contents);
			} else {
				log.info("发送拒绝短信");
				int templateId = 1033605;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[3];//

				contents[0] = UserGroupName;
				contents[1] = "未通过";
				contents[2] = "请确认信息后在申请";
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
						phoneNumbers, contents);
			}

		}else {
			result = 1;
			msg = "审批成功,通知失败";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 权限手动同步
	 * @param sessionid
	 * @param PersonID
	 * @param PassivePersonID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "grouprulesync", method = RequestMethod.POST)
	@ResponseBody
	public Object grouprulesync(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			@RequestParam Integer PassivePersonID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("同步户主权限PersonID--" + PersonID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 判断当前人员是否为家庭成员角色
		List<Map<String, Object>> Rule = RuleService.queryRuleByPersonID(PassivePersonID);
		for (int i = 0; i < Rule.size(); i++) {
			Integer DeviceID = (Integer) Rule.get(i).get("DeviceID");
			String LiftRule = Rule.get(i).get("LiftRule").toString();
			Integer row = RuleService.addRule(PersonID, DeviceID, null, null, LiftRule);
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
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
	 * 访客提交
	 * @param sessionid
	 * @param PersonID
	 * @param VisitorApplyName
	 * @param VisitorBeginTime
	 * @param VisitorEndTime
	 * @param VisitorType
	 * @param VisitorApplyPhone
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "visitorsubmit", method = RequestMethod.POST)
	@ResponseBody
	public Object visitorsubmit(
			@RequestParam String sessionid,
			@RequestParam Integer PersonID,
			@RequestParam String VisitorApplyName,
			@RequestParam String VisitorBeginTime,
			@RequestParam String VisitorEndTime,
			@RequestParam Integer VisitorType,
			@RequestParam String VisitorApplyPhone,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("访客提交VisitorApplyName" + VisitorApplyName);
		String decodeVisitorApplyName = URLDecoder.decode((new String(VisitorApplyName.getBytes("ISO8859-1"), "UTF-8")), "UTF-8");
		log.info("访客提交VisitorApplyName转码后-------" + decodeVisitorApplyName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer VisitorStatus = 2;// 访客状态 0 正常 1失败 2申请中
		Visitor Visitor = new Visitor(PersonID, decodeVisitorApplyName, VisitorBeginTime, VisitorEndTime, VisitorType,
				VisitorApplyPhone, VisitorStatus);
		Integer row = VisitorService.insertvisitor(Visitor);
		Integer VisitorApplyID = Visitor.getVisitorApplyID();
		if (VisitorApplyID != null) {
			log.info("同步被访人权限至访客");
			// 判断当前人员是否为家庭成员角色
			List<Map<String, Object>> Rule = RuleService.queryRuleByPersonID(PersonID);
			System.out.println(Rule.toString());
			for (int i = 0; i < Rule.size(); i++) {
				Integer DeviceID = (Integer) Rule.get(i).get("DeviceID");
				System.out.println("DeviceID===="+DeviceID);
				String LiftRule = Rule.get(i).get("LiftRule").toString();
				System.out.println("LiftRule===="+LiftRule);
				row = VisitorService.insertVisitorApplyRule(DeviceID, VisitorApplyID, LiftRule, VisitorBeginTime,
						VisitorEndTime);
				System.out.println(row);
			}
			// 权限同步完成,通知被访人进行审批
			// 获取被访人的手机号码
			String Phone = PersonService.queryPhoneByPersonID(PersonID);
			String time = DateTimeUtil.nowDayhms();
			if (Phone != null) {
				log.info("发送短信");
				int templateId = 1033634;
				String[] phoneNumbers = new String[1];
				phoneNumbers[0] = Phone;
				String[] contents = new String[4];//

				contents[0] = decodeVisitorApplyName;
				contents[1] = time;
				contents[2] = VisitorBeginTime;
				contents[3] = VisitorEndTime;
				row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
						phoneNumbers, contents);

			} else {
				result = -2;
				msg = "被访人手机号错误,无法通知,申请已提交,请自主联系被访人";
			}
			result = 0;
		} else {
			result = -1;
		}

		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 修改访客信息,审批访客
	 * @param sessionid
	 * @param VisitorApplyID
	 * @param VisitorApplyName
	 * @param VisitorBeginTime
	 * @param VisitorEndTime
	 * @param VisitorStatus
	 * @param VisitorApplyPhone
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updatevisitor", method = RequestMethod.POST)
	@ResponseBody
	public Object updatevisitor(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyID,
			@RequestParam String VisitorApplyName,
			@RequestParam String VisitorBeginTime,
			@RequestParam String VisitorEndTime,
			@RequestParam Integer VisitorStatus,
			@RequestParam String VisitorApplyPhone,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("修改访客信息,审批访客VisitorApplyName" + VisitorApplyName);
		String decodeVisitorApplyName = URLDecoder.decode((new String(VisitorApplyName.getBytes("ISO8859-1"), "UTF-8")), "UTF-8");
		log.info("修改访客信息,审批访客decodeVisitorApplyName转码--------" + decodeVisitorApplyName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 修改访客基本信息
		Integer row = VisitorService.updatevisitor(VisitorApplyID, decodeVisitorApplyName, VisitorBeginTime, VisitorEndTime,
				VisitorStatus, VisitorApplyPhone);
		// 修改权限时间
		row = VisitorService.updateVisitorTimeByVisitorApplyID(VisitorApplyID, VisitorBeginTime, VisitorEndTime);
		//通知访客,发送生码链接进行访客码生成
		int templateId = 1033672;
		String[] phoneNumbers = new String[1];
		phoneNumbers[0] = VisitorApplyPhone;
		String[] contents = new String[4];//
		contents[0] = decodeVisitorApplyName;
		contents[1] = "连接待定";
		contents[2] = VisitorBeginTime;
		contents[3] = VisitorEndTime;

		log.info("执行完713行----------------------------------");
		row = MessageUtils.Message(1400212869, "963b7c07c59ee9d37672dac3ae9157ae", templateId, "闪卡科技",
				phoneNumbers, contents);
		
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
	@RequestMapping(value = "getvisitorrule", method = RequestMethod.POST)
	@ResponseBody
	public Object getvisitorrule(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("获取访客权限VisitorApplyID" + VisitorApplyID);
		Map<String, Object> map = new HashMap<>();
		List<HashMap<String, Object>> VisitorRules = VisitorService.getvisitorrule(VisitorApplyID);
		map.put("VisitorApplyRule", VisitorRules);

		return map;
	}
	
	/**
	 * 访客权限删除
	 * @param sessionid
	 * @param
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delvisitorrule", method = RequestMethod.POST)
	@ResponseBody
	public Object delvisitorrule(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyRuleID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("删除访客权限VisitorApplyRuleID" + VisitorApplyRuleID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer row = VisitorService.delvisitorrule(VisitorApplyRuleID);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 增加访客权限
	 * @param sessionid
	 * @param VisitorApplyID
	 * @param DeviceID
	 * @param LiftRule
	 * @param PersonID
	 * @param VisitorBeginTime
	 * @param VisitorEndTime
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "addvisitorrule", method = RequestMethod.POST)
	@ResponseBody
	public Object addvisitorrule(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyID,
			@RequestParam Integer DeviceID,
			@RequestParam String LiftRule,
			@RequestParam Integer PersonID,
			@RequestParam String VisitorBeginTime,
			@RequestParam String VisitorEndTime,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("增加访客权限VisitorApplyID" + VisitorApplyID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 获取被访人权限,用于验证访客权限
		String PersonID_LiftRule = RuleService.queryLiftRuleByDevice(DeviceID,PersonID);
		if (PersonID_LiftRule == null) {
			log.info("被访人没有此权限,超出范围");
			result = -2;
			msg = "被访人没有此权限,超出范围";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		} else {
			log.info("验证楼层是否有超出范围");
			List<String> LiftRule_list = Arrays.asList(LiftRule.split(","));
			List<String> PersonID_LiftRule_list = Arrays.asList(PersonID_LiftRule.split(","));
			for (int i = 0; i < LiftRule_list.size(); i++) {
				String Int_LiftRule = LiftRule_list.get(i);
				boolean r = PersonID_LiftRule_list.contains(Int_LiftRule);
				if (r == false) {
					log.info("被访人没有此楼层权限,超出范围");
					result = -2;
					msg = "被访人没有此楼层权限,超出范围";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				} else {
					log.info("权限符合要求,开始增加权限");
					Integer row = VisitorService.insertVisitorApplyRule(DeviceID, VisitorApplyID, LiftRule,
							VisitorBeginTime, VisitorEndTime);

				}
			}
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	
	/**
	 * 修改访客权限
	 * @param sessionid
	 * @param VisitorApplyRuleID
	 * @param VisitorApplyID
	 * @param DeviceID
	 * @param LiftRule
	 * @param PersonID
	 * @param VisitorBeginTime
	 * @param VisitorEndTime
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updatevisitorrule", method = RequestMethod.POST)
	@ResponseBody
	public Object updatevisitorrule(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyRuleID,
			@RequestParam Integer VisitorApplyID,
			@RequestParam Integer DeviceID,
			@RequestParam String LiftRule,
			@RequestParam Integer PersonID,
			@RequestParam String VisitorBeginTime,
			@RequestParam String VisitorEndTime,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("增加访客权限VisitorApplyID" + VisitorApplyID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 获取被访人权限,用于验证访客权限
		String PersonID_LiftRule = RuleService.queryLiftRuleByDevice(DeviceID,PersonID);
		if (PersonID_LiftRule == null) {
			log.info("被访人没有此权限,超出范围");
			result = -2;
			msg = "被访人没有此权限,超出范围";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		} else {
			log.info("验证楼层是否有超出范围");
			List<String> LiftRule_list = Arrays.asList(LiftRule.split(","));
			List<String> PersonID_LiftRule_list = Arrays.asList(PersonID_LiftRule.split(","));
			for (int i = 0; i < LiftRule_list.size(); i++) {
				String Int_LiftRule = LiftRule_list.get(i);
				boolean r = PersonID_LiftRule_list.contains(Int_LiftRule);
				if (r == false) {
					log.info("被访人没有此楼层权限,超出范围");
					result = -2;
					msg = "被访人没有此楼层权限,超出范围";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				} else {
					log.info("权限符合要求,修改权限数据");
					Integer row = VisitorService.updateVisitorRuleByID(VisitorApplyRuleID, DeviceID, VisitorApplyID,
							LiftRule, VisitorBeginTime, VisitorEndTime);

				}
			}
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

	/**
	 *生成访客通行码
	 * @return
	 */
	@RequestMapping(value = "getvisitorrules", method = RequestMethod.POST)
	@ResponseBody
	public Object getvisitorrules(
		@RequestParam String sessionid,
		@RequestParam Integer VisitorApplyID,//访客记录ID获取访客权限
		//@RequestParam Integer PersonID,//人员id获取人员权限
		@RequestParam Integer CardType,
		@RequestParam Integer CardSonType,
		@RequestParam String CardValidPeriod,//卡片有效期
		@RequestParam String Frequency,//计次卡次数
		@RequestParam(required = false) String Supply_CardNumber,
		HttpServletRequest request,
		HttpServletResponse response) throws Exception {
		log.info("获取访客权限VisitorApplyID" + VisitorApplyID);
		Map<String, Object> map = new HashMap<>();
		List<HashMap<String, Object>> VisitorRules = VisitorService.getvisitorrule(VisitorApplyID);
		List<HashMap<String, Object>> all_powers_list_map = new ArrayList<>();
		// 获取所有权限的总条数----以层为单位 1号楼1层 1号楼2层
		int totle = 0;
		for (int i = 0; i < VisitorRules.size(); i++) {
			String deviceid = VisitorRules.get(i).get("DeviceID").toString();
			String liftRules = VisitorRules.get(i).get("LiftRule").toString();
			//String personID = VisitorRules.get(i).get("VisitorApplyID").toString();
			//String startTime = VisitorRules.get(i).get("StartTime").toString();
			//String endTime = VisitorRules.get(i).get("EndTime").toString();
			List<String> LiftRule_list = Arrays.asList(liftRules.split(","));
			for (int j = 0; j < LiftRule_list.size(); j++) {
				String LiftRule = LiftRule_list.get(j);
				Integer power_len = Integer.valueOf(LiftRule);
				//log.info("计算偏值");// 因协议中的楼层00为一层
				// 获取设备偏执
				Integer FloorDifference = deviceService.queryFloorDifferenceByDeviceID(Integer.valueOf(deviceid));
				if (null == FloorDifference) {
					log.info("偏执为空,赋值为0");
					FloorDifference = 0;
				}
				// 计算楼层权限
				if (power_len == 0) {
					log.info("0代表所有权限,所有无需计算");
				} else {
					if (power_len < 0) {
						power_len = power_len + FloorDifference + 1 - 1;// +1是负数偏值 -1是协议00位为1层
						log.info("power_len----" + power_len);
					} else {
						power_len = power_len + FloorDifference - 1;// -1是协议00位为1层
					}
				}

				HashMap<String, Object> power = new HashMap<>();
				power.put("LiftRule", power_len);
				power.put("deviceid", deviceid);
				//power.put("startTime", startTime);
				//power.put("endTime", endTime);

				all_powers_list_map.add(i, power);
				totle = totle + 1;
			}
		}

		System.out.println("-----------------------------------------------------"+all_powers_list_map.toString());
		log.info("处理之后的数据为:" + all_powers_list_map);
		// 定义数据
		//String head = ByteUtil.convertStringToHex("vgrltk");
		byte[] len_byte = new byte[2]; // 长度
		// 一个扇区3块 一块16字节
		// 第一扇区第一块啊
		byte[] village_code = new byte[3];// 小区码
		byte[] flag = ByteUtil.Str16toBytes("00"); // 1字节标识
		byte[] card_number = new byte[2]; // 2字节卡号
		byte[] flag_2 = new byte[2]; // 2字节标识//

		byte[] first_powor = new byte[8];// 8字节的扇区第一块权限
		// 第一扇区第二块啊
		byte[] second_powor = new byte[8];// 8字节的扇区第二块权限
		byte[] card_type = new byte[8];// 8字节的卡号类型
		// 第一扇区第三块啊
		byte[] third_powor = new byte[2];// 2字节的扇区第三块权限
		byte[] powor_lenth = new byte[4];// 4字节的总权限大致长度
		// 0X02当楼层权限超过 13 组时，写 0X06，否则写 0X00
		// 0X03当楼层权限超过 37 组时，写 0X07，否则写 0X00
		// 0X04当楼层权限超过 61 组时，写 0X08，否则写 0X00
		// 0X05当楼层权限超过 85 组时，写 0X09，否则写 0X00
		//Bit0—bit4:有效日，（1--31）,bit5-bit7，默认001
		//0X01  本条以及上面一条对应flag_3
		byte[] flag_3 =  new byte[2]; // 2字节标识
		CardValidPeriod.substring(8,9);
		byte[] bytes3 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(8, 10)));
		bytes3[0]=(byte) (bytes3[0] | (0 << 5));
		bytes3[0]=(byte) (bytes3[0] | (0 << 6));
		bytes3[0]=(byte) (bytes3[0] | (1 << 7));
		flag_3[0]=bytes3[0];
		log.info("有效日-------------"+flag_3[0]);
		flag_3[1]=(byte) 0x01;

		byte[] forth_powor = new byte[8];// 8字节的扇区第二块权限
		// 以下扇区的长度根据权限长度定,无需列举
		// ---------------------第一扇区第一块----------------
		village_code = ByteUtil.Str16toBytes("010101");//小区码暂时写死
		log.info("小区码3位=-=="+village_code[0]+"---"+village_code[1]+"---"+village_code[2]);
		card_number =ByteUtil.unlong2H2bytes(Integer.valueOf(VisitorApplyID));
		//log.info("卡号字段=-=="+card_number[0]+"---"+card_number[1]+"---"+card_number[2]+"---"+card_number[3]);
		byte[] bytes1 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(2, 4)));
		flag_2[0]= bytes1[0];
		log.info("有效年份字段=-=="+flag_3[0]);
		byte[] bytes2 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(5, 7)));
		bytes2[0]=(byte) (bytes2[0] | (1 << 4));
		bytes2[0]=(byte) (bytes2[0] | (0 << 5));
		bytes2[0]=(byte) (bytes2[0] | (0 << 6));
		bytes2[0]=(byte) (bytes2[0] | (1 << 7));
		flag_2[1]=bytes2[0];
		log.info("有效月份字段=-=="+flag_3[1]);
		int all_powers_list_map_lenth = all_powers_list_map.size();
		log.info("------------------" + all_powers_list_map_lenth);
		int first = 0;
		for (int i = 0; i <= 3; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
				//log.info("LiftRule====="+LiftRule);
				if (LiftRule.equals("0")) {
					log.info("整楼权限，直接赋值bit7置1");
					//电梯组
					//byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					//first_powor_new[0] = (byte) (first_powor_new[0] | (1 << 7));
					first_powor[first * 2] = (byte) 0x7F;
					//楼层
					//byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					first_powor[first * 2 + 1] = (byte) 0x80;//所有楼层均有权限
				} else {
					if (CardSonType==0) {//标识自动登记楼层。不清楚怎么判断先做test
						//电梯组
						log.info("deviceid值===" + deviceid);
						byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						log.info("进入s==1:first_powor_new前===" + first_powor_new[0]);
						first_powor_new[0] = (byte) (first_powor_new[0] | (1 << 7));
						log.info("进入s==1:first_powor_new后===" + first_powor_new[0]);
						first_powor[first * 2] = first_powor_new[0];
						//楼层
						log.info("LiftRule权限值===" + LiftRule);
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						first_powor[first * 2 + 1] = LiftRule_new[0];
					} else {//手动登记楼层
						//电梯组
						byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						first_powor_new[0] = (byte) (first_powor_new[0] | (0 << 7));
						first_powor[first * 2] = first_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						first_powor[first * 2 + 1] = LiftRule_new[0];
					}

				}

			} else {
				log.info("开始执行自动补0");
				first_powor[first * 2] = (byte) 0x00;
				first_powor[first * 2 + 1] = (byte) 0x00;
			}
			first = first + 1;
		}
		System.out.println("first_powor：：" + ByteUtil.byte2Hex(first_powor));
		for (byte b : first_powor) {
			System.out.println("B:" + b);
		}
		// ---------------------第一扇区第二块----------------
		// 权限8字节
		int second = 0;
		for (int i = 4; i <= 7; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
				if (LiftRule.equals("0")) {
					log.info("整楼权限，直接赋值bit7置1");
					//电梯组
					byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					second_powor_new[0] = (byte) (second_powor_new[0] | (1 << 7));
					second_powor[second * 2] = second_powor_new[0];
					//楼层
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					second_powor[second * 2 + 1] = (byte) 0x80;//所有楼层均有权限
				} else {
					if (CardSonType==0) {//标识自动登记楼层
						//电梯组
						log.info("deviceid值===" + deviceid);
						byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						log.info("进入s==1:second_powor_new前===" + second_powor_new[0]);
						second_powor_new[0] = (byte) (second_powor_new[0] | (1 << 7));
						log.info("进入s==1:second_powor_new后===" + second_powor_new[0]);
						second_powor[second * 2] = second_powor_new[0];
						//楼层
						log.info("LiftRule权限值===" + LiftRule);
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						second_powor[second * 2 + 1] = LiftRule_new[0];
					} else {//手动登记楼层
						//电梯组
						byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						second_powor_new[0] = (byte) (second_powor_new[0] | (0 << 7));
						second_powor[second * 2] = second_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						second_powor[second * 2 + 1] = LiftRule_new[0];
					}
				}

			} else {
				log.info("开始执行自动补0");
				second_powor[second * 2] = (byte) 0x00;
				second_powor[second * 2 + 1] = (byte) 0x00;
			}
			second = second + 1;
			log.info("本次循环结束");
		}
		System.out.println("second_powor：：" + ByteUtil.byte2Hex(second_powor));
		for (byte b : second_powor) {
			System.out.println("B:" + b);
		}
		//卡类型8字节
		if(CardType==1){//乘梯卡
			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
			card_type[0] = b_CardType[0];
			if(CardSonType==1){
				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
				b_CardSonType[0]=(byte) (b_CardSonType[0] | (0 << 0));
				b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
				card_type[1] = b_CardSonType[0];
				log.info("开类型第2字段——————"+card_type[1]);
			}
			if(CardSonType==2) {
				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
				b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
				b_CardSonType[0] = (byte) (b_CardSonType[0] | (0 << 1));
				card_type[1] = b_CardSonType[0];
			}
			card_type[2] = (byte) 0X17;
			//log.info("开类型第3字段——————"+card_type[2]);
			card_type[3] = (byte) 0X3B;
			//log.info("开类型第4字段——————"+card_type[3]);
			card_type[4] = (byte) 0X00;
			card_type[5] = (byte) 0X00;
			card_type[6] = (byte) 0X00;
			card_type[7] = (byte) 0X00;
		}
		if(CardType==2){//乘梯卡计次数
			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
			card_type[0] = b_CardType[0];
			if(CardSonType==1){
				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
				b_CardSonType[0]=(byte) (b_CardSonType[0] | (0 << 0));
				b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
				card_type[1] = b_CardSonType[0];
				log.info("开类型第2字段——————"+card_type[1]);
			}
			if(CardSonType==2) {
				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
				b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
				b_CardSonType[0] = (byte) (b_CardSonType[0] | (0 << 1));
				card_type[1] = b_CardSonType[0];
			}
			card_type[2] = (byte) 0X17;
			log.info("开类型第3字段——————"+card_type[2]);
			card_type[3] = (byte) 0X3B;
			log.info("开类型第4字段——————"+card_type[3]);
			if(Frequency!=null){
				byte[] bytes = ByteUtil.unlong2H2bytes(Integer.valueOf(Frequency));
				card_type[4]=bytes[0];
				log.info("开类型第5字段——————"+card_type[4]);
				card_type[5]=bytes[1];
				log.info("开类型第6字段——————"+card_type[5]);
			}else {
				card_type[4] = (byte) 0x00;
				card_type[5] = (byte) 0x00;
			}


		}
		if(CardSonType==3){
			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
			card_type[0] = b_CardType[0];
			card_type[1] = (byte) 0X00;
			card_type[2] = (byte) 0X00;
			card_type[3] = (byte) 0X00;
			card_type[4] = (byte) 0X00;
			card_type[5] = (byte) 0X00;
			card_type[6] = (byte) 0X00;
			card_type[7] = (byte) 0X00;
		}
		if(CardSonType==4||CardSonType==5){
			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
			card_type[0] = b_CardType[0];
			card_type[1]=(byte) 0X00;
			//Supply_CardNumber传入的挂失或者恢复卡号
			byte[] bytes = ByteUtil.unlong2H4bytes(Integer.valueOf(Supply_CardNumber));
			card_type[2]=bytes[0];
			card_type[3]=bytes[1];
			card_type[4]=bytes[2];
			card_type[5]=bytes[3];
			card_type[6]=(byte) 0X00;
			card_type[7]=(byte) 0X00;

		}
		System.out.println("card_type：：" + ByteUtil.byte2Hex(card_type));
		// ---------------------第一扇区第三块----------------
		if (all_powers_list_map_lenth > 8) {
			log.info("第九组权限");
			String deviceid = all_powers_list_map.get(8).get("deviceid").toString();
			String LiftRule = all_powers_list_map.get(8).get("LiftRule").toString();
			if (LiftRule.equals("0")) {
				log.info("整楼权限，直接赋值bit7置1");
				//电梯组
				byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
				third_powor_new[0] = (byte) (third_powor_new[0] | (1 << 7));
				third_powor[0] = third_powor_new[0];
				//楼层
				byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
				third_powor[1] = (byte) 0x80;//所有楼层均有权限
			} else {
				if (CardSonType==0) {//标识自动登记楼层
					//电梯组
					log.info("deviceid值===" + deviceid);
					byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					log.info("进入s==1:third_powor_new前===" + third_powor_new[0]);
					third_powor_new[0] = (byte) (third_powor_new[0] | (1 << 7));
					log.info("进入s==1:third_powor_new后===" + third_powor_new[0]);
					third_powor[0] = third_powor_new[0];
					//楼层
					log.info("LiftRule权限值===" + LiftRule);
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
					LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
					log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
					third_powor[1] = LiftRule_new[0];
				} else {//手动登记楼层
					//电梯组
					byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					third_powor_new[0] = (byte) (third_powor_new[0] | (0 << 7));
					third_powor[0] = third_powor_new[0];
					//楼层
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
					third_powor[1] = LiftRule_new[0];
				}
			}

		}
		System.out.println("third_powor：：" + ByteUtil.byte2Hex(third_powor));
		// 获取总楼层权限
		log.info("超出13组后的一次判断赋值模块");
		if (all_powers_list_map_lenth > 13) {
			powor_lenth[0] = (byte) 0x06;
		} else if (all_powers_list_map_lenth > 37) {
			powor_lenth[1] = (byte) 0x07;
		} else if (all_powers_list_map_lenth > 61) {
			powor_lenth[2] = (byte) 0x08;
		} else if (all_powers_list_map_lenth > 85) {
			powor_lenth[3] = (byte) 0x09;
		}
		System.out.println("powor_lenth：：" + ByteUtil.byte2Hex(powor_lenth));
		// 权限8字节
		int forth = 0;
		for (int i = 9; i <= 12; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
				if (LiftRule.equals("0")) {
					log.info("整楼权限，直接赋值bit7置1");
					//电梯组
					byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					forth_powor_new[0] = (byte) (forth_powor_new[0] | (1 << 7));
					forth_powor[forth * 2] = forth_powor_new[0];
					//楼层
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					forth_powor[forth * 2 + 1] = (byte) 0x80;//所有楼层均有权限
				} else {
					if (CardSonType==0) {//标识自动登记楼层
						//电梯组
						log.info("deviceid值===" + deviceid);
						byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						log.info("进入s==1:forth_powor_new前===" + forth_powor_new[0]);
						forth_powor_new[0] = (byte) (forth_powor_new[0] | (1 << 7));
						log.info("进入s==1:forth_powor_new后===" + forth_powor_new[0]);
						forth_powor[forth * 2] = forth_powor_new[0];
						//楼层
						log.info("LiftRule权限值===" + LiftRule);
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						forth_powor[forth * 2 + 1] = LiftRule_new[0];
					} else {//标识手动登记楼层
						//电梯组
						byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						forth_powor_new[0] = (byte) (forth_powor_new[0] | (0 << 7));
						forth_powor[forth * 2] = forth_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						forth_powor[forth * 2 + 1] = LiftRule_new[0];
					}
				}

			} else {
				log.info("开始执行自动补0");
				forth_powor[forth * 2] = (byte) 0x00;
				forth_powor[forth * 2 + 1] = (byte) 0x00;
			}
			forth = forth + 1;
			log.info("本次循环结束");
		}
		System.out.println("forth_powor：：" + ByteUtil.byte2Hex(forth_powor));
		for (byte b : forth_powor) {
			System.out.println("B:" + b);
		}
		// 剩余权限
		log.info("剩余权限开始-------");
		int b_len = all_powers_list_map_lenth - 13;
		log.info("b_len:"+b_len);
		if(b_len<=0){
			b_len=0;
		}
		byte[] surplus_powor = new byte[16];// 剩余权限,字节不固定
		int surplus = 0;
		for (int i = 13; i <= all_powers_list_map_lenth-1; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
				log.info("deviceid:"+deviceid);
				log.info("LiftRule:"+LiftRule);
				if (LiftRule.equals("0")) {
					log.info("整楼权限，直接赋值bit7置1");
					//电梯组
					byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (1 << 7));
					surplus_powor[surplus * 2] = surplus_powor_new[0];
					//楼层
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					surplus_powor[surplus * 2 + 1] = (byte) 0x80;//所有楼层均有权限
				} else {
					if (CardSonType==0) {//标识自动登记楼层
						//电梯组
						log.info("deviceid值===" + deviceid);
						byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						log.info("进入s==1:surplus_powor_new前===" + surplus_powor_new[0]);
						surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (1 << 7));
						log.info("进入s==1:surplus_powor_new后===" + surplus_powor_new[0]);
						surplus_powor[surplus * 2] = surplus_powor_new[0];
						//楼层
						log.info("LiftRule权限值===" + LiftRule);
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
					} else {//手动登记楼层
						//电梯组
						byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (0 << 7));
						surplus_powor[surplus * 2] = surplus_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
						surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
					}
				}

			} else {
				log.info("开始执行自动补0");
				surplus_powor[surplus * 2] = (byte) 0x00;
				surplus_powor[surplus * 2 + 1] = (byte) 0x00;
			}
			surplus = surplus + 1;
			log.info("本次循环结束");
		}
		System.out.println("surplus_powor：：" + ByteUtil.byte2Hex(surplus_powor));
		for (byte b : surplus_powor) {
			System.out.println("B:" + b);
		}
//		int length1 = surplus_powor.length;
//		if(length1<16){
//			surplus_powor = new byte[16];
//		}
		System.out.println("surplus_powor变为16字节后内容=====" + Arrays.toString(surplus_powor) );
//		byte[] texts = ByteUtil.concatBytes(village_code, flag, card_number, flag_2, first_powor, second_powor,
//				card_type, third_powor, powor_lenth, flag_3, forth_powor, surplus_powor);
//		System.out.println("texts：：" + ByteUtil.byte2Hex(texts));
		byte[] textm = ByteUtil.concatBytes(village_code,flag,card_number,flag_2,
				first_powor,second_powor,card_type,third_powor,powor_lenth,flag_3,forth_powor);
		System.out.println("textm：：" + ByteUtil.byte2Hex(textm));
		byte[] makeUp=new byte[240-textm.length];
		System.out.println("补0-16进制"+ByteUtil.byte2Hex(makeUp));
		System.out.println("补0-数组"+Arrays.toString(makeUp));
		byte[] textss=ByteUtil.concatBytes(textm,makeUp);
		byte[] startTimestamps = ByteUtil.long2H4bytes(new Date().getTime());//当前时间戳4个字节

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");;
		Date datEnd = format.parse(CardValidPeriod);
		int endTimestamp = (int) (datEnd.getTime() / 1000);
		byte[]endTimestamps=ByteUtil.long2H4bytes(endTimestamp);//权限截至时间前
		byte[] lengths=ByteUtil.unlong2H2bytes(252);
		//byte[] dev=ByteUtil.concatBytees(textm,makeUp);
		byte[] card_number2 = new byte[4];
		card_number2 =ByteUtil.unlong2H4bytes(Integer.valueOf(VisitorApplyID));
		byte[] QRContent = ByteUtil.concatBytes(card_number2, textss, startTimestamps, endTimestamps);
		byte[] QRContentEncryption = HMACUtil.encrypt(QRContent, "123456789");
		byte[] head = ByteUtil.Str16toBytes(ByteUtil.convertStringToHex("vgrltk"));
		byte[] QR = ByteUtil.concatBytes(head, lengths, QRContent, QRContentEncryption);
		map.put("VisitorRules",VisitorRules);
		map.put("QR",QR);
		return map;
	}


	/**
	 *生成访客通行码
	 * @return
	 */
	@RequestMapping(value = "Passcode", method = RequestMethod.POST)
	@ResponseBody
	public Object Passcode(
			@RequestParam String sessionid,
			@RequestParam Integer VisitorApplyID,//访客记录ID获取访客权限
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("获取访客权限VisitorApplyID" + VisitorApplyID);
		Map<String, Object> map = new HashMap<>();
		List<HashMap<String, Object>> VisitorRules = VisitorService.getvisitorrule(VisitorApplyID);
		String[] CardValidPeriodssss = RuleService.queryVisitorEndTimeByVisitorApplyID(VisitorApplyID);
		System.out.println(Arrays.toString(CardValidPeriodssss));
		String CardValidPeriod =  CardValidPeriodssss[0];
		String[] visitorBeginTimesss = RuleService.queryVisitorBeginTimeByVisitorApplyID(VisitorApplyID);
		System.out.println(Arrays.toString(visitorBeginTimesss));
		String visitorBeginTime = visitorBeginTimesss[0];
		log.info("查询出权限截至时间为"+CardValidPeriod);
		log.info("查询出权限开始时间为"+CardValidPeriod);
		List<HashMap<String, Object>> all_powers_list_map = new ArrayList<>();
		// 获取所有权限的总条数----以层为单位 1号楼1层 1号楼2层
		int totle = 0;
		for (int i = 0; i < VisitorRules.size(); i++) {
			String deviceid = VisitorRules.get(i).get("DeviceID").toString();
			String liftRules = VisitorRules.get(i).get("LiftRule").toString();
			//String personID = VisitorRules.get(i).get("VisitorApplyID").toString();
			//String startTime = VisitorRules.get(i).get("StartTime").toString();
			//String endTime = VisitorRules.get(i).get("EndTime").toString();
			List<String> LiftRule_list = Arrays.asList(liftRules.split(","));
			for (int j = 0; j < LiftRule_list.size(); j++) {
				String LiftRule = LiftRule_list.get(j);
				Integer power_len = Integer.valueOf(LiftRule);
				//log.info("计算偏值");// 因协议中的楼层00为一层
				// 获取设备偏执
				Integer FloorDifference = deviceService.queryFloorDifferenceByDeviceID(Integer.valueOf(deviceid));
				if (null == FloorDifference) {
					log.info("偏执为空,赋值为0");
					FloorDifference = 0;
				}
				// 计算楼层权限
				if (power_len == 0) {
					log.info("0代表所有权限,所有无需计算");
				} else {
					if (power_len < 0) {
						power_len = power_len + FloorDifference + 1 - 1;// +1是负数偏值 -1是协议00位为1层
						log.info("power_len----" + power_len);
					} else {
						power_len = power_len + FloorDifference - 1;// -1是协议00位为1层
					}
				}

				HashMap<String, Object> power = new HashMap<>();
				power.put("LiftRule", power_len);
				power.put("deviceid", deviceid);
				//power.put("startTime", startTime);
				//power.put("endTime", endTime);

				all_powers_list_map.add(i, power);
				totle = totle + 1;
			}
		}

		System.out.println("-----------------------------------------------------" + all_powers_list_map.toString());
		log.info("处理之后的数据为:" + all_powers_list_map);
		// 定义数据

		byte[] head = ByteUtil.Str16toBytes("55aa");

		// 一个扇区3块 一块16字节
		// 第一扇区第一块啊
		byte[] village_code = new byte[3];// 小区码
		byte[] flag = ByteUtil.Str16toBytes("00"); // 1字节标识
		byte[] card_number = new byte[2]; // 2字节卡号
		byte[] flag_2 = new byte[2]; // 2字节标识//

		byte[] first_powor = new byte[8];// 8字节的扇区第一块权限
		// 第一扇区第二块啊
		byte[] second_powor = new byte[8];// 8字节的扇区第二块权限
		byte[] card_type = new byte[8];// 8字节的卡号类型
		// 第一扇区第三块啊
		byte[] third_powor = new byte[2];// 2字节的扇区第三块权限
		byte[] powor_lenth = new byte[4];// 4字节的总权限大致长度
		// 0X02当楼层权限超过 13 组时，写 0X06，否则写 0X00
		// 0X03当楼层权限超过 37 组时，写 0X07，否则写 0X00
		// 0X04当楼层权限超过 61 组时，写 0X08，否则写 0X00
		// 0X05当楼层权限超过 85 组时，写 0X09，否则写 0X00
		//Bit0—bit4:有效日，（1--31）,bit5-bit7，默认001
		//0X01  本条以及上面一条对应flag_3
		byte[] flag_3 = new byte[2]; // 2字节标识
		String substring = CardValidPeriod.substring(8, 10);
		//StringBuilder reverse = new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse();
		String dd = new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse().toString();
		byte[] bytes9 = ByteUtils.long2Hbytes1byte(ByteUtils.binary2To10(dd));
		bytes9[0] = (byte) (bytes9[0] | (1 << 0));
//			flag_3 = ByteUtil.unlong2H2bytes(Integer.valueOf(CardValidPeriod.substring(8, 10)));
		flag_3[0] = bytes9[0];
		flag_3[1] = (byte) 0X01;
		byte[] forth_powor = new byte[8];// 8字节的扇区第二块权限
		// 以下扇区的长度根据权限长度定,无需列举
		// ---------------------第一扇区第一块----------------
		village_code = ByteUtil.Str16toBytes("010101");//小区码暂时写死
		log.info("小区码3位=-==" + village_code[0] + "---" + village_code[1] + "---" + village_code[2]);
		byte[] bytes4 = ByteUtils.unlong2H2bytes(VisitorApplyID);
		card_number[0] = bytes4[bytes4.length - 1];
		card_number[1] = bytes4[0];
		//log.info("卡号字段=-=="+card_number[0]+"---"+card_number[1]+"---"+card_number[2]+"---"+card_number[3]);
		byte[] bytes1 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(2, 4)));
		flag_2[0] = bytes1[0];
		//log.info("有效年份字段=-=="+flag_3[0]);
		byte[] bytes2 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(5, 7)));
		bytes2[0] = (byte) (bytes2[0] | (1 << 4));
		bytes2[0] = (byte) (bytes2[0] & ~(1 << 5));
		bytes2[0] = (byte) (bytes2[0] & ~(1 << 6));
		bytes2[0] = (byte) (bytes2[0] | (1 << 7));
		flag_2[1] = bytes2[0];
		//log.info("有效月份字段=-=="+flag_3[1]);
		int all_powers_list_map_lenth = all_powers_list_map.size();
		log.info("------------------" + all_powers_list_map_lenth);
		int first = 0;
		for (int i = 0; i <= 3; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
						//电梯组
						byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						first_powor_new[0] = (byte) (first_powor_new[0] & ~(1 << 7));
						first_powor[first * 2] = first_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~(1 << 6));//主门有效
						first_powor[first * 2 + 1] = LiftRule_new[0];
			} else {
				log.info("开始执行自动补0");
				first_powor[first * 2] = (byte) 0x00;
				first_powor[first * 2 + 1] = (byte) 0x00;
			}
			first = first + 1;
		}
		System.out.println("first_powor：：" + ByteUtil.byte2Hex(first_powor));
		for (byte b : first_powor) {
			System.out.println("B:" + b);
		}
		// ---------------------第一扇区第二块----------------
		// 权限8字节
		int second = 0;
		for (int i = 4; i <= 7; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
						//电梯组
						byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						second_powor_new[0] = (byte) (second_powor_new[0] & ~(1 << 7));
						second_powor[second * 2] = second_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~(1 << 6));//主门有效
						second_powor[second * 2 + 1] = LiftRule_new[0];
			} else {
				log.info("开始执行自动补0");
				second_powor[second * 2] = (byte) 0x00;
				second_powor[second * 2 + 1] = (byte) 0x00;
			}
			second = second + 1;
			log.info("本次循环结束");
		}
		System.out.println("second_powor：：" + ByteUtil.byte2Hex(second_powor));
		for (byte b : second_powor) {
			System.out.println("B:" + b);
		}
		//卡类型8字节

			byte[] b_CardType = ByteUtil.int2Hbytes1byte(1);
			card_type[0] = b_CardType[0];
			card_type[1] = (byte) 0X00;
			card_type[2] = (byte) 0X17;
			card_type[3] = (byte) 0X3B;
			card_type[4] = (byte) 0X00;
			card_type[5] = (byte) 0X00;
			card_type[6] = (byte) 0X00;
			card_type[7] = (byte) 0X00;


		System.out.println("card_type：：" + ByteUtil.byte2Hex(card_type));
		// ---------------------第一扇区第三块----------------
		if (all_powers_list_map_lenth > 8) {
			log.info("第九组权限");
			String deviceid = all_powers_list_map.get(8).get("deviceid").toString();
			String LiftRule = all_powers_list_map.get(8).get("LiftRule").toString();
					//电梯组
					byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
					third_powor_new[0] = (byte) (third_powor_new[0] & ~(1 << 7));
					third_powor[0] = third_powor_new[0];
					//楼层
					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
					LiftRule_new[0] = (byte) (LiftRule_new[0] & ~(1 << 6));//主门有效
					third_powor[1] = LiftRule_new[0];
				}else{
			log.info("开始执行自动补0");
			third_powor[0] = (byte) 0x00;
			third_powor[1] = (byte) 0x00;
		}
		System.out.println("third_powor：：" + ByteUtil.byte2Hex(third_powor));
		// 获取总楼层权限
		log.info("超出13组后的一次判断赋值模块");
		if (all_powers_list_map_lenth > 13) {
			powor_lenth[0] = (byte) 0x06;
		} else if (all_powers_list_map_lenth > 37) {
			powor_lenth[1] = (byte) 0x07;
		} else if (all_powers_list_map_lenth > 61) {
			powor_lenth[2] = (byte) 0x08;
		} else if (all_powers_list_map_lenth > 85) {
			powor_lenth[3] = (byte) 0x09;
		}
		System.out.println("powor_lenth：：" + ByteUtil.byte2Hex(powor_lenth));
		// 权限8字节
		int forth = 0;
		for (int i = 9; i <= 12; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
						//电梯组
						byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						forth_powor_new[0] = (byte) (forth_powor_new[0] & ~(1 << 7));
						forth_powor[forth * 2] = forth_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~(1 << 6));//主门有效
						forth_powor[forth * 2 + 1] = LiftRule_new[0];
					} else {
				log.info("开始执行自动补0");
				forth_powor[forth * 2] = (byte) 0x00;
				forth_powor[forth * 2 + 1] = (byte) 0x00;
			}
			forth = forth + 1;
			log.info("本次循环结束");
		}
		System.out.println("forth_powor：：" + ByteUtil.byte2Hex(forth_powor));
		for (byte b : forth_powor) {
			System.out.println("B:" + b);
		}
		// 剩余权限
		log.info("剩余权限开始-------");
		int b_len = all_powers_list_map_lenth - 13;
		log.info("b_len:" + b_len);
		if (b_len <= 0) {
			b_len = 0;
		}
		byte[] surplus_powor = new byte[b_len * 2];// 剩余权限,字节不固定
		int surplus = 0;
		for (int i = 13; i <= all_powers_list_map_lenth - 1; i++) {
			if (all_powers_list_map_lenth > i) {// 当前权限
				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
				log.info("deviceid:" + deviceid);
				log.info("LiftRule:" + LiftRule);
						//电梯组
						byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						surplus_powor_new[0] = (byte) (surplus_powor_new[0] & ~(1 << 7));
						surplus_powor[surplus * 2] = surplus_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~(1 << 6));//主门有效
						surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
					} else {
				log.info("开始执行自动补0");
				surplus_powor[surplus * 2] = (byte) 0x00;
				surplus_powor[surplus * 2 + 1] = (byte) 0x00;
			}
			surplus = surplus + 1;
			log.info("本次循环结束");
		}
		System.out.println("surplus_powor：：" + ByteUtil.byte2Hex(surplus_powor));
		for (byte b : surplus_powor) {
			System.out.println("B:" + b);
		}
//		int length1 = surplus_powor.length;
//		if(length1<16){
//			surplus_powor = new byte[16];
//		}
		System.out.println("surplus_powor变为16字节后内容=====" + Arrays.toString(surplus_powor));
//		byte[] texts = ByteUtil.concatBytes(village_code, flag, card_number, flag_2, first_powor, second_powor,
//				card_type, third_powor, powor_lenth, flag_3, forth_powor, surplus_powor);
//		System.out.println("texts：：" + ByteUtil.byte2Hex(texts));
		byte[] textm = ByteUtil.concatBytes(village_code, flag, card_number, flag_2,
				first_powor, second_powor, card_type, third_powor, powor_lenth, flag_3, forth_powor, surplus_powor);
		System.out.println("textm：：" + ByteUtil.byte2Hex(textm));
		byte[] makeUp = new byte[240 - textm.length];
		byte[] textss = ByteUtil.concatBytes(textm, makeUp);
		log.info("最终下发的数据为"+ByteUtils.byte2Hex(textss));

		log.info("查询出权限开始时间为"+CardValidPeriod);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date begin = format.parse(visitorBeginTime);
		int VisitorBeginTime = (int) (begin.getTime() / 1000);
		Date end = format.parse(CardValidPeriod );
		int CardValidPeriods = (int) (end.getTime() / 1000);
		byte[] startTimestamps = ByteUtil.long2H4bytes(VisitorBeginTime);//当前时间戳4个字节
		byte[]endTimestamps=ByteUtil.long2H4bytes(CardValidPeriods);//权限截至时间前
		byte[] card_number4 = new byte[4];
		card_number4 =ByteUtil.unlong2H4bytes(Integer.valueOf(VisitorApplyID));
		byte[] lengths=ByteUtil.unlong2H2bytes(252);
		byte[] QRContent = ByteUtil.concatBytes(card_number4, textss, startTimestamps, endTimestamps);
		byte[] QRContentEncryption = HMACUtil.encrypt(QRContent, "ffffffffffff");
		byte[] heads = ByteUtil.Str16toBytes(ByteUtil.convertStringToHex("vgrltk"));
		byte[] QR = ByteUtil.concatBytes(heads, lengths, QRContent, QRContentEncryption);
		log.info("生成二维码数据的16进制为"+ByteUtils.byte2Hex(QR));
		log.info("生成二维码数据的16进制转字符串"+ByteUtils.convertHexToString(ByteUtils.byte2Hex(QR)));
		map.put("VisitorRules",VisitorRules);
		map.put("QR",QR);
		return map;
	}
}
