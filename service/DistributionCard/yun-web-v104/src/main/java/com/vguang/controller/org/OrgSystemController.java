package com.vguang.controller.org;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import com.vguang.service.ISyncService;
import com.vguang.utils.JedisManager;
import com.vguang.utils.ScoketServer;

/**
 * @author wangsir
 *
 *         2017年9月28日
 */
@Controller
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@RequestMapping("/orgman/system")
public class OrgSystemController {
	private Logger log = LoggerFactory.getLogger(OrgSystemController.class);
	@Autowired
	private ISyncService sysService;
	@Autowired
	private JedisManager jedisManager;
	/**
	 * 获取系统配置
	 * 
	 * @param sessionid
	 * @param SysConfigKey
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getsysconfigs", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object getsysconfigs(@RequestParam String sessionid,
			@RequestParam String SysConfigKeys,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("获取多项系统配置SysConfigKeys" + SysConfigKeys);
		Map<String, Object> map = new HashMap<>();
		List<String> sys_list = Arrays.asList(SysConfigKeys.split(","));
		ArrayList list = new ArrayList<>();
		int w = 0;
		for (int i = 0; i < sys_list.size(); i++) {
			String SysConfigKey = sys_list.get(i);
			HashMap<String, Object> SysConfig = sysService.querySysConfigByKey(SysConfigKey);
			if(SysConfig !=null) {
				list.add(w, SysConfig);
				w =w++;
			}
			
		}

		map.put("SysConfig", list);
		return map;
	}
	
	/**
	 * 获取单项系统配置
	 * @param sessionid
	 * @param SysConfigKey
	 * @param SysConfigType
	 * @param SendOutState
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getsysconfig", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object getsysconfig(@RequestParam String sessionid,
			@RequestParam(required = false) String SysConfigKey,
			@RequestParam(required = false) Integer SysConfigType,
			@RequestParam(required = false) String SendOutState,
			@RequestParam(required = false) Integer SysGroup,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("获取单项系统配置SysConfigKey" + SysConfigKey);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("SysConfigKey", SysConfigKey);
		params.put("SysConfigType", SysConfigType);
		params.put("SendOutState", SendOutState);
		params.put("SysGroup", SysGroup);
		log.info("params====================="+params);
		Integer Totle= sysService.queryCountSysConfigsByparams(params);
		List<HashMap<String, Object>> SysConfigs = null;
		if(Totle >0 ) {
			SysConfigs= sysService.querySysConfigsByparams(params);
		}
		map.put("Totle", Totle);
		map.put("SysConfig", SysConfigs);
		return map;
	}
	
	/**
	 * 修改单项配置
	 * @param sessionid
	 * @param SysConfigKey
	 * @param SysConfigValue
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updatesysconfig", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object updatesysconfig(@RequestParam String sessionid,
			@RequestParam String SysConfigKey,
			@RequestParam String SysConfigValue,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("修改单项配置SysConfigKey" + SysConfigKey);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer row = sysService.updateSyscongigByKey(SysConfigKey, SysConfigValue);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 同步系统配置(指针对扇区秘钥)
	 * @param sessionid
	 * @param SecretKey
	 * @param SecretValue
	 * @param SectorKey
	 * @param SectorValue
	 * @param Ord_Secret
	 * @param Flag
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "synchroconfig", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object synchroconfig(@RequestParam String sessionid,
			@RequestParam String SecretKey,	//秘钥
			@RequestParam String SecretValue,
			@RequestParam String SectorKey,//扇区
			@RequestParam String SectorValue,
			@RequestParam String Ord_Secret,
			@RequestParam Integer Flag,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		int Sector = Integer.valueOf(SectorValue);// 扇区 一字节
		String Sector_type = "60"; // 秘钥类型 0x60:A秘钥，0x61:B秘钥 一字节
		// 获取原始卡秘钥
		if (Flag == 1) {
			log.info("初始页操作,获取原始秘钥");
			Ord_Secret = sysService.queryValueByKey("CardSecretKey");
		}
		// String Ord_Secret ="FFFFFFFFFFFF"; //原秘钥 第一次设置 为FFFFFFFFFFFF 六字节
		String new_Secret = SecretValue; // 秘钥数据
		Integer setUpSectorAndSecret_result = ScoketServer.setUpSectorAndSecret(Sector, Sector_type, Ord_Secret,
				new_Secret);
		log.info("开始接收修改扇区的返回值");
		for (int j = 0; j < 11; j++) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = new GregorianCalendar();
			Date date = new Date();
			log.info("系统当前时间      ：" + df.format(date));
			Thread.sleep(1000);
			log.info("j============" + j);
			String Key = "setUpSectorAndSecret_result";
			String Json = jedisManager.getValueByStr(0, Key);
			log.info("Json=========" + Json);
			if (Json != null) {
				log.info("获取到了返回值,判断是否为正确的返回值");
				if (Json.equals("010000")) {
					log.info("修改成功,将这一对配置变为已同步成功");
					Integer SendOutState = 1;
					// 修改扇区
					Integer row = sysService.updateSendOutStateByKey(SendOutState, SectorKey);// 修改扇区的
					row = sysService.updateSyscongigByKey(SectorKey, SectorValue);
					// 修改秘钥
					row = sysService.updateSendOutStateByKey(SendOutState, SecretKey); // 修改扇区秘钥的
					row = sysService.updateSyscongigByKey(SecretKey, SecretValue);
				} else if (Json.equals("0100ff")) {
					result = -2;
					msg = "修改失败";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}
			} else {
				if (j == 10) {
					log.info("修改扇区超时了");
					result = -2;
					msg = "设备不在线";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}
			}
		}

		// Integer row = sysService.updateSyscongigByKey(SysConfigKey, SysConfigValue);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 初始化系统配置
	 * @param sessionid
	 * @param SysConfigName
	 * @param SysConfigKey
	 * @param SysConfigValue
	 * @param SysConfigType
	 * @param SysConfigDesc
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(value = "initializesconfigs", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object initializesconfigs(@RequestParam String sessionid,
			@RequestParam String SysConfigNames,
			@RequestParam String SysConfigKeys,
			@RequestParam String SysConfigValues,
			@RequestParam String SysConfigTypes,
			@RequestParam String SysGroups,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		log.info("初始化系统配置");
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		int handshake = -1;//握手返回值
		String msg = "";
		log.info("发送握手指令,确保设备在线");
		ScoketServer ScoketServer = new ScoketServer();
		int handshake_result = ScoketServer.handshake();
		if(handshake_result ==-3) {
			result = 0;
			msg = "发卡设备未连接";
			map.put("result", result);
			map.put("msg", msg);
		}
		log.info("发送成功,开始接受返回值,最大时间为10秒");
		for (int i = 0; i < 11; i++) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = new GregorianCalendar();
			Date date = new Date();
			log.info("系统当前时间      ：" + df.format(date));
			Thread.sleep(1000);
			log.info("i============" + i);
			String Key = "handshake";
			String Json = jedisManager.getValueByStr(0, Key);
			log.info("Json=========" + Json);
			if (Json != null) {
				log.info("获取到了返回值,判断是否为正确的返回值");
				if (Json.equals("010000")) {
					log.info("------------------------------握手成功-----------------------------------");
					handshake = 0;
					msg = "返回成功";
					i =10;
				}
			} else {
				if (i == 10) {
					log.info("------------------------------设备不在线----------------------------------");
					result = -2;
					msg = "设备不在线";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}
			}

		}
		if(handshake == 0) {
			log.info("------------握手成功----------");
			List<String> SysConfigNames_list = Arrays.asList(SysConfigNames.split(","));
			List<String> SysConfigKeys_list = Arrays.asList(SysConfigKeys.split(","));
			List<String> SysConfigValues_list = Arrays.asList(SysConfigValues.split(","));
			List<String> SysConfigTypes_list = Arrays.asList(SysConfigTypes.split(","));
			List<String> SysGroups_list = Arrays.asList(SysGroups.split(","));
			// 验证是否有重复错误数据
			for (int i = 0; i < SysConfigNames_list.size(); i++) {
				String SysConfigKey = SysConfigKeys_list.get(i);
				Integer totle = sysService.queryCountSysConfigByKey(SysConfigKey);
				if (totle > 0) {
					result = -2;
					msg = "当前配置重复,请勿重复配置,重复的数据为:<" + SysConfigNames_list.get(i) + ">";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}

			}
			log.info("无重复数据,开始增加");
			for (int i = 0; i < SysConfigNames_list.size(); i++) {
				String SysConfigNam = SysConfigNames_list.get(i);
				String SysConfigKey = SysConfigKeys_list.get(i);
				String SysConfigValue = SysConfigValues_list.get(i);
				String SysConfigType = SysConfigTypes_list.get(i);
				String SysGroup = SysGroups_list.get(i);
				Integer SendOutState= 0;
				log.info("SysConfigNam====" + SysConfigNam);
				log.info("SysConfigKey====" + SysConfigKey);
				log.info("SysConfigValue====" + SysConfigValue);
				log.info("SysConfigType====" + SysConfigType);
				log.info("SysGroup====" + SysGroup);
				String SysConfigDesc = SysConfigNam;
				// 配置系统
				Integer row = sysService.addSysConfigByKey(SysConfigNam, Integer.valueOf(SysConfigType), SysConfigKey, SysConfigValue,
						SysConfigDesc,SendOutState,SysGroup);

			}
			log.info("开始给设备下发扇区以及秘钥数据");
			//获取所有的扇区
			Integer type = 0;//0为扇区
			 List<HashMap<String, Object>> SysConfigs= sysService.querySysConfigsbByType(type);
			for (int i = 0; i < SysConfigs.size(); i++) {
				HashMap<String, Object> SysConfig = SysConfigs.get(i);
				String SysConfigKey = (String) SysConfig.get("SysConfigKey");
				String SysConfigValue = (String) SysConfig.get("SysConfigValue");
				log.info("SysConfigKey====" + SysConfigKey);
				log.info("SysConfigValue====" + SysConfigValue);
				// 获取相应的秘钥
				String Secret_KEY = SysConfigKey + "_Secret";
				log.info("Secret_KEY====" + Secret_KEY);
				String Secret = sysService.queryValueByKey(Secret_KEY);
				log.info("Secret====" + Secret);
				int Sector =Integer.valueOf(SysConfigValue);//扇区  一字节
				String Sector_type = "60";		//秘钥类型 0x60:A秘钥，0x61:B秘钥   一字节
				//获取原始卡秘钥
				String Ord_Secret= sysService.queryValueByKey("CardSecretKey");
				//String Ord_Secret ="FFFFFFFFFFFF";	//原秘钥  第一次设置 为FFFFFFFFFFFF  六字节
				String new_Secret = SysConfigValue;	//秘钥数据
				Integer setUpSectorAndSecret_result = ScoketServer.setUpSectorAndSecret(Sector,Sector_type,Ord_Secret,new_Secret);
				log.info("开始接收修改扇区的返回值");
				for (int j = 0; j < 11; j++) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar c = new GregorianCalendar();
					Date date = new Date();
					log.info("系统当前时间      ：" + df.format(date));
					Thread.sleep(1000);
					log.info("j============" + j);
					String Key = "setUpSectorAndSecret_result";
					String Json = jedisManager.getValueByStr(0, Key);
					jedisManager.delValueByStr(0, Key);
					log.info("Json=========" + Json);
					if (Json != null) {
						log.info("获取到了返回值,判断是否为正确的返回值");
						if (Json.equals("010000")) {
							log.info("修改成功,将这一对配置变为已同步成功");
							Integer SendOutState = 1;
							Integer row= sysService.updateSendOutStateByKey(SendOutState,SysConfigKey);//修改扇区的
									row= sysService.updateSendOutStateByKey(SendOutState,Secret_KEY);	//修改扇区秘钥的
							
							j=10;
						}else if(Json.equals("0100ff")) {
							result = -2;
							msg = "修改失败";
							map.put("result", result);
							map.put("msg", msg);
							return map;						}
					} else {
						if (j == 10) {
							log.info("修改扇区超时了");
							result = -2;
							msg = "设备不在线";
							map.put("result", result);
							map.put("msg", msg);
							return map;
						}
					}
				}
			}
			
			
		}

		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

}
