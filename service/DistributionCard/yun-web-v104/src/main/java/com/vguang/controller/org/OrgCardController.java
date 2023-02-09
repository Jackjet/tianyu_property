package com.vguang.controller.org;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vguang.service.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vguang.eneity.BlackList;
import com.vguang.eneity.Card;
import com.vguang.service.impl.RuleService;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;
import com.vguang.utils.ByteUtil;
import com.vguang.utils.ExcelUtil;
import com.vguang.utils.JedisManager;
import com.vguang.utils.ScoketServer;
import com.vguang.utils.StringUtil;
import com.vguang.utils.SysConfig;
import com.vguang.utils.TimeUtil;
import com.vguang.utils.encrypt.AESUtil;
import com.vguang.utils.encrypt.B64Util;
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@Controller
@RequestMapping("/orgman/card")
public class OrgCardController {
	private Logger log = LoggerFactory.getLogger(OrgCardController.class);
	@Autowired
	private ICardService CardService;
	@Autowired
	private IRecordsService RecordsService ;
	@Autowired
	private JedisManager jedisManager;
	@Autowired
	private IDeviceService deviceservice;
	@Autowired
	private ISyncService sysService;
	@Autowired
	private IRuleService RuleService;
	@Autowired
	private ILoginService loginService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IRuleService ruleService;

	

