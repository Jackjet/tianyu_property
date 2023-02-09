package com.vguang.controller.org;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.vguang.service.ICardService;
import com.vguang.service.ILoginService;
import com.vguang.service.ISyncService;
import com.vguang.utils.CaptchaManager;
import com.vguang.utils.ConstantSet;
import com.vguang.utils.GsonUtil;
import com.vguang.utils.JedisManager;
import com.vguang.utils.StringUtil;
import com.vguang.utils.SysConfig;

/**
 * @author wangsir
 *
 *         2017年9月30日
 */
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@Controller
@RequestMapping("/orgman")
public class OrgLoginController {
	private static final Logger log = LoggerFactory.getLogger(OrgLoginController.class);

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

	/**
	 * 获取验证码
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@CrossOrigin
	@RequestMapping(value = "getCaptcha", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Object genCaptcha(HttpServletRequest request, HttpServletResponse response) {
		log.info(StringUtil.logStr("web--请求生成验证码"));
		String msg = "";
		String captchaId = StringUtil.randStrs(6);
		log.info(StringUtil.logStr("captchaId为:{}"), captchaId);
		String capUrl = "";
		try {
			capUrl = captchaManager.generateCaptchaImage(captchaId);
		} catch (IOException e) {
			msg = "生成验证码出现异常";
			log.info(StringUtil.logStr(msg));
			e.printStackTrace();
		}
		msg = "请求验证码成功";
		Map<String, Object> result = getRepMap(ConstantSet.RSP_SUCCESS_CODE, msg);
		result.put("captchaId", captchaId);
		result.put("capUrl", capUrl);
		log.info(StringUtil.logStr(gson.toJson(result)));
		return result;
	}

	/**
	 * code ConstantSet错误 0正确
	 * 
	 * @param rstCode
	 * @param errMsg
	 * @return
	 */
	private Map<String, Object> getRepMap(int rstCode, String errMsg) {
		Map<String, Object> result = new HashMap<>();
		result.put("result", rstCode);
		result.put("msg", errMsg);
		return result;
	}

	/**
	 * 账号登陆
	 * @param
	 * @param
	 * @param CaptchaId
	 * @param CaptchaValue
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(
			@RequestParam String AccountName, 
			@RequestParam String AccountPassword,
			@RequestParam String CaptchaId,
			@RequestParam String CaptchaValue,
			@RequestParam(required = false) String CardSecretKey,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("公司系统账号/密码登录:{},{}", AccountName, AccountPassword);
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		Integer result = -1;
		String msg = "";
		// 验证账号密码
		Integer AccountID = loginService.queryAccountIDByAccount(AccountName, AccountPassword);
		log.info("AccountID=========" + AccountID);
		if (AccountID == null) {
			result = -2;
			msg = "账号密码错误";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		// 验证状态
		Integer AccountState = loginService.queryAccountStateByID(AccountID);
		if (AccountState != 0) {
			result = -1;
			msg = "当前账号以禁用,请联系管理员";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		// 首先判断当前系统是否存在卡秘钥
		if (CardSecretKey == null) {
			log.info("判断是否已存在");
			String SysConfigKey = "CardSecretKey";
			String SysConfigValue = syncService.queryValueByKey(SysConfigKey);
			if (SysConfigValue == null) {
				result = -2;
				msg = "首次登陆请填写秘钥";
				map.put("result", result);
				map.put("msg", msg);
				return map;
			}
		} else {
			log.info("新增卡秘钥");
			String SysConfigName = "卡秘钥";
			Integer SysConfigType = 2;
			String SysConfigKey = "CardSecretKey";
			String SysConfigValue = CardSecretKey;
			String SysConfigDesc = "卡秘钥";
			Integer OrgSwitch = 1;
			String value = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue,
					SysConfigDesc,null, OrgSwitch);
			log.info("修改首次登陆状态");
			Integer FirstState = 1;
			Integer row = loginService.updateFirstState(FirstState, AccountID);
		}

		if (CaptchaValue.equals("qwer")) {
			log.info("自动测试用，正式版需要注销");
		} else {
			boolean validateRst = captchaManager.validateValue(CaptchaId, CaptchaValue);
			// 验证码不对
			if (!validateRst) {
				msg = "验证码不正确";
				log.info(StringUtil.logStr(msg));
				return getRepMap(ConstantSet.RSP_ERR_CAPTCHA_CHECK, msg);
			}
		}
		String sessionid = StringUtil.randStrs(36);
		log.info("sessionid===="+sessionid);
		Integer PersonID= loginService.queryPersonIDByAccount(AccountName, AccountPassword);
		//Integer CardId = loginService.queryCardIDByAccount(AccountName, AccountPassword);
		jedisManager.setValueByStr(1, sessionid, PersonID.toString(), 7200);
		result = 0;
		map.put("sessionid", sessionid);
		map.put("PersonID", PersonID);
		map.put("AccountID", AccountID);
		map.put("result", result);
		return map;
	}
	
	/**
	 * 给人设置登陆账号密码
	 * @param sessionid
	 * @param CardID
	 * @param AccountName
	 * @param AccountPassword
	 * @param Power
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "setupaccount", method = RequestMethod.POST)
	@ResponseBody
	public Object setupaccount(
			@RequestParam String sessionid,
			@RequestParam String PersonID,
			@RequestParam String AccountName,
			@RequestParam String AccountPassword,
			@RequestParam String Power,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("给人设置登陆账号密码PersonID===" + PersonID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		// 新增账号表
		Integer FirstState = 1;
		Integer AccountState = 0;
		//判断当前账号是否重复
		Integer Totle= loginService.queryCountAccountByAccountName(AccountName);
		if(Totle >0) {
			result = -2;
			msg = "账号重复";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		Integer row = loginService.insertAccount(PersonID, AccountName, AccountPassword, FirstState, AccountState);
		// 修改人员卡号权限
		//row = CardService.updatePowerByCardID(PersonID, Power);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 修改人员的登陆账号密码
	 * @param sessionid
	 * @param AccountID
	 * @param AccountName
	 * @param AccountPassword
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "updateaccount", method = RequestMethod.POST)
	@ResponseBody
	public Object setupaccount(
			@RequestParam String sessionid,
			@RequestParam String AccountID,
			@RequestParam String AccountName,
			@RequestParam String AccountPassword,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("修改人员的登陆账号密码AccountID===" + AccountID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		// 修改账号表
		Integer FirstState = 1;
		Integer AccountState = 0;
		// 判断当前账号是否重复
		Integer Totle = loginService.queryCountAccountByAccountName(AccountName);
		if (Totle > 0) {
			result = -2;
			msg = "账号重复";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		Integer row = loginService.updateAccountPasswordByID(AccountID, AccountName, AccountPassword);
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	

}