	/**
	 * 增加卡号
	 * @param sessionid
	 * @param
	 * @param CardType
	 * @param CardSonType
	 * @param
	 * @param
	 * @param FullName
	 * @param Phone
	 * @param Email
	 * @param Power
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "add_card", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object add_card(
			@RequestParam String sessionid,
			@RequestParam String CardNumber,
			@RequestParam Integer PersonID,
			@RequestParam Integer CardType,
			@RequestParam Integer CardSonType,
			@RequestParam String HouseNum,
			@RequestParam(required = false)String CardStartTime,
			@RequestParam(required = false) String CardEndTime,
			@RequestParam(required = false) String FullName,
			@RequestParam(required = false) String Phone,
			@RequestParam(required = false) String Email,
			@RequestParam String Power,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("增加卡号CardNumber===" + CardNumber);
		Map<String, Object> map = new HashMap<>();
		// 验证卡号是否重复
		Integer result = -1;
		String msg = "";
		Integer Ctotle = CardService.queryCountByNumber(CardNumber);
		if (Ctotle > 0) {
			log.info("卡号重复,直接返回");
			result = -2;
			msg = "卡号重复,已存在";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		//验证手机号是否重复
		if(null !=Phone) {
			log.info("开始验证手机号是否重复Phone-->"+Phone);
			Integer totle= CardService.queryCountCardIdByPhone(Phone);
			if(totle > 0) {
				log.info("手机号重复,直接返回");
				result = -2;
				msg = "手机号重复";
				map.put("result", result);
				map.put("msg", msg);
				return map;
			}
		}
		// 增加卡号
		String OldCardID = UUID.randomUUID().toString();
		String PersonIdentification = null;
		if ("-1".equals(CardStartTime)) {
			log.info("CardStartTime=========" + CardStartTime);
			CardStartTime = null;
		}
		if ("-1".equals(CardEndTime)) {
			log.info("CardEndTime=========" + CardEndTime);
			CardEndTime = null;
		}
		Integer CardState = 0;
		Card card = new Card(CardNumber, CardType, CardSonType, CardState, CardStartTime, CardEndTime, FullName, Phone,
				Email, Power, OldCardID, PersonIdentification,HouseNum,PersonID);
		Integer row = CardService.addCard(card);
		Integer cardid = card.getCardID();
		if (row > 0) {
			result = 0;
		}
		map.put("result", result);
		map.put("msg", msg);
		map.put("CardId", cardid);
		return map;
	}



	/**
	 * 删除卡数据
	 * @param sessionid
	 * @param CardID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delete_card", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object delete_card(
			@RequestParam String sessionid,
			@RequestParam String CardID,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("删除卡数据CardID===" + CardID);
		Integer result = 1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		result= CardService.delete_card(CardID);

		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 查询卡数据
	 * @param sessionid
	 * @param
	 * @param CardType
	 * @param CardSonType
	 * @param FullName
	 * @param
	 * @param
	 * @param
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "query_card", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object query_card(
			@RequestParam String sessionid,
			@RequestParam(required = false) String CardNumber,
			@RequestParam(required = false) Integer CardType,
			@RequestParam(required = false) Integer CardSonType,
			@RequestParam(required = false) String FullName,
			@RequestParam Integer currentpage,
			@RequestParam Integer pagesize,
			HttpServletRequest request,
			HttpServletResponse response) {
		log.info("查询卡数据CardNumber===" + CardNumber);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("CardNumber", CardNumber);
		params.put("CardType", CardType);
		params.put("CardSonType", CardSonType);
		params.put("FullName", FullName);
		// 获取条数
		Integer totle = CardService.queryCountCards(params);
		List<Map<String, Object>> Cards = null;
		if (totle > 0) {
			Cards = CardService.queryCards(params);
			for (int i = 0; i < Cards.size(); i++) {
				Integer personID = (Integer) Cards.get(i).get("PersonID");
				List<Map<String, Object>> rule = ruleService.queryDeviceNameAndLiftRuleByPersonID(personID);
				Cards.get(i).put("rule",rule);
			}
		}
		map.put("totle", totle);
		map.put("Cards", Cards);
		return map;
	}
	
	
	/**
	 * 开启发卡模式
	 * @param sessionid
	 * @param DeviceID
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(value = "open_distributioncard", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object open_distributioncard(
			@RequestParam String sessionid,
			@RequestParam String DeviceID,
			@RequestParam Integer Flag,
			@RequestParam String SecretType,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		log.info("开启发卡模式DeviceID===" + DeviceID);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		ScoketServer ScoketServer = new ScoketServer();
		// 发送握手指令
		int handshake_result = ScoketServer.handshake();
		if (handshake_result == -10) {
			map.put("result", handshake_result);
			map.put("msg", msg);
			return map;
		}
		int open_return = ScoketServer.open_distributioncard(DeviceID, Flag, SecretType);
		if (open_return == 0) {
			log.info("发送指令成功,开始获取返回值");
			for (int i = 0; i < 30; i++) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar c = new GregorianCalendar();
				Date date = new Date();
				log.info("系统当前时间      ：" + df.format(date));
				Thread.sleep(1000);
				log.info("i============" + i);
				String Key = "open_distributioncard_return";
				String Json = jedisManager.getValueByStr(0, Key);
				log.info("Json=========" + Json);
				if (Json != null) {
					log.info("获取到了返回值,判断是否为正确的返回值");
					if (Json.equals("010000")) {
						result = 0;
						msg = "返回成功";
						map.put("result", result);
						map.put("msg", msg);
						return map;
					} else  {
						log.info("返回值错误");
						msg = "返回值错误,错误值为--"+Json;
						result = -3;
						map.put("result", result);
						map.put("msg", msg);
						return map;
					}

				} else {
					if (i == 29) {
						log.info("进来了");
						msg = "设备不在线";
						result = -2;
					}
				}

			}

		} else if (open_return == 10) {
			result = 10;
		} else {
			
			msg = "发送失败";
		}
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * 前端循环获取刷卡的卡信息
	 * @param sessionid
	 * @param DeviceID
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	@RequestMapping(value = "queryreturncard", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object queryreturncard(
			@RequestParam String sessionid,
			@RequestParam String DeviceID,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
		log.info("前端循环获取刷卡的卡信息DeviceID===" + DeviceID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String Key = "returncard";
		String bb = jedisManager.getValueByStr(0, Key);
		jedisManager.delValueByStr(0, Key);
		log.info("bb==================" + bb);
		if (bb == null) {
			result = -3;
			map.put("Cards", bb);
			map.put("result", result);
		} else {
			String len = bb.substring(6, 8);
			System.out.println("len========" + len);
			int int_ien = ByteUtil.Str16toint10(len);
			System.out.println("int_ien----------->" + int_ien);
			// 获取类型
			String type = bb.substring(10, 12);
			System.out.println("type---------->" + type);
			// 获取卡号长度
			int cardlen = Integer.valueOf(bb.substring(12, 14));
			System.out.println("cardlen------" + cardlen);
			// 转义为10进制
			int len_int = ByteUtil.Str16toint10(len);
			System.out.println("数据长度为len_int==========" + len_int);
			// 获取数据
			String text = bb.substring(14, 10 + len_int * 2);
			System.out.println("text========" + text);

			if (type.equals("03")) {
				log.info("身份卡上报,直接获取卡号即可---->"+type);
				result = 0;
				String cardnumber = text.substring(0, cardlen * 2);
				Integer CardID = CardService.queryCardIDbyNumber(cardnumber, 0);// 页面根据有没有Cardid判断是否发卡
				log.info("cardnumber========" + cardnumber);
				map.put("CardID", CardID);
				map.put("CardNumber", cardnumber);
				map.put("result", result);
			} else if (type.equals("04")) {

				log.info("配置卡上报,获取卡片的配置信息---->" + type);
				result = 0;
				String cardnumber = text.substring(0, cardlen * 2);
				Integer CardID = CardService.queryCardIDbyNumber(cardnumber, 0);// 页面根据有没有Cardid判断是否发卡
				log.info("cardnumber========" + cardnumber);
				// 获取卡配置
				if (cardlen * 2 >= text.length() - 2) {
					log.info("卡号无配置");
					map.put("result", result);
					map.put("CardID", CardID);
					map.put("CardNumber", cardnumber);
					return map;
				}
				String Configs = text.substring(cardlen * 2, text.length());
				log.info("Configs----" + Configs);
				// 获取前5为 判断:7667636667:
				String cfg_head = Configs.substring(0, 10);
				if (!cfg_head.equals("7667636667")) {
					log.info("配置头错误");
					map.put("result", result);
					map.put("CardID", CardID);
					map.put("CardNumber", cardnumber);
					return map;
				}
				Configs= Configs.substring(10, Configs.length());
				log.info("Configs----" + Configs);
				// 解密
				String key = "Nm-#km=IlXER=soP";
				String iv = "oBLEW#UkvmP=WXC=";
				Configs = AESUtil.decryptNoBase64(ByteUtil.Str16toBytes(Configs), key.getBytes(), iv.getBytes());
				log.info("Configs+++++++++++++++++" + Configs);
				if (Configs == null) {
					log.info("解密失败");
					map.put("result", result);
					map.put("CardID", CardID);
					map.put("CardNumber", cardnumber);
					return map;
				}
				boolean AAA = Configs.contains("&");
				if (AAA == false) {
					log.info("卡号无配置");
					map.put("result", result);
					map.put("CardID", CardID);
					map.put("CardNumber", cardnumber);
					return map;
				}
				String[] strarr = Configs.split("&");
				for (int i = 0; i < strarr.length; i++) {
					if (i == 1) {
						log.info("wifi姓名---->" + strarr[1]);
						map.put("devicewifiname", strarr[1]);
					} else if (i == 2) {
						log.info("wifi密码---->" + strarr[2]);
						map.put("devicewifipassword", strarr[2]);
					} else if (i == 4) {
						log.info("网络类型 1以太网 2wifi ---->" + strarr[4]);
						map.put("netType", strarr[4]);
					} else if (i == 5) {
						log.info("IP 类型1动态  2静态---->" + strarr[5]);
						map.put("ip_model", strarr[5]);
					} else if (i == 6) {
						log.info("IP---->" + strarr[6]);
						map.put("deviceIP", strarr[6]);
					} else if (i == 7) {
						log.info("掩码---->" + strarr[7]);
						map.put("mask", strarr[7]);
					} else if (i == 8) {
						log.info("网关---->" + strarr[8]);
						map.put("gateway", strarr[8]);
					} else if (i == 9) {
						log.info("Dns---->" + strarr[9]);
						map.put("dns", strarr[9]);
					} else if (i == 12) {
						log.info("设备类型 1门禁  2梯控---->" + strarr[12]);
						map.put("deviceType", strarr[12]);
					} else if (i == 13) {
						log.info("1为普通设备  2为发卡器---->" + strarr[13]);
						map.put("flag", strarr[13]);
					} else if (i == 14) {
						log.info("秘钥类型---->" + strarr[14]);
						map.put("secretType", strarr[14]);
					} else if (i == 15) {
						log.info("服务器ip---->" + strarr[15]);
						map.put("serverip", strarr[15]);
					} else if (i == 16) {
						log.info("端口号---->" + strarr[16]);
						map.put("deviceport", strarr[16]);
					}else if (i == 27) {
						log.info("梯控开关---->" + strarr[27]);
						map.put("ElevatorSwitch", strarr[27]);
					}
				}
				map.put("result", result);
				map.put("CardID", CardID);
				map.put("CardNumber", cardnumber);

			} else {
				log.info("门禁卡或梯控卡,解析卡片内容------>"+type);
				String cardnumber = text.substring(0, cardlen * 2);
				log.info("cardnumber========" + cardnumber);
				// 获取组织编号(无效数据)
				String orgid = text.substring(cardlen * 2, 16);
				// 获取卡子类型
				String cardsontype = text.substring(cardlen * 2 + 8, 18);
				// 获取开始时间
				String StartTime = text.substring(cardlen * 2 + 10, 26);
				String S1 = StartTime.substring(0, 2);
				String S2 = StartTime.substring(2, 4);
				String S3 = StartTime.substring(4, 6);
				String S4 = StartTime.substring(6, 8);
				int sh = Integer.parseInt(S1, 16);
				int sh1 = Integer.parseInt(S2, 16);
				int sh2 = Integer.parseInt(S3, 16);
				int sh3 = Integer.parseInt(S4, 16);
				int stime = sh | (sh1 << 8) | (sh2 << 16) | (sh3 << 24);
				log.info("开始时间为>>>" + stime);
				// 获取结束时间
				String EndTime = text.substring(cardlen * 2 + 18, 34);
				String E1 = EndTime.substring(0, 2);
				String E2 = EndTime.substring(2, 4);
				String E3 = EndTime.substring(4, 6);
				String E4 = EndTime.substring(6, 8);
				int eh = Integer.parseInt(E1, 16);
				int eh1 = Integer.parseInt(E2, 16);
				int eh2 = Integer.parseInt(E3, 16);
				int eh3 = Integer.parseInt(E4, 16);
				int etime = eh | (eh1 << 8) | (eh2 << 16) | (eh3 << 24);
				log.info("结束时间为>>>" + etime);
				// 获取卡类型
				String card_type = text.substring(cardlen * 2 + 26, 40);
				log.info("cardtype========" + card_type);
				String cardtype = "";
				if (card_type.equals("6d6a6b")) {
					cardtype = "0";
				} else if (card_type.equals("746b6b")) {
					cardtype = "1";
				}
				if (len.equals("14")) {
					// 管理员卡没有权限数据
				} else {
					String power_len = text.substring(0, text.indexOf(card_type));
					String power = text.substring(power_len.length() + 6, text.length());
					log.info("power=======" + power);
					ArrayList power_list = ByteUtil.Str16Get1(power);
					ArrayList DeviceNames = new ArrayList<>();
					// 获取设备名称
					for (int i = 0; i < power_list.size(); i++) {
						Integer deviceid = (Integer) power_list.get(i);
						String deviceName = deviceservice.queryDeviceNameByID(deviceid);
						DeviceNames.add(i, deviceName);
					}
					map.put("power", power_list);
					map.put("DeviceNames", DeviceNames);
				}
				Integer CardID = CardService.queryCardIDbyNumber(cardnumber, 0);// 页面根据有没有Cardid判断是否发卡
				result = 0;
				map.put("CardID", CardID);
				map.put("cardsontype", cardsontype);
				map.put("CardNumber", cardnumber);
				map.put("StartTime", stime);
				map.put("EndTime", etime);
				map.put("cardtype", cardtype);
				map.put("result", result);

			}
		}

		return map;
	}
	
	/**
	 * 下发卡号----四格梯控协议
	 * @param sessionid
	 * @param DeviceID
	 * @param
	 * @param
	 * @param
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(value = "distributioncard", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object distributioncard(
			@RequestParam String sessionid,
			@RequestParam Integer DeviceID,
			@RequestParam Integer CardID,
			@RequestParam(required = false) String Supply_CardNumber,
			@RequestParam String New_CardNumber,
			@RequestParam Integer CardType,
			@RequestParam Integer CardSonType,
			@RequestParam String Powers,
			@RequestParam long StartTime,
			@RequestParam long EndTime,
			@RequestParam Integer Flag,
			@RequestParam String OperatorID,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		log.info("下发卡权限DeviceID===" + DeviceID);
		List<String> powers_rule_list = Arrays.asList(Powers.split(","));// 内容为ruleid,梯控使用
		List<String> powers_list = new ArrayList<>();// 内容为deviceid,门禁使用
		for (int i = 0; i < powers_rule_list.size(); i++) {
			String powers_ruleid = powers_rule_list.get(i);
			String deviceid = RuleService.queryDeviceidByRuleid(powers_ruleid);
			log.info("deviceid-------->" + deviceid);
			powers_list.add(i, deviceid);
		}
		log.info("powers_list-------->" + powers_list);
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		ScoketServer ScoketServer = new ScoketServer();
		// 根据卡号查询是否存在于系统中
		Integer CardState = 0;
		if (Flag == 1) {
			Integer Supply_CardID = CardService.queryCardIDbyNumber(Supply_CardNumber, CardState);
			log.info("Supply_CardID ====" + Supply_CardID);
			if (Supply_CardID == null) {
				result = -2;
				msg = "当前补卡卡号不存在或以失效";
				map.put("result", result);
				map.put("msg", msg);
				return map;
			} else {
				log.info("当前补卡卡号存在,修改状态为");
			}
		}

		// 因卡号长度不定,长度单算
		byte[] head = ByteUtil.Str16toBytes("55aa53");// 协议头
		byte[] len_byte = new byte[2]; // 长度
		byte[] Org_byte = new byte[4]; // 组织编号
		byte[] Card_Son_Type = new byte[1];
		byte[] Start_Time = ByteUtil.unlong2H4bytes(StartTime);// 开始时间
		byte[] End_Time = ByteUtil.unlong2H4bytes(EndTime); // 结束时间
		byte[] Card_Type = new byte[3];
		byte[] mj_power = new byte[32];// 门禁权限
		byte[] tk_power = new byte[80];// 梯控权限
		byte[] Card_Flag = new byte[1]; // 补卡标识
		byte[] crc = new byte[1];// 异或校验
		log.info("获取组织唯一标识");
		int random = StringUtil.random();
		String SysConfigName = "组织身份标识";
		Integer SysConfigType = 2;
		String SysConfigKey = "OrgIdentity";
		String SysConfigValue = String.valueOf(random);
		String OrgIdentity = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue, null, 1,
				1);
		log.info("OrgIdentity=====" + OrgIdentity);
		Org_byte = ByteUtil.unlong2H4bytes(Integer.valueOf(OrgIdentity));// 组织编号
		String len = "";
		if (CardType == 0) {
			log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<门禁卡>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Card_Type = ByteUtil.Str16toBytes("6d6a6b");
			if (CardSonType == 0) {
				Card_Son_Type = ByteUtil.Str16toBytes("00");
				log.info("===============================管理员卡===============================");
				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
					//
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}

			} else if (CardSonType == 1) {
				Card_Son_Type = ByteUtil.Str16toBytes("01");
				log.info("===============================时段卡卡===============================");
				for (int i = 0; i < powers_list.size(); i++) {
					String Str_power = powers_list.get(i);
					Integer power = Integer.valueOf(Str_power);

					int index = 31 - (((power % 8) == 0) ? (power / 8 - 1) : (power / 8));
					int off = (((power % 8) == 0) ? 8 : (power % 8)) - 1;
					mj_power[index] = (byte) (mj_power[index] | (1 << off));

				}
				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 32 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+32为权限+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, mj_power, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 32 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+32为权限+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, mj_power, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}

			} else if (CardSonType == 2) {
				Card_Son_Type = ByteUtil.Str16toBytes("02");
				log.info("===============================普通卡===============================");
				for (int i = 0; i < powers_list.size(); i++) {
					String Str_power = powers_list.get(i);
					Integer power = Integer.valueOf(Str_power);
					int index = 31 - (((power % 8) == 0) ? (power / 8 - 1) : (power / 8));
					int off = (((power % 8) == 0) ? 8 : (power % 8)) - 1;
					mj_power[index] = (byte) (mj_power[index] | (1 << off));

				}
				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 32 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+32为权限+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, mj_power, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 32 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+6d6a6b+32为权限+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, mj_power, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}

			}
		} else if (CardType == 1) {
			Card_Type = ByteUtil.Str16toBytes("746b6b");
			log.info(
					"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<梯控卡>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (CardSonType == 0) {
				Card_Son_Type = ByteUtil.Str16toBytes("00");
				log.info("===============================管理员卡===============================");
				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}

			} else if (CardSonType == 1) {
				Card_Son_Type = ByteUtil.Str16toBytes("01");
				log.info("===============================时段卡卡===============================");
				// 计算梯控权限
				tk_power = RuleService.Calculation_tk_power(tk_power, powers_rule_list);

				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 80 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+80为权限+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, tk_power, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 80 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+80为权限+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, tk_power, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}

			} else if (CardSonType == 2) {
				Card_Son_Type = ByteUtil.Str16toBytes("02");
				log.info("===============================普通卡===============================");
				// 计算梯控权限
				tk_power = RuleService.Calculation_tk_power(tk_power, powers_rule_list);
				System.out.println("tk_power-----最后的的16进制为：："+ByteUtil.byte2Hex(tk_power));
				if (Flag == 0) {
					log.info("-----------------------新卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("00");
					int length = 4 + 1 + 4 + 4 + +3 + 80 + 1;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+80为权限+卡标识
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, tk_power, Card_Flag);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				} else if (Flag == 1) {
					log.info("-----------------------补卡-----------------------");
					Card_Flag = ByteUtil.Str16toBytes("01");
					int length = 4 + 1 + 4 + 4 + 3 + 80 + 1 + Supply_CardNumber.length() / 2;// 4字节组织编号+1字节卡类型+4开始时间+4结束时间+746b6b+80为权限+卡标识+n字节的补卡卡号
					byte r = (byte) length;
					len_byte[0] = r;
					byte[] CardNumber = ByteUtil.Str16toBytes(Supply_CardNumber);// 补卡卡号
					byte[] texts = ByteUtil.concatBytes(head, len_byte, Org_byte, Card_Son_Type, Start_Time, End_Time,
							Card_Type, tk_power, Card_Flag, CardNumber);
					// 获取异或
					crc = ByteUtil.bytesXorCrc(texts);
					// 最终数据
					byte[] bytes = ByteUtil.concatBytes(texts, crc);
					log.info(".........下发卡数据........");
					int open_return = ScoketServer.sendOutCard(bytes);
				}
			}
		}

		Integer count_down_time = 30;
		String Key = "Card_result";
		map = CardService.Receive_Value(count_down_time, Key);
		result = (Integer) map.get("result");
		if (result != 0) {
			map.put("result", result);
			return map;
		} else {
			// 增加到记录表中
			Integer BlackListID = null;
			Integer State = 1;
			log.info("发卡成功,修改卡数据");
			Integer power = 1;
			if (CardSonType == 0) {
				power = 0;
			}
			String Str_StartTime = null;
			String Str_EndTime = null;
			if (!"".equals(StartTime)) {
				Str_StartTime = TimeUtil.second2str(StartTime);
			}
			if (!"".equals(EndTime)) {
				Str_EndTime = TimeUtil.second2str(EndTime);
			}
			// Integer row= CardService.updateDistributionCard(CardType, CardSonType,
			// CardState, Str_StartTime, Str_EndTime, power, CardID);
			Integer row = RecordsService.InsertRecords(CardID.toString(), DeviceID, BlackListID, State,
					Integer.valueOf(OperatorID));

			map.put("result", 0);
		}

		return map;
	}
	
	
	/**
	 * 下发身份id
	 * @param sessionid
	 * @param CardNumber
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	@RequestMapping(value = "distributionidentity", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object distributionidentity(
			@RequestParam String sessionid,
			@RequestParam String CardNumber,
			@RequestParam String Flag,//补卡标识  0 新  1补
			@RequestParam String SecretType,//秘钥类型  A:60 B:61
			@RequestParam (required = false) String repairCardNumber,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		log.info("下发卡身份idCardNumber===" + CardNumber);
		Integer result = -1;
		String msg = "";
		String P_Power_Sector_Secret = sysService.queryValueByKey("P_Power_Sector_Secret"); // 人员权限扇区密钥
		String P_Blacklist_Sector_Secret = sysService.queryValueByKey("P_Blacklist_Sector_Secret"); // 人员黑名单扇区密钥
		String P_Power_Sector = sysService.queryValueByKey("P_Power_Sector"); // 人员权限扇区
		String P_Blacklist_Sector = sysService.queryValueByKey("P_Blacklist_Sector"); // 人员黑名单扇区
		Map<String, Object> map = new HashMap<>();
		ScoketServer ScoketServer = new ScoketServer();
		// 根据卡号查询是否存在于系统中
		Integer CardState = 0;
		Integer CardID = null;
		CardID = CardService.queryCardIDbyNumber(CardNumber, CardState);
		log.info("CardID ====" + CardID);
		if (CardID == null) {
			result = -2;
			msg = "当前卡号不存在或以失效";
			map.put("result", result);
			map.put("msg", msg);
			return map;
		}
		Integer len = CardID.toString().length();
		String CardIDLen =null;
		if(len <10) {
			CardIDLen = "0"+len.toString();
		}
		log.info("开始下发身份id--CardID----" + CardID);
		byte[] head = ByteUtil.Str16toBytes("55aa56");// 协议头
		byte[] len_byte = new byte[2]; // 长度
		// byte[] sector = ByteUtil.Str16toBytes("0a"); // 扇区
		// byte[] block_number = ByteUtil.Str16toBytes("02"); // 块号
		// byte[] secret_type = new byte[1]; // 秘钥类型
		// byte[] secret = new byte[6]; // 秘钥
		byte[] ID_len = ByteUtil.Str16toBytes(CardIDLen); // 身份id长度
		// byte[] ID = new byte[15]; // 身份id
		byte[] repair_cardtype = new byte[1]; // 补卡标识
		byte[] repair_CardNumber_len = new byte[1]; // 补卡卡号长度
		byte[] repair_CardNumber = new byte[CardNumber.length()/2]; // 补卡卡号
		byte[] crc = new byte[1];// 异或校验
		byte[] ID = CardID.toString().getBytes();
		// 总长度
		if (Flag.equals("0")) {
			int length = 3+2+1+len+1;
			byte r = (byte) length;
			len_byte[0] = r;
			repair_cardtype = ByteUtil.Str16toBytes("00");
		} else if (Flag.equals("1")) {
			int length = 3+2+1+len+1+1+CardNumber.length();
			byte r = (byte) length;
			len_byte[0] = r;
			repair_cardtype = ByteUtil.Str16toBytes("01");
			//补卡卡号长度
			Integer Repair_len = CardNumber.length()/2;
			String Repair_CardIDLen =null;
			if(len <10) {
				Repair_CardIDLen = "0"+Repair_len.toString();
			}
			
			repair_CardNumber_len =ByteUtil.Str16toBytes(Repair_CardIDLen); 
			// 补卡的原物理卡号
			repair_CardNumber = repairCardNumber.getBytes();
		}
		// 拼接数据字符串
		byte[] texts = ByteUtil.concatBytes(head, len_byte, ID_len, ID,repair_cardtype,repair_CardNumber_len, repair_CardNumber);
		// 获取异或
		crc = ByteUtil.bytesXorCrc(texts);
		// 最终数据
		byte[] bytes = ByteUtil.concatBytes(texts, crc);
		log.info(".........下发卡数据........");
		int open_return = ScoketServer.sendOutCard(bytes);

		if (open_return == 0) {
			Integer count_down_time = 30;
			String Key = "distributionidentity_result";
			map = CardService.Receive_Value(count_down_time, Key);
		} else {
			msg = "发送失败";
		}

		return map;

	}
	
	
	/**
	 * 配置卡
	 * @param sessionid
	 * @param deviceType
	 * @param netType
	 * @param ip_model
	 * @param devicewifiname
	 * @param devicewifipassword
	 * @param deviceIP
	 * @param mask
	 * @param gateway
	 * @param dns
	 * @param deviceport
	 * @param serverip
	 * @param secretType
	 * @param flag
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "deviceconfigcard", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object deviceconfigcard(@RequestParam String sessionid,
			@RequestParam(required = false) String deviceType, // 1门禁 2梯控
			@RequestParam(required = false) String netType, // 1以太网 2wifi
			@RequestParam(required = false) String ip_model, // 1动态 2静态
			@RequestParam(required = false) String devicewifiname,
			@RequestParam(required = false) String devicewifipassword,
			@RequestParam(required = false) String deviceIP,
			@RequestParam(required = false) String mask,
			@RequestParam(required = false) String gateway,
			@RequestParam(required = false) String dns,
			@RequestParam(required = false) String deviceport, // 端口
			@RequestParam(required = false) String serverip, // 服务器ip
			@RequestParam(required = false) String secretType,
			@RequestParam(required = false) String ElevatorSwitch,//电梯开关  0关  1开
			@RequestParam Integer flag, 		//1为普通设备  2为发卡器
			@RequestParam Integer deviceid,
			@RequestParam String DeviceIdentification,//设备唯一标识
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		byte[] head = ByteUtil.Str16toBytes("55aa57");// 协议头
		byte[] len_byte = new byte[2];// 数据长度
		byte[] qr_byte = new byte[2];// 数据长度
		byte[] RepairCard = new byte[1];// 补卡标识
		byte[] crc = new byte[1];// 异或校验
		// 拼接配置字符串
		String Power_Sector_Secret = null;
		String Blacklist_Sector_Secret = null;
		String Power_Sector = null;
		String Blacklist_Sector = null;

		String P_Power_Sector_Secret = null;
		String P_Blacklist_Sector_Secret = null;
		String P_Power_Sector = null;
		String P_Blacklist_Sector = null;
		
		String D_Power_Sector_Secret = null;
		String D_Blacklist_Sector_Secret = null;
		String D_Power_Sector = null;
		String D_Blacklist_Sector = null;
		log.info("普通设备");
		if ("1".equals(deviceType)) {
			log.info("门禁设备");
			// 开始获取门禁秘钥
			Power_Sector_Secret = sysService.queryValueByKey("M_Power_Sector_Secret"); // 门禁权限扇区密钥
			Blacklist_Sector_Secret = sysService.queryValueByKey("M_Blacklist_Sector_Secret"); // 门禁黑名单扇区密钥
			Power_Sector = sysService.queryValueByKey("M_Power_Sector"); // 门禁权限扇区
			Blacklist_Sector = sysService.queryValueByKey("M_Blacklist_Sector"); // 门禁黑名单扇区
			if (secretType.equals("A")) {
				log.info("A秘钥");
				// 截取前12位
				Power_Sector_Secret = Power_Sector_Secret.substring(0, 12); // 门禁权限扇区密钥
				Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12); // 门禁黑名单扇区密钥

			} else  {
				log.info("B秘钥");
				Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length() - 12,
						Power_Sector_Secret.length()); // 门禁权限扇区密钥
				Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length() - 12,
						Blacklist_Sector_Secret.length()); // 门禁黑名单扇区密钥
			}
		} else if ("2".equals(deviceType)) {
			log.info("梯控设备");
			Power_Sector_Secret = sysService.queryValueByKey("T_Power_Sector_Secret"); // 梯控权限扇区密钥
			Blacklist_Sector_Secret = sysService.queryValueByKey("T_Blacklist_Sector_Secret"); // 梯控黑名单扇区密钥
			Power_Sector = sysService.queryValueByKey("T_Power_Sector"); // 梯控权限扇区
			Blacklist_Sector = sysService.queryValueByKey("T_Blacklist_Sector"); // 梯控黑名单扇区
			if (secretType.equals("A")) {
				log.info("A秘钥");
				// 截取前12位
				Power_Sector_Secret = Power_Sector_Secret.substring(0, 12); // 门禁权限扇区密钥
				Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12); // 门禁黑名单扇区密钥

			} else  {
				log.info("B秘钥");
				Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length() - 12,
						Power_Sector_Secret.length()); // 门禁权限扇区密钥
				Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length() - 12,
						Blacklist_Sector_Secret.length()); // 门禁黑名单扇区密钥
			}
		}
		// 共有
		log.info("人员权限");
		P_Power_Sector_Secret = sysService.queryValueByKey("P_Power_Sector_Secret"); // 人员权限扇区密钥
		P_Blacklist_Sector_Secret = sysService.queryValueByKey("P_Blacklist_Sector_Secret"); // 人员黑名单扇区密钥
		P_Power_Sector = sysService.queryValueByKey("P_Power_Sector"); // 人员权限扇区
		P_Blacklist_Sector = sysService.queryValueByKey("P_Blacklist_Sector"); // 人员黑名单扇区
		if (secretType.equals("A")) {
			log.info("A秘钥");
			// 截取前12位
			P_Power_Sector_Secret = P_Power_Sector_Secret.substring(0, 12); // 员权限扇区密钥
			P_Blacklist_Sector_Secret = P_Blacklist_Sector_Secret.substring(0, 12); // 人员黑名单扇区密钥

		} else {
			log.info("B秘钥");
			P_Power_Sector_Secret = P_Power_Sector_Secret.substring(P_Power_Sector_Secret.length() - 12,
					P_Power_Sector_Secret.length()); // 员权限扇区密钥
			P_Blacklist_Sector_Secret = P_Blacklist_Sector_Secret.substring(P_Blacklist_Sector_Secret.length() - 12,
					P_Blacklist_Sector_Secret.length()); // 人员黑名单扇区密钥
		}
		
		
		log.info("卡配置权限");
		D_Power_Sector_Secret = sysService.queryValueByKey("D_Power_Sector_Secret"); // 卡配置权限扇区密钥
		D_Blacklist_Sector_Secret = sysService.queryValueByKey("D_Blacklist_Sector_Secret"); // 卡配置黑名单扇区密钥
		D_Power_Sector = sysService.queryValueByKey("D_Power_Sector"); // 卡配置权限扇区
		D_Blacklist_Sector = sysService.queryValueByKey("D_Blacklist_Sector"); // 卡配置黑名单扇区
		if (secretType.equals("A")) {
			log.info("A秘钥");
			// 截取前12位
			D_Power_Sector_Secret = D_Power_Sector_Secret.substring(0, 12); //卡配置权限扇区密钥
			D_Blacklist_Sector_Secret = D_Blacklist_Sector_Secret.substring(0, 12); // 卡配置黑名单扇区密钥

		} else  {
			log.info("B秘钥");
			D_Power_Sector_Secret = D_Power_Sector_Secret.substring(D_Power_Sector_Secret.length() - 12,
					D_Power_Sector_Secret.length()); // 卡配置权限扇区密钥
			D_Blacklist_Sector_Secret = D_Blacklist_Sector_Secret.substring(D_Blacklist_Sector_Secret.length() - 12,
					D_Blacklist_Sector_Secret.length()); // 卡配置黑名单扇区密钥
		}
		
		log.info("获取组织唯一标识");
		int random = StringUtil.random();
		String SysConfigName ="组织身份标识";
		Integer SysConfigType =2;
		String SysConfigKey ="OrgIdentity";
		String SysConfigValue =String.valueOf(random);
		String OrgIdentity = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue, null, 1, 1);
		log.info("OrgIdentity====="+OrgIdentity);
		byte[] Repair_Card =new byte[1];//暂时默认为新卡
		String Configs = deviceid + "&" + devicewifiname + "&" + devicewifipassword + "&" + DeviceIdentification + "&" + netType + "&" + ip_model + "&" + deviceIP + "&"
				+ mask + "&" + gateway + "&" + dns + "&" + null + "&" + null + "&" + deviceType + "&" + flag + "&"
				+ secretType + "&" + serverip + "&" + deviceport + "&" + Power_Sector_Secret + "&"
				+ Blacklist_Sector_Secret + "&" + Power_Sector + "&" + Blacklist_Sector +"&"+ P_Power_Sector_Secret 
			    + "&" + P_Power_Sector + "&" + D_Power_Sector +"&"+ D_Power_Sector_Secret + "&" +ElevatorSwitch +"&" +OrgIdentity;
		log.info("Configs+++++++++++++++++" + Configs);
		
		
		String key = "Nm-#km=IlXER=soP";
		String iv = "oBLEW#UkvmP=WXC=";
		
		byte[] congfig = AESUtil.cbcEncryptToBytes(Configs, key, iv);
		String test = ByteUtil.byte2Hex(congfig);
		log.info("test-----"+test);
		String qrcode = "7667636667"+test;
		log.info("qrcode-----"+qrcode);
		RepairCard[0]= 0;//补卡标识
		congfig = ByteUtil.Str16toBytes(qrcode);// 协议头
		// 获取字符串的长度
		int length = (qrcode.length()/2);//加1为补卡标识
		log.info("length-----"+length);
		qr_byte= ByteUtil.unlong2H2bytes(length);
		 int all_len = length+2+1;
		 log.info("all_len-----"+all_len);
		len_byte= ByteUtil.unlong2H2bytes(all_len);
		//byte[] congfig = test.getBytes();
		// 拼接数据字符串
		byte[] texts = ByteUtil.concatBytes(head ,len_byte ,qr_byte, congfig,RepairCard);
		log.info("texts==="+ByteUtil.byte2Hex(texts));
		// 获取异或
		crc = ByteUtil.bytesXorCrc(texts);
		log.info("crc====="+ByteUtil.byte2Hex(crc));
		// 最终数据
		byte[] bytes = ByteUtil.concatBytes(texts, crc);
		log.info(".........下发卡数据........");
		int open_return = ScoketServer.sendOutCard(bytes);

		if (open_return == 0) {
			Integer count_down_time = 30;
			String Key = "deviceconfigcard_result";
			map = CardService.Receive_Value(count_down_time, Key);
		} else {
			msg = "发送失败";
		}

		return map;

	}
	

//	/**
//	 * 发卡---日立梯控协议
//	 * @param sessionid
//	 * @param DeviceID
//	 * @param CardID
//	 * @param Supply_CardNumber
//	 * @param New_CardNumber
//	 * @param CardType
//	 * @param CardSonType
//	 * @param Powers
//	 * @param StartTime
//	 * @param EndTime
//	 * @param Flag
//	 * @param OperatorID
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws IOException
//	 * @throws InterruptedException
//	 */
//	@RequestMapping(value = "rl_distributioncard", method = RequestMethod.POST)
//	@ResponseBody
//	@CrossOrigin
//	public Object rl_distributioncard(
//			@RequestParam String sessionid,
//			@RequestParam Integer DeviceID,
//			@RequestParam Integer CardID,
//			@RequestParam(required = false) String Supply_CardNumber,
//			@RequestParam String New_CardNumber,
//			@RequestParam Integer CardType,
//			@RequestParam Integer CardSonType,
//			@RequestParam String Powers,
//			@RequestParam long StartTime,
//			@RequestParam long EndTime,
//			@RequestParam Integer Flag,
//			@RequestParam String OperatorID,
//			@RequestParam String CardValidPeriod,//卡片有效期
//			@RequestParam String Frequency,//计次卡次数
//			@RequestParam Integer PersonID,//人员id
//			HttpServletRequest request,
//			HttpServletResponse response) throws IOException, InterruptedException {
//		log.info("下发卡权限DeviceID===" + DeviceID);
//		List<String> powers_rule_list = Arrays.asList(Powers.split(","));// 内容为ruleid,梯控使用
//		log.info("powers_rule_list:"+powers_rule_list);
//		Integer result = -1;
//		String msg = "";
//		Map<String, Object> map = new HashMap<>();
//		ScoketServer ScoketServer = new ScoketServer();
//		// 根据卡号查询是否存在于系统中
//		Integer CardState = 0;
//		Integer Supply_CardID = CardService.queryCardIDbyNumber(Supply_CardNumber, CardState);
//		if (CardType == 4) {
//			log.info("Supply_CardID ====" + Supply_CardID);
//			if (Supply_CardID == null) {
//				result = -2;
//				msg = "当前补卡卡号不存在或以失效";
//				map.put("result", result);
//				map.put("msg", msg);
//				return map;
//			} else {
//				log.info("当前补卡卡号存在,修改状态为");
//			}
//		}
//		// 获取所有权限的总条数----以层为单位 1号楼1层 1号楼2层
//		int totle =0;
//		log.info("powers_rule_list.size():"+powers_rule_list.size());
//		List<HashMap<String, Object>> all_powers_list_map = new ArrayList<>();
//		for (int i = 0; i < powers_rule_list.size()-1; i++) {
//			log.info("totle:"+totle);
//			String powers_ruleid = powers_rule_list.get(i);
//			log.info("powers_ruleid:"+powers_ruleid);
//			String LiftRules = RuleService.queryLiftRuleByRuleid(powers_ruleid);
//			log.info("LiftRules:"+LiftRules);
//			String deviceid = RuleService.queryDeviceidByRuleid(powers_ruleid);
//			log.info("deviceid:"+deviceid);
//			if (LiftRules.equals("0")) {
//				log.info("整楼权限，直接赋值0即可");
//				HashMap<String, Object> power = new HashMap<>();
//				power.put("LiftRule", 0);
//				power.put("deviceid", deviceid);
//				all_powers_list_map.add(totle, power);
//				totle = totle+1;
//			} else {
//				log.info("非通用权限");
//				List<String> LiftRule_list = Arrays.asList(LiftRules.split(","));
//				for (int j = 0; j < LiftRule_list.size(); j++) {
//					String LiftRule = LiftRule_list.get(j);
//					Integer power_len = Integer.valueOf(LiftRule);
//					//log.info("计算偏值");// 因协议中的楼层00为一层
//					// 获取设备偏执
//					Integer FloorDifference = deviceservice.queryFloorDifferenceByDeviceID(Integer.valueOf(deviceid));
//					if (null == FloorDifference) {
//						log.info("偏执为空,赋值为0");
//						FloorDifference = 0;
//					}
//					// 计算楼层权限
//					if (power_len == 0) {
//						log.info("0代表所有权限,所有无需计算");
//					} else {
//						if (power_len < 0) {
//							power_len = power_len + FloorDifference + 1 - 1;// +1是负数偏值 -1是协议00位为1层
//							log.info("power_len----" + power_len);
//						} else {
//							power_len = power_len + FloorDifference - 1;// -1是协议00位为1层
//						}
//					}
//
//					HashMap<String, Object> power = new HashMap<>();
//					power.put("LiftRule", power_len);
//					power.put("deviceid", deviceid);
//					all_powers_list_map.add(i, power);
//					totle = totle+1;
//				}
//			}
//		}
//
//		log.info("处理之后的数据为:" + all_powers_list_map);
//
//		// 定义数据
//
//		byte[] head = ByteUtil.Str16toBytes("55aa");
//
//		// 一个扇区3块 一块16字节
//		// 第一扇区第一块啊
//		byte[] village_code = new byte[3];// 小区码
//		byte[] flag = ByteUtil.Str16toBytes("00"); // 1字节标识
//		byte[] card_number = new byte[2]; // 2字节卡号
//		byte[] flag_2 = new byte[2]; // 2字节标识//
//
//		byte[] first_powor = new byte[8];// 8字节的扇区第一块权限
//		// 第一扇区第二块啊
//		byte[] second_powor = new byte[8];// 8字节的扇区第二块权限
//		byte[] card_type = new byte[8];// 8字节的卡号类型
//		// 第一扇区第三块啊
//		byte[] third_powor = new byte[2];// 2字节的扇区第三块权限
//		byte[] powor_lenth = new byte[4];// 4字节的总权限大致长度
//		// 0X02当楼层权限超过 13 组时，写 0X06，否则写 0X00
//		// 0X03当楼层权限超过 37 组时，写 0X07，否则写 0X00
//		// 0X04当楼层权限超过 61 组时，写 0X08，否则写 0X00
//		// 0X05当楼层权限超过 85 组时，写 0X09，否则写 0X00
//		//Bit0—bit4:有效日，（1--31）,bit5-bit7，默认001
//		//0X01  本条以及上面一条对应flag_3
//		byte[] flag_3 =  new byte[2]; // 2字节标识
//		CardValidPeriod.substring(8,9);
//		byte[] bytes3 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(8, 10)));
//		bytes3[0]=(byte) (bytes3[0] | (0 << 5));
//		bytes3[0]=(byte) (bytes3[0] | (0 << 6));
//		bytes3[0]=(byte) (bytes3[0] | (1 << 7));
//		flag_3[0]=bytes3[0];
//		log.info("有效日-------------"+flag_3[0]);
//		flag_3[1]=(byte) 0x01;
//
//		byte[] forth_powor = new byte[8];// 8字节的扇区第二块权限
//		// 以下扇区的长度根据权限长度定,无需列举
//		// ---------------------第一扇区第一块----------------
//		village_code = ByteUtil.Str16toBytes("010101");//小区码暂时写死
//		log.info("小区码3位=-=="+village_code[0]+"---"+village_code[1]+"---"+village_code[2]);
//		card_number =ByteUtil.unlong2H2bytes(Integer.valueOf(Supply_CardNumber));
//		//log.info("卡号字段=-=="+card_number[0]+"---"+card_number[1]+"---"+card_number[2]+"---"+card_number[3]);
//		byte[] bytes1 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(2, 4)));
//		flag_2[0]= bytes1[0];
//		log.info("有效年份字段=-=="+flag_3[0]);
//		byte[] bytes2 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(5, 7)));
//		bytes2[0]=(byte) (bytes2[0] | (1 << 4));
//		bytes2[0]=(byte) (bytes2[0] | (0 << 5));
//		bytes2[0]=(byte) (bytes2[0] | (0 << 6));
//		bytes2[0]=(byte) (bytes2[0] | (1 << 7));
//		flag_2[1]=bytes2[0];
//		log.info("有效月份字段=-=="+flag_3[1]);
//		int all_powers_list_map_lenth = all_powers_list_map.size();
//		log.info("------------------" + all_powers_list_map_lenth);
//		int first = 0;
//		for (int i = 0; i <= 3; i++) {
//			if (all_powers_list_map_lenth > i) {// 当前权限
//				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
//				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
//				//log.info("LiftRule====="+LiftRule);
//				if (LiftRule.equals("0")) {
//					log.info("整楼权限，直接赋值bit7置1");
//					//电梯组
//					//byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					//first_powor_new[0] = (byte) (first_powor_new[0] | (1 << 7));
//					first_powor[first * 2] = (byte) 0x7F;
//					//楼层
//					//byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					first_powor[first * 2 + 1] = (byte) 0x80;//所有楼层均有权限
//				} else {
//					if (CardSonType==0) {//标识自动登记楼层。不清楚怎么判断先做test
//						//电梯组
//						log.info("deviceid值===" + deviceid);
//						byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						log.info("进入s==1:first_powor_new前===" + first_powor_new[0]);
//						first_powor_new[0] = (byte) (first_powor_new[0] | (1 << 7));
//						log.info("进入s==1:first_powor_new后===" + first_powor_new[0]);
//						first_powor[first * 2] = first_powor_new[0];
//						//楼层
//						log.info("LiftRule权限值===" + LiftRule);
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
//						first_powor[first * 2 + 1] = LiftRule_new[0];
//					} else {//手动登记楼层
//						//电梯组
//						byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						first_powor_new[0] = (byte) (first_powor_new[0] | (0 << 7));
//						first_powor[first * 2] = first_powor_new[0];
//						//楼层
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						first_powor[first * 2 + 1] = LiftRule_new[0];
//					}
//
//				}
//
//			} else {
//				log.info("开始执行自动补0");
//				first_powor[first * 2] = (byte) 0x00;
//				first_powor[first * 2 + 1] = (byte) 0x00;
//			}
//			first = first + 1;
//		}
//		System.out.println("first_powor：：" + ByteUtil.byte2Hex(first_powor));
//		for (byte b : first_powor) {
//			System.out.println("B:" + b);
//		}
//		// ---------------------第一扇区第二块----------------
//		// 权限8字节
//		int second = 0;
//		for (int i = 4; i <= 7; i++) {
//			if (all_powers_list_map_lenth > i) {// 当前权限
//				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
//				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
//				if (LiftRule.equals("0")) {
//					log.info("整楼权限，直接赋值bit7置1");
//					//电梯组
//					byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					second_powor_new[0] = (byte) (second_powor_new[0] | (1 << 7));
//					second_powor[second * 2] = second_powor_new[0];
//					//楼层
//					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					second_powor[second * 2 + 1] = (byte) 0x80;//所有楼层均有权限
//				} else {
//					if (CardSonType==0) {//标识自动登记楼层
//						//电梯组
//						log.info("deviceid值===" + deviceid);
//						byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						log.info("进入s==1:second_powor_new前===" + second_powor_new[0]);
//						second_powor_new[0] = (byte) (second_powor_new[0] | (1 << 7));
//						log.info("进入s==1:second_powor_new后===" + second_powor_new[0]);
//						second_powor[second * 2] = second_powor_new[0];
//						//楼层
//						log.info("LiftRule权限值===" + LiftRule);
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
//						second_powor[second * 2 + 1] = LiftRule_new[0];
//					} else {//手动登记楼层
//						//电梯组
//						byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						second_powor_new[0] = (byte) (second_powor_new[0] | (0 << 7));
//						second_powor[second * 2] = second_powor_new[0];
//						//楼层
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						second_powor[second * 2 + 1] = LiftRule_new[0];
//					}
//				}
//
//			} else {
//				log.info("开始执行自动补0");
//				second_powor[second * 2] = (byte) 0x00;
//				second_powor[second * 2 + 1] = (byte) 0x00;
//			}
//			second = second + 1;
//			log.info("本次循环结束");
//		}
//		System.out.println("second_powor：：" + ByteUtil.byte2Hex(second_powor));
//		for (byte b : second_powor) {
//			System.out.println("B:" + b);
//		}
//		//卡类型8字节
//		if(CardType==1){//乘梯卡
//			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
//			card_type[0] = b_CardType[0];
//			if(CardSonType==1){
//				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
//				b_CardSonType[0]=(byte) (b_CardSonType[0] | (0 << 0));
//				b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
//				card_type[1] = b_CardSonType[0];
//				log.info("开类型第2字段——————"+card_type[1]);
//			}
//			if(CardSonType==2) {
//				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
//				b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
//				b_CardSonType[0] = (byte) (b_CardSonType[0] | (0 << 1));
//				card_type[1] = b_CardSonType[0];
//			}
//			card_type[2] = (byte) 0X17;
//			//log.info("开类型第3字段——————"+card_type[2]);
//			card_type[3] = (byte) 0X3B;
//			//log.info("开类型第4字段——————"+card_type[3]);
//			card_type[4] = (byte) 0X00;
//			card_type[5] = (byte) 0X00;
//			card_type[6] = (byte) 0X00;
//			card_type[7] = (byte) 0X00;
//		}
//		if(CardType==2){//乘梯卡计次数
//			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
//			card_type[0] = b_CardType[0];
//			if(CardSonType==1){
//				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
//				b_CardSonType[0]=(byte) (b_CardSonType[0] | (0 << 0));
//				b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
//				card_type[1] = b_CardSonType[0];
//				log.info("开类型第2字段——————"+card_type[1]);
//			}
//			if(CardSonType==2) {
//				byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
//				b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
//				b_CardSonType[0] = (byte) (b_CardSonType[0] | (0 << 1));
//				card_type[1] = b_CardSonType[0];
//			}
//			card_type[2] = (byte) 0X17;
//			log.info("开类型第3字段——————"+card_type[2]);
//			card_type[3] = (byte) 0X3B;
//			log.info("开类型第4字段——————"+card_type[3]);
//			if(Frequency!=null){
//				byte[] bytes = ByteUtil.unlong2H2bytes(Integer.valueOf(Frequency));
//				card_type[4]=bytes[0];
//				log.info("开类型第5字段——————"+card_type[4]);
//				card_type[5]=bytes[1];
//				log.info("开类型第6字段——————"+card_type[5]);
//			}else {
//				card_type[4] = (byte) 0x00;
//				card_type[5] = (byte) 0x00;
//			}
//
//
//		}
//		if(CardSonType==3){
//			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
//			card_type[0] = b_CardType[0];
//			card_type[1] = (byte) 0X00;
//			card_type[2] = (byte) 0X00;
//			card_type[3] = (byte) 0X00;
//			card_type[4] = (byte) 0X00;
//			card_type[5] = (byte) 0X00;
//			card_type[6] = (byte) 0X00;
//			card_type[7] = (byte) 0X00;
//		}
//		if(CardSonType==4||CardSonType==5){
//			byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
//			card_type[0] = b_CardType[0];
//			card_type[1]=(byte) 0X00;
//			//Supply_CardNumber传入的挂失或者恢复卡号
//			byte[] bytes = ByteUtil.unlong2H4bytes(Integer.valueOf(Supply_CardNumber));
//			card_type[2]=bytes[0];
//			card_type[3]=bytes[1];
//			card_type[4]=bytes[2];
//			card_type[5]=bytes[3];
//			card_type[6]=(byte) 0X00;
//			card_type[7]=(byte) 0X00;
//
//		}
//		System.out.println("card_type：：" + ByteUtil.byte2Hex(card_type));
//		// ---------------------第一扇区第三块----------------
//		if (all_powers_list_map_lenth > 8) {
//			log.info("第九组权限");
//			String deviceid = all_powers_list_map.get(8).get("deviceid").toString();
//			String LiftRule = all_powers_list_map.get(8).get("LiftRule").toString();
//			if (LiftRule.equals("0")) {
//				log.info("整楼权限，直接赋值bit7置1");
//				//电梯组
//				byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//				third_powor_new[0] = (byte) (third_powor_new[0] | (1 << 7));
//				third_powor[0] = third_powor_new[0];
//				//楼层
//				byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//				third_powor[1] = (byte) 0x80;//所有楼层均有权限
//			} else {
//				if (CardSonType==0) {//标识自动登记楼层
//					//电梯组
//					log.info("deviceid值===" + deviceid);
//					byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					log.info("进入s==1:third_powor_new前===" + third_powor_new[0]);
//					third_powor_new[0] = (byte) (third_powor_new[0] | (1 << 7));
//					log.info("进入s==1:third_powor_new后===" + third_powor_new[0]);
//					third_powor[0] = third_powor_new[0];
//					//楼层
//					log.info("LiftRule权限值===" + LiftRule);
//					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
//					LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//					log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
//					third_powor[1] = LiftRule_new[0];
//				} else {//手动登记楼层
//					//电梯组
//					byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					third_powor_new[0] = (byte) (third_powor_new[0] | (0 << 7));
//					third_powor[0] = third_powor_new[0];
//					//楼层
//					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//					third_powor[1] = LiftRule_new[0];
//				}
//			}
//
//		}
//		System.out.println("third_powor：：" + ByteUtil.byte2Hex(third_powor));
//		// 获取总楼层权限
//		log.info("超出13组后的一次判断赋值模块");
//		if (all_powers_list_map_lenth > 13) {
//			powor_lenth[0] = (byte) 0x06;
//		} else if (all_powers_list_map_lenth > 37) {
//			powor_lenth[1] = (byte) 0x07;
//		} else if (all_powers_list_map_lenth > 61) {
//			powor_lenth[2] = (byte) 0x08;
//		} else if (all_powers_list_map_lenth > 85) {
//			powor_lenth[3] = (byte) 0x09;
//		}
//		System.out.println("powor_lenth：：" + ByteUtil.byte2Hex(powor_lenth));
//		// 权限8字节
//		int forth = 0;
//		for (int i = 9; i <= 12; i++) {
//			if (all_powers_list_map_lenth > i) {// 当前权限
//				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
//				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
//				if (LiftRule.equals("0")) {
//					log.info("整楼权限，直接赋值bit7置1");
//					//电梯组
//					byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					forth_powor_new[0] = (byte) (forth_powor_new[0] | (1 << 7));
//					forth_powor[forth * 2] = forth_powor_new[0];
//					//楼层
//					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					forth_powor[forth * 2 + 1] = (byte) 0x80;//所有楼层均有权限
//				} else {
//					if (CardSonType==0) {//标识自动登记楼层
//						//电梯组
//						log.info("deviceid值===" + deviceid);
//						byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						log.info("进入s==1:forth_powor_new前===" + forth_powor_new[0]);
//						forth_powor_new[0] = (byte) (forth_powor_new[0] | (1 << 7));
//						log.info("进入s==1:forth_powor_new后===" + forth_powor_new[0]);
//						forth_powor[forth * 2] = forth_powor_new[0];
//						//楼层
//						log.info("LiftRule权限值===" + LiftRule);
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
//						forth_powor[forth * 2 + 1] = LiftRule_new[0];
//					} else {//标识手动登记楼层
//						//电梯组
//						byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						forth_powor_new[0] = (byte) (forth_powor_new[0] | (0 << 7));
//						forth_powor[forth * 2] = forth_powor_new[0];
//						//楼层
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						forth_powor[forth * 2 + 1] = LiftRule_new[0];
//					}
//				}
//
//			} else {
//				log.info("开始执行自动补0");
//				forth_powor[forth * 2] = (byte) 0x00;
//				forth_powor[forth * 2 + 1] = (byte) 0x00;
//			}
//			forth = forth + 1;
//			log.info("本次循环结束");
//		}
//		System.out.println("forth_powor：：" + ByteUtil.byte2Hex(forth_powor));
//		for (byte b : forth_powor) {
//			System.out.println("B:" + b);
//		}
//		// 剩余权限
//		log.info("剩余权限开始-------");
//		int b_len = all_powers_list_map_lenth - 13;
//		log.info("b_len:"+b_len);
//		if(b_len<=0){
//			b_len=0;
//		}
//		byte[] surplus_powor = new byte[16];// 剩余权限,字节不固定
//		int surplus = 0;
//		for (int i = 13; i <= all_powers_list_map_lenth-1; i++) {
//			if (all_powers_list_map_lenth > i) {// 当前权限
//				String deviceid = all_powers_list_map.get(i).get("deviceid").toString();
//				String LiftRule = all_powers_list_map.get(i).get("LiftRule").toString();
//				log.info("deviceid:"+deviceid);
//				log.info("LiftRule:"+LiftRule);
//				if (LiftRule.equals("0")) {
//					log.info("整楼权限，直接赋值bit7置1");
//					//电梯组
//					byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//					surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (1 << 7));
//					surplus_powor[surplus * 2] = surplus_powor_new[0];
//					//楼层
//					byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//					surplus_powor[surplus * 2 + 1] = (byte) 0x80;//所有楼层均有权限
//				} else {
//					if (CardSonType==0) {//标识自动登记楼层
//						//电梯组
//						log.info("deviceid值===" + deviceid);
//						byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						log.info("进入s==1:surplus_powor_new前===" + surplus_powor_new[0]);
//						surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (1 << 7));
//						log.info("进入s==1:surplus_powor_new后===" + surplus_powor_new[0]);
//						surplus_powor[surplus * 2] = surplus_powor_new[0];
//						//楼层
//						log.info("LiftRule权限值===" + LiftRule);
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						log.info("进入s==1:LiftRule_new前===" + LiftRule_new[0]);
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
//						surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
//					} else {//手动登记楼层
//						//电梯组
//						byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
//						surplus_powor_new[0] = (byte) (surplus_powor_new[0] | (0 << 7));
//						surplus_powor[surplus * 2] = surplus_powor_new[0];
//						//楼层
//						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
//						LiftRule_new[0] = (byte) (LiftRule_new[0] | (0 << 6));//主门有效
//						surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
//					}
//				}
//
//			} else {
//				log.info("开始执行自动补0");
//				surplus_powor[surplus * 2] = (byte) 0x00;
//				surplus_powor[surplus * 2 + 1] = (byte) 0x00;
//			}
//			surplus = surplus + 1;
//			log.info("本次循环结束");
//		}
//		System.out.println("surplus_powor：：" + ByteUtil.byte2Hex(surplus_powor));
//		for (byte b : surplus_powor) {
//			System.out.println("B:" + b);
//		}
////		int length1 = surplus_powor.length;
////		if(length1<16){
////			surplus_powor = new byte[16];
////		}
//		System.out.println("surplus_powor变为16字节后内容=====" + Arrays.toString(surplus_powor) );
////		byte[] texts = ByteUtil.concatBytes(village_code, flag, card_number, flag_2, first_powor, second_powor,
////				card_type, third_powor, powor_lenth, flag_3, forth_powor, surplus_powor);
////		System.out.println("texts：：" + ByteUtil.byte2Hex(texts));
//		byte[] textm = ByteUtil.concatBytes(village_code,flag,card_number,flag_2,
//				first_powor,second_powor,card_type,third_powor,powor_lenth,flag_3,forth_powor);
//		System.out.println("textm：：" + ByteUtil.byte2Hex(textm));
//		byte[] makeUp=new byte[240-textm.length];
//		byte[] textss=ByteUtil.concatBytes(textm,makeUp);
//		byte[] lengths=ByteUtil.unlong2H2bytes(252);
//		byte[] value = ByteUtil.concatBytes(head, lengths, textss);
//		//获得亦或
//		byte[] crc = ByteUtil.bytesXorCrc(value);
//		log.info("亦或后字节-=-----------"+ByteUtil.byte2Hex(crc));
//		// 最终数据
//        byte[] bytes = ByteUtil.concatBytes(value, crc);
//        log.info("最终数据-=-----------"+ByteUtil.byte2Hex(bytes));
//
////		int length = texts.length;// 数据长度
////		byte l = (byte) length;
////		len_byte[0] = l;
////
////		byte[] value = ByteUtil.concatBytes(head, len_byte, texts);
////		// 获取异或
////		byte[] crc = ByteUtil.bytesXorCrc(value);
////		// 最终数据
////		byte[] bytes = ByteUtil.concatBytes(value, crc);
//		log.info(".........下发卡数据........");
//		int open_return = ScoketServer.sendOutCard(bytes);
//		if(open_return!=0){
//			return open_return;
//		}
//		Integer count_down_time = 5;
//		String Key = "Card_result";
//		map = CardService.Receive_Value(count_down_time, Key);
//		result = (Integer) map.get("result");
//		if (result != 0) {
//			map.put("result", result);
//			return map;
//		} else {
//			// 增加到记录表中
//			Integer BlackListID = null;
//			Integer State = 1;
//			log.info("发卡成功,修改卡数据");
//			Integer power = 1;
//			String PhoneNum = personService.queryPhoneByPersonID(PersonID);
//			String FullName = personService.queryFullNameByPersonID(PersonID);
//
//
//
//			//CardService.addCard()
//			Integer row = RecordsService.InsertRecords(CardID.toString(), DeviceID, BlackListID, State,
//					Integer.valueOf(OperatorID));
//
//			map.put("result", 0);
//		}
//
//		return map;
//	}


	/**
	 * 权限管理模块的卡片挂失
	 * @param sessionid
	 * @param CardID
	 * @return
	 */
	@RequestMapping(value = "ReportLoss", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object ReportLoss(
			@RequestParam String sessionid, @RequestParam Integer CardID){
		log.info("卡片挂失");
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		Integer integer = CardService.updateCardTypeByCardID(CardID);
	if(integer==1){
		result=0;
	}
	map.put("result",result);
	map.put("msg",msg);
	return map;
	}

	/**
	 * 挂失页面选择信息接口
	 * @param sessionid
	 * @return
	 */
	@RequestMapping(value = "reportLossInFormation", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object reportLossInFormation(@RequestParam String sessionid){
		log.info("卡片挂失页面");
		List<Map<String, Object>> maps = CardService.queryC();
		return maps;
	}
}
