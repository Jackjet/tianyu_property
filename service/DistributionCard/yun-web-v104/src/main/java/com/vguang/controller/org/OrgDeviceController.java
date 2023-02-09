package com.vguang.controller.org;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.vguang.eneity.*;
import com.vguang.service.*;
import com.vguang.service.impl.PersonService;
import com.vguang.utils.*;
import com.vguang.utils.encrypt.B64Util;
import com.vguang.utils.encrypt.HMACUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
//@CrossOrigin(origins = "http://www.dingdingkaimen.cn", maxAge = 3600)
@Controller
@RequestMapping("/orgman/device")
public class OrgDeviceController extends Thread {
	private Logger log = LoggerFactory.getLogger(OrgDeviceController.class);
	@Autowired
	private IDeviceService deviceservice;
	@Autowired
	private ICardService cardService;
	@Autowired
	private ISyncService sysService;
	@Autowired
	private IRuleService RuleService;
	@Autowired
	private IRecordsService RecordsService;
	@Autowired
	private ICardService CardService;
	@Autowired
	private JedisManager jedisManager;
	@Autowired
	private IPersonService personService;

	/**
	 * 乘梯卡发卡下拉列表
	 * @param sessionid
	 * @param DeviceID
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "queryDeviceByDeviceId", method = RequestMethod.POST)
	@ResponseBody
	public Object queryDeviceByDeviceId(@RequestParam String sessionid,
							   @RequestParam(required = false) Integer DeviceID
							  ) {
		Map<String, Object> map = new HashMap<>();
		Integer result = 0;
		String msg = "查询成功";
		Device device = deviceservice.queryDeviceByDeviceAndType(DeviceID);
		if(device==null){
			msg = "查询失败";
			result = -1;
		}

		map.put("result",result);
		map.put("msg",msg);
		map.put("device",device);
		return map;

	}
	/**
	 * 新增发卡器
	 */
	@CrossOrigin
	@RequestMapping(value = "addCardIssuer", method = RequestMethod.POST)
	@ResponseBody
	public Object addCardIssuer(@RequestParam String sessionid,
								@RequestParam String DeviceName,
								@RequestParam Integer DeviceType,//设备类型 1门禁  2梯控 3发卡器
								@RequestParam String DeviceIdentification,//设备标识前端生成
								@RequestParam String DeviceModel,//设备型号
								@RequestParam String DeviceWifiName,//WIFI名称
								@RequestParam String DeviceWifiPassWord,//WIFI密码
								@RequestParam String ServerIP,//服务器ip
								@RequestParam String DevicePort,//服务器端口
								@RequestParam Integer Ip_Mode,//IP 类型1动态  2静态
								@RequestParam String DeviceIP,//设备联网ip
								@RequestParam String Mask,//掩码
								@RequestParam String Gateway,//网关
								@RequestParam String Dns//DNS

								) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 添加设备表
		Integer DeviceState = 0;
		Device Device = new Device(null,DeviceName, DeviceModel, DeviceWifiName, DeviceWifiPassWord, DeviceIP, DevicePort,
				DeviceType, DeviceState, 2, Ip_Mode, Mask, Gateway, Dns, 2, ServerIP, "A", null, DeviceIdentification, null);
		Integer row = deviceservice.insertDevice(Device);
		if (row > 0) {
			Integer DeviceID = Device.getDeviceID();
			log.info("开始生成设备二维码");
			if (Ip_Mode == 0) {
				String Key = "ffffffffffff";
				String Key2 = "1234567887654321";
				String heand = "___VBAR_CONFIG_V1.1.0___{";
				String data = "devnum=" + DeviceID + "," + "taddr=" + "'" + ServerIP + "'," + "port='" + DevicePort + "'," + "auth_sector=" + 5 + "," + "sector_key='" + Key + "'," +
						"key_type=96," + "ssid='" + DeviceWifiName + "'," + "psk='" + DeviceWifiPassWord + "'," + "ip_mode=" + Ip_Mode;
				String segmentation = "}";
				String segmentations = "--";
				String s = heand + data + segmentation;
				byte[] bytes2 = HMACUtil.encrypt(s.getBytes(), Key2);
				String ss = B64Util.encode(bytes2);
				String content = s+segmentations + ss;
				map.put("content", content);
			} else {
				String Key = "ffffffffffff";
				String Key2 = "1234567887654321";
				String heand = "___VBAR_CONFIG_V1.1.0___{";
				String data = "devnum=" + DeviceID + "," + "taddr=" + "'" + ServerIP + "'," + "port='" + DevicePort + "'," + "auth_sector=" + 5 + "," + "sector_key='" + Key + "'," +
						"key_type=96," + "ssid='" + DeviceWifiName + "'," + "psk='" + DeviceWifiPassWord + "'," + "ip_mode=" + Ip_Mode + "," + "ip='" + DeviceIP + "'," + "mask='" + Mask + "'," + "gateway='" + Gateway + "'," + "dns='" + Dns + "'";
				String segmentation = "}";
				String segmentations = "--";
				String s = heand + data + segmentation;
				byte[] bytes2 = HMACUtil.encrypt(s.getBytes(), Key2);
				String ss = B64Util.encode(bytes2);
				String content = s +segmentations+ ss;
				map.put("content", content);

			}

		}
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 更新发卡器
	 */
	@CrossOrigin
	@RequestMapping(value = "updateCardIssuer", method = RequestMethod.POST)
	@ResponseBody
	public Object updateCardIssuer(@RequestParam String sessionid,
								@RequestParam String DeviceName,
								@RequestParam Integer DeviceType,//设备类型 1门禁  2梯控 3发卡器
								@RequestParam String DeviceIdentification,//设备标识前端生成
								@RequestParam String DeviceModel,//设备型号
								@RequestParam String DeviceWifiName,//WIFI名称
								@RequestParam String DeviceWifiPassWord,//WIFI密码
								@RequestParam String ServerIP,//服务器ip
								@RequestParam String DevicePort,//服务器端口
								@RequestParam Integer Ip_Mode,//IP 类型1动态  2静态
								@RequestParam String DeviceIP,//设备联网ip
								@RequestParam String Mask,//掩码
								@RequestParam String Gateway,//网关
								@RequestParam String Dns,//DNS
								    @RequestParam Integer DeviceID
	) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 添加设备表
		Integer DeviceState = 0;
		Device Device = new Device(DeviceID,DeviceName, DeviceModel, DeviceWifiName, DeviceWifiPassWord, DeviceIP, DevicePort,
				DeviceType, DeviceState, 2, Ip_Mode, Mask, Gateway, Dns, 2, ServerIP, "A", null, DeviceIdentification, null);
		Integer row = deviceservice.updateDevice(Device);

		if (row > 0) {
			//Integer DeviceID = Device.getDeviceID();
			log.info("开始生成设备二维码");
			if (Ip_Mode == 0) {
				String Key = "ffffffffffff";
				String Key2 = "1234567887654321";
				String heand = "___VBAR_CONFIG_V1.1.0___{";
				String data = "devnum=" + DeviceID + "," + "taddr=" + "'" + ServerIP + "'," + "port='" + DevicePort + "'," + "auth_sector=" + 5 + "," + "sector_key='" + Key + "'," +
						"key_type=96," + "ssid='" + DeviceWifiName + "'," + "psk='" + DeviceWifiPassWord + "'," + "ip_mode=" + Ip_Mode;
				String segmentation = "}";
				String segmentations = "--";
				String s = heand + data + segmentation;
				byte[] bytes2 = HMACUtil.encrypt(s.getBytes(), Key2);
				String ss = B64Util.encode(bytes2);
				String content = s +segmentations+ ss;
				map.put("content", content);
			} else {
				String Key = "ffffffffffff";
				String Key2 = "1234567887654321";
				String heand = "___VBAR_CONFIG_V1.1.0___{";
				String data = "devnum=" + DeviceID + "," + "taddr=" + "'" + ServerIP + "'," + "port='" + DevicePort + "'," + "auth_sector=" + 5 + "," + "sector_key='" + Key + "'," +
						"key_type=96," + "ssid='" + DeviceWifiName + "'," + "psk='" + DeviceWifiPassWord + "'," + "ip_mode=" + Ip_Mode + "," + "ip='" + DeviceIP + "'," + "mask='" + Mask + "'," + "gateway='" + Gateway + "'," + "dns='" + Dns + "'";
				String segmentation = "}";
				String segmentations = "--";
				String s = heand + data + segmentation;
				byte[] bytes2 = HMACUtil.encrypt(s.getBytes(), Key2);
				String ss = B64Util.encode(bytes2);
				String content = s +segmentations+ ss;
				map.put("content", content);

			}

		}
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}


	/**
	 * 增加设备
	 *
	 * @param sessionid
	 * @param DeviceName
	 * @param DeviceType
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "add_device", method = RequestMethod.POST)
	@ResponseBody
	public Object add_device(@RequestParam String sessionid,
							 @RequestParam String DeviceName,
							 @RequestParam String DeviceModel,
							 @RequestParam Integer DeviceType, // 1门禁 2梯控
							 @RequestParam Integer NetType, // 1以太网 2wifi
							 @RequestParam(required = false) Integer Ip_Model, // 1动态 2静态
							 @RequestParam(required = false) String DeviceWifiName,
							 @RequestParam(required = false) String DeviceWifiPassWord,
							 @RequestParam(required = false) String DeviceIP,
							 @RequestParam(required = false) String Mask,
							 @RequestParam(required = false) String Gateway,
							 @RequestParam(required = false) String Dns,
							 @RequestParam(required = false) String DevicePort, // 端口
							 @RequestParam(required = false) String ServerIP, // 服务器ip
							 @RequestParam(required = false) String SecretType,
							 @RequestParam String FloorDifference,
							 @RequestParam Integer Flag,
							 HttpServletRequest request,
							 HttpServletResponse response) {
		log.info("增加设备DeviceName===" + DeviceName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 添加设备表
		Integer DeviceState = 0;
		Device Device = new Device(null,DeviceName, DeviceModel, DeviceWifiName, DeviceWifiPassWord, DeviceIP, DevicePort,
				DeviceType, DeviceState, Flag, Ip_Model, Mask, Gateway, Dns, NetType, ServerIP, SecretType, null, null, FloorDifference);
		Integer row = deviceservice.insertDevice(Device);
		if (row > 0) {
			Integer DeviceID = Device.getDeviceID();
			log.info("开始生成设备二维码");
			String Url = deviceservice.loadQrcode(DeviceID, Ip_Model, DeviceWifiName, DeviceWifiPassWord, DeviceIP, Mask,
					Gateway, Dns, DevicePort, DeviceType, Flag, NetType, ServerIP, SecretType, null);
			if (Flag == 2) {
				log.info("当前设备为发卡器,将其他设备改为普通设备,保证唯一");
				Flag = 1;
				row = deviceservice.updateUn_DeviceFlagByID(Flag, DeviceID);
			}
			map.put("url", Url);
		}

		result = 0;

		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 查询设备
	 *
	 * @param sessionid
	 * @param DeviceName
	 * @param
	 * @param DeviceType
	 * @param currentpage
	 * @param pagesize
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "query_device", method = RequestMethod.POST)
	@ResponseBody
	public Object query_device(@RequestParam String sessionid,
							   @RequestParam(required = false) String DeviceName,
							   @RequestParam(required = false) Integer DeviceType,//2为梯控 3为发卡器
							   @RequestParam Integer currentpage,
							   @RequestParam Integer pagesize, HttpServletRequest request, HttpServletResponse response) {
		log.info("查询设备DeviceName===" + DeviceName);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("currentpage", (currentpage - 1) * pagesize);
		params.put("pagesize", pagesize);
		params.put("DeviceName", DeviceName);
		//params.put("DeviceModel", DeviceModel);
		params.put("DeviceType", DeviceType);
		// 获取设备条数
		Integer totle = deviceservice.queryCountDevice(params);
		List<Map<String, Object>> Devices = null;
		if (totle > 0) {
			Devices = deviceservice.queryDevice(params);
		}
		map.put("totle", totle);
		map.put("Devices", Devices);
		return map;
	}

	/**
	 * 根据设备功能查询
	 *
	 * @param sessionid
	 * @param Flag
	 * @param DeviceType
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "query_devicebytype", method = RequestMethod.POST)
	@ResponseBody
	public Object query_devicebytype(@RequestParam String sessionid,
									 @RequestParam(required = false) String Flag,
									 @RequestParam(required = false) String DeviceType,
									 HttpServletRequest request,
									 HttpServletResponse response) {
		log.info("查询发卡时权限设备DeviceType===" + DeviceType);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("DeviceType", DeviceType);
		params.put("Flag", Flag);
		List<Map<String, Object>> Devices = null;
		Devices = deviceservice.queryRuleDevice(params);
		map.put("Devices", Devices);
		return map;
	}

	/**
	 * 修改设备数据
	 *
	 * @param sessionid
	 * @param DeviceName
	 * @param DeviceType
	 * @param DeviceState
	 * @param DeviceWifiName
	 * @param DeviceWifiPassWord
	 * @param DeviceIP
	 * @param DevicePort
	 * @param Flag
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "update_device", method = RequestMethod.POST)
	@ResponseBody
	public Object update_device(@RequestParam String sessionid,
								@RequestParam String DeviceName,
								@RequestParam Integer DeviceType,
								@RequestParam Integer DeviceState,
								@RequestParam(required = false) String DeviceWifiName,
								@RequestParam(required = false) String DeviceWifiPassWord,
								@RequestParam(required = false) String DeviceIP,
								@RequestParam(required = false) String DevicePort,
								@RequestParam Integer NetType, // 1以太网 2wifi
								@RequestParam(required = false) Integer Ip_Model, // 1动态 2静态
								@RequestParam(required = false) String Mask,
								@RequestParam(required = false) String Gateway,
								@RequestParam(required = false) String Dns,
								@RequestParam(required = false) String ServerIP, // 服务器ip
								@RequestParam(required = false) String SecretType,
								@RequestParam String FloorDifference,
								@RequestParam Integer Flag,
								@RequestParam Integer DeviceID, HttpServletRequest request, HttpServletResponse response) {
		log.info("修改设备数据DeviceName===" + DeviceName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer row = deviceservice.updateDeviceByID(DeviceName, DeviceType, DeviceState, DeviceWifiName,
				DeviceWifiPassWord, DeviceIP, DevicePort, DeviceID, NetType, Ip_Model, Mask, Gateway, Dns, ServerIP,
				SecretType, Flag, FloorDifference);
		log.info("开始生成二维码");
		if (row > 0) {
			log.info("开始生成设备二维码");
			String Url = deviceservice.loadQrcode(DeviceID, Ip_Model, DeviceWifiName, DeviceWifiPassWord, DeviceIP, Mask,
					Gateway, Dns, DevicePort, DeviceType, Flag, NetType, ServerIP, SecretType, null);
			if (Flag == 2) {
				log.info("当前设备为发卡器,将其他设备改为普通设备,保证唯一");
				Flag = 1;
				row = deviceservice.updateUn_DeviceFlagByID(Flag, DeviceID);
			}
			map.put("url", Url);
		}
		result = 0;
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 删除设备
	 *
	 * @param sessionid
	 * @param DeviceID
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "delete_device", method = RequestMethod.POST)
	@ResponseBody
	public Object delete_device(@RequestParam String sessionid,
								@RequestParam Integer DeviceID,
								HttpServletRequest request, HttpServletResponse response) {
		log.info("删除设备DeviceID===" + DeviceID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 删除权限
		Integer row = RuleService.delRuleByDeviceID(DeviceID);
		// 删除通行
		//row = RecordsService.delAuthPassRecordByDeviceID(DeviceID);
		// 删除操作记录
		row = RecordsService.delRecordsByDeviceID(DeviceID);
		// 删除设备
		row = deviceservice.delDeviceByDeviceID(DeviceID);
		if(row==1){
			result=0;
			map.put("msg","删除成功");
			map.put("result",result);
			return map;
		}
		result=-1;
		map.put("result",result);
		map.put("msg","删除失败");
		return map;
	}

	/**
	 * 更改扇区无作用
	 * @param sessionid
	 * @param DeviceID
	 * @param sector
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "updatesector", method = RequestMethod.POST)
	@ResponseBody
	public Object updatesector(@RequestParam String sessionid,
							   @RequestParam Integer DeviceID,
							   @RequestParam Integer sector,
							   HttpServletRequest request,
							   HttpServletResponse response) {
		log.info("删除设备DeviceID===" + DeviceID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 判断当前设备是否被使用

		map.put("result", result);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 设备批量导入
	 *
	 * @param request
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@CrossOrigin
	@RequestMapping(value = "upload_device", method = RequestMethod.POST)
	@ResponseBody
	public Object cardupload(

			HttpServletRequest request) throws IllegalStateException, IOException {

		log.info("上传execl文件:filename");
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		String msg = "请上传正确的表格内容";
		Integer Ctotle = null;
		ArrayList list2 = new ArrayList<>();
		ArrayList list3 = new ArrayList<>();
		// 1、解析上传文件字段信息
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<?> items = null;
		try {
			items = upload.parseRequest(request);
			log.info("是否还有数据：{}", items.size());
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		Iterator<?> iter = items.iterator();

		// 2、遍历文件字段信息
		String fieldName = null, fileName = null, fileVer = null, replyTopic = null;
		Integer allflag = null;
		long fileLen = 0;
		byte[] payload = null;
		List<Integer> devlist = new ArrayList<>();
		Map<String, ? super Object> params = new HashMap<>();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// 如果是普通表单字段
				log.info("fieldname:{}, value:{}, name:{}", item.getFieldName(), item.getString(), item.getName());
				if ("allflag".equals(item.getFieldName())) {
					allflag = Integer.valueOf(item.getString());
				} else if ("fileversion".equals(item.getFieldName())) {
					fileVer = item.getString();
				} else {
					params.put(item.getFieldName(), item.getString());
				}
			} else {
				// 如果是文件字段:filedName=file fileName=vbar.tar.xz
				fieldName = item.getFieldName();
				fileName = item.getName();
				fileLen = item.getSize();
				log.info("fieldName:{}, len:{}, fileName:{}", fieldName, fileLen, fileName);
				try {
					// 两种方式获取文件流
					// payload = ByteUtil.toByteArray(item.getInputStream());
					payload = item.get();
					log.info("==读取成功==");
					String execlcard = sysService.queryValueByKey("execlcard");
					String fileName1 = new String(fileName.getBytes("GBK"), "UTF-8");
					String path = execlcard + fileName1;
					log.info("path==" + path);
					log.info("FilePath:{}", path);
					File newFile = new File(path);

					item.write(newFile);
					log.info("成功");
					// 判断是否为execl表格
					if (newFile.getName().endsWith("xlsx") || newFile.getName().endsWith("xls")) {
						log.info("是execl表格");

						// 上传成功，读取表格内容
						ArrayList<ArrayList<Object>> result1 = ExcelUtil.readExcel(newFile);
						log.info("返回的list++++++++++++++" + result1);
						// 判断跳水
						int size = result1.size();
						log.info("设备条数为----------" + size);
						if (size > 1000) {
							log.info("设备超过1000条");
							flag = -2;
							msg = "设备超过1000条,请分批次导入";
							map.put("result", flag);
							map.put("msg", msg);
							return map;
						}
						Map<String, Object> params1 = new HashMap<String, Object>();
						ArrayList OrdCardIDList = new ArrayList<>();
						ArrayList DeviceIdentificationList = new ArrayList<>();
						// 检出表格内容是否有错
						int count = 0;
						int FlagTotle = 0;
						String ordernumber = null;
						for (int i = 1; i < result1.size(); i++) {
							for (int j = 0; j < result1.get(i).size(); j = j + 20) {
								log.info("result1.get(i).size()=====" + result1.get(i).size());
								log.info("ppppppppppppp" + result1.get(i));

								if (result1.get(i).size() > 1) {

									log.info(i + "行 " + j + "列  " + result1.get(i).get(j).toString());
									ordernumber = result1.get(i).get(j).toString();
									// -----------------------设备名称---------------------------------------------------
									Object Obj_DeviceName = result1.get(i).get(j + 1);
									String DeviceName = "";
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceName)) {
										flag = -2;
										msg = "设备不可为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										DeviceName = Obj_DeviceName.toString();
									}
									log.info("DeviceName=======" + DeviceName);
									// -----------------------设备型号---------------------------------------------------
									Object Obj_DeviceModel = result1.get(i).get(j + 2);
									String DeviceModel = "";
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceModel)) {
										flag = -2;
										msg = "设备型号不可为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										DeviceModel = Obj_DeviceModel.toString();
									}
									log.info("DeviceModel=======" + DeviceModel);
									// -----------------------设备功能---------------------------------------------------
									Object Obj_Flag = result1.get(i).get(j + 3);
									Integer Flag = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_Flag)) {
										flag = -2;
										msg = "设备功能不可为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										Flag = Integer.valueOf((String) Obj_Flag);
										if (Flag == 1) {
											log.info("普通设备");
										} else if (Flag == 2) {
											log.info("发卡器设备");
											// 查询当前系统是否存在发卡器,保证唯一
											Integer totle = deviceservice.queryCountDeviceIDByFlag(Flag);
											if (totle > 0) {
												flag = -2;
												msg = "以存在发卡器,发卡器唯一 ,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}
											if (FlagTotle >= 1) {
												flag = -2;
												msg = "execl内容错误,存在多个发卡器 ,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}
											log.info("FlagTotle-------------->" + FlagTotle);
											FlagTotle = FlagTotle + 1;
										} else {
											flag = -2;
											msg = "设备功能字段不正确:1普通设备  2发卡器 ,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									}
									// -----------------------网络类型---------------------------------------------------
									Object Obj_NetType = result1.get(i).get(j + 4);
									log.info("Obj_NetType........" + Obj_NetType);
									Integer NetType = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_NetType)) {
										log.info("网络类型为空,判断是否为发卡器");
										if (Flag == 2) {
											flag = -2;
											msg = "发卡器不可无网络类型";
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										NetType = Integer.valueOf((String) Obj_NetType);
										if (NetType == 1) {
											log.info("以太网设备");
										} else if (NetType == 2) {
											log.info("Wifi设备");
										} else if (NetType == 3) {
											log.info("离线设备");
										} else {
											flag = -2;
											msg = "网络类型字段错误,1以太网 2wifi3离线,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									}
									// -----------------------Wifi名称---------------------------------------------------
									Object Obj_DeviceWifiName = result1.get(i).get(j + 5);
									String DeviceWifiName = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceWifiName)) {
										log.info("Wifi名称为空,判断网络类型是否为Wifi模式");
										if (NetType == 2) {
											flag = -2;
											msg = "Wifi模式下,Wifi名称不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										DeviceWifiName = Obj_DeviceWifiName.toString();
									}
									// -----------------------Wifi密码---------------------------------------------------
									Object Obj_DeviceWifiPassWord = result1.get(i).get(j + 6);
									String DeviceWifiPassWord = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceWifiPassWord)) {
										log.info("Wifi名称为空,判断网络类型是否为Wifi模式");
										if (NetType == 2) {
											flag = -2;
											msg = "Wifi模式下,Wifi密码不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										DeviceWifiPassWord = Obj_DeviceWifiPassWord.toString();
									}
									// -----------------------IP类型---------------------------------------------------
									Object Obj_Ip_Model = result1.get(i).get(j + 7);
									Integer Ip_Model = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_Ip_Model)) {
										log.info("IP类型为空,判断是否为以太网设备");
										if (NetType == 1) {
											flag = -2;
											msg = "以太网模式下,IP类型不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										Ip_Model = Integer.valueOf((String) Obj_Ip_Model);
										if (Ip_Model == 1) {
											log.info("动态IP");
										} else if (Ip_Model == 2) {
											log.info("静态IP");
										} else {
											flag = -2;
											msg = "IP类型数据错误 1动态 2静态,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									}
									// -----------------------IP,掩码,网关,Dns---------------------------------------------------
									Object Obj_DeviceIP = result1.get(i).get(j + 8);
									Object Obj_Mask = result1.get(i).get(j + 9);
									Object Obj_Gateway = result1.get(i).get(j + 10);
									Object Obj_Dns = result1.get(i).get(j + 11);
									String DeviceIP = null;
									String Mask = null;
									String Gateway = null;
									String Dns = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceIP)
											|| StringUtil.isNullAndEmpty_Obj(Obj_Mask)
											|| StringUtil.isNullAndEmpty_Obj(Obj_Gateway)
											|| StringUtil.isNullAndEmpty_Obj(Obj_Dns)) {
										log.info("IP,掩码,网关,Dns其中一项或多项为空 ");
										log.info("IP类型为空,判断是否为以太网设备");
										if (NetType == 1) {
											log.info("判断动静态IP,静态IP不可为空");
											if (Ip_Model == 2) {
												log.info("静态IP");
												flag = -2;
												msg = "当网络类型为以太网,且为静态ip模式下,'IP,掩码,网关,Dns'缺一不可,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}

										}
									} else {
										DeviceIP = Obj_DeviceIP.toString();
										Mask = Obj_Mask.toString();
										Gateway = Obj_Gateway.toString();
										Dns = Obj_Dns.toString();
									}
									// -----------------------端口号---------------------------------------------------
									Object Obj_DevicePort = result1.get(i).get(j + 12);
									String DevicePort = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DevicePort)) {
										log.info("端口号为空,判断是否为发卡器");
										if (Flag == 2) {
											log.info("发卡器设备");
											flag = -2;
											msg = "当设备为发卡器时,端口号不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										DevicePort = Obj_DevicePort.toString();
									}
									// -----------------------设备类型---------------------------------------------------
									Object Obj_DeviceType = result1.get(i).get(j + 13);
									log.info("Obj_DeviceType=======>" + Obj_DeviceType);
									Integer DeviceType = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceType)) {
										log.info("设备类型为空,判断是否为普通设备");
										if (Flag == 1) {
											log.info("普通设备设备");
											flag = -2;
											msg = "当设备为普通设备时,设备类型不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}

									} else {
										DeviceType = Integer.valueOf((String) Obj_DeviceType);
										log.info("DeviceType============" + DeviceType);
										if (DeviceType == 1) {
											log.info("门禁设备");
										} else if (DeviceType == 2) {
											log.info("梯控设备");
										} else {
											log.info("设备类型错误");
											flag = -2;
											msg = "设备类型字段错误 1门禁设备  2梯控设备,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									}
									// -----------------------设备状态---------------------------------------------------
									Object Obj_DeviceState = result1.get(i).get(j + 14);
									Integer DeviceState = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceState)) {
										log.info("设备状态为空");
										flag = -2;
										msg = "设备状态不可为空,错误序号为:" + ordernumber;
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										DeviceState = Integer.valueOf((String) Obj_DeviceState);
										log.info("DeviceState--------->" + DeviceState);
										if (DeviceState == 0) {
											log.info("正常");
										} else if (DeviceState == 1) {
											log.info("禁用");
										} else {
											log.info("设备状态字段错误DeviceState:" + DeviceState);
											flag = -2;
											msg = "设备状态字段错误 0正常  1禁用,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									}
									// -----------------------服务器ip--------------------------------------------------
									Object Obj_ServerIP = result1.get(i).get(j + 15);
									String ServerIP = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_ServerIP)) {
										log.info("服务器IP为空,判断是否为发卡器");
										if (Flag == 2) {
											log.info("发卡器设备");
											flag = -2;
											msg = "当设备为发卡器时,服务器IP不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										ServerIP = Obj_ServerIP.toString();
									}
									// -----------------------秘钥类型--------------------------------------------------
									Object Obj_SecretType = result1.get(i).get(j + 16);
									String SecretType = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_SecretType)) {
										log.info("秘钥为空,判断是否为普通设备");
										if (Flag == 1) {
											log.info("普通设备设备");
											flag = -2;
											msg = "当设备为普通设备时,秘钥类型不可为空,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
									} else {
										SecretType = Obj_SecretType.toString();
										if (SecretType.equals("A")) {
											log.info("A秘钥");
										} else if (SecretType.equals("B")) {
											log.info("B秘钥");
										} else {
											log.info("秘钥类型字段错误,SecretType:" + SecretType);
											flag = -2;
											msg = "秘钥类型字段错误 ,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;

										}
									}
									// -----------------------原设备ID--------------------------------------------------
									Object Obj_OldDeviceID = result1.get(i).get(j + 17);
									String OldDeviceID = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_OldDeviceID)) {
										log.info("原设备ID为空");
										flag = -2;
										msg = "原设备ID不可为空,错误序号为:" + ordernumber;
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										OldDeviceID = Obj_OldDeviceID.toString();
										log.info("判断表格是否重复");
										for (int k = 0; k < OrdCardIDList.size(); k++) {
											String ood = (String) OrdCardIDList.get(k);
											if (ood.equals(OldDeviceID)) {
												log.info("EXECL表中的设备原系统id重复");
												flag = -2;
												msg = "EXECL表中的设备原系统id重复,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}
										}
										Integer deviceid = deviceservice.queryDeviceIDByOrgDeviceID(OldDeviceID);
										if (null != deviceid) {
											log.info("设备原系统id重复");
											flag = -2;
											msg = "设备原系统id重复,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
										OrdCardIDList.add(OldDeviceID);
									}
									// -----------------------梯控偏值数--------------------------------------------------
									Object Obj_FloorDifference = result1.get(i).get(j + 18);
									String FloorDifference = null;
									log.info("判断是否为梯控设备");
									if (DeviceType == 2) {
										log.info("梯控设备");
										if (StringUtil.isNullAndEmpty_Obj(Obj_FloorDifference)) {
											log.info("梯控设备,电梯偏值不可为空,无请填0");
											flag = -2;
											msg = "梯控设备,电梯偏值不可为空,无请填0,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										} else {
											FloorDifference = Obj_FloorDifference.toString();
											// 判断是否为整数
											boolean boo = StringUtil.isInteger(FloorDifference);
											if (boo == false) {
												log.info("梯控设备,电梯偏值必须为整数");
												flag = -2;
												msg = "梯控设备,电梯偏值必须为整数,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}
											log.info("FloorDifference-----" + FloorDifference);

										}
									}
									// -----------------------设备唯一标识--------------------------------------------------
									Object Obj_DeviceIdentification = result1.get(i).get(j + 19);
									String DeviceIdentification = null;
									if (StringUtil.isNullAndEmpty_Obj(Obj_DeviceIdentification)) {
										log.info("设备唯一标识为空");
										flag = -2;
										msg = "设备唯一标识不可为空,错误序号为:" + ordernumber;
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									} else {
										DeviceIdentification = Obj_DeviceIdentification.toString();
										log.info("判断身份ID长度,最长为15字节");
										int len = DeviceIdentification.length();
										if (len > 15) {
											flag = -2;
											msg = "设备唯一标识超长,最长为15,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
										log.info("判断表格是否重复");
										for (int k = 0; k < DeviceIdentificationList.size(); k++) {
											String dif = (String) DeviceIdentificationList.get(k);
											if (dif.equals(OldDeviceID)) {
												log.info("EXECL表中的设备唯一标识重复");
												flag = -2;
												msg = "EXECL表中的设备唯一标识,错误序号为:" + ordernumber;
												map.put("result", flag);
												map.put("msg", msg);
												return map;
											}
										}
										log.info("判断设备唯一标识是否存在");
										Integer deviceid = deviceservice
												.queryDeviceIDByDeviceIdentification(DeviceIdentification);
										if (null != deviceid) {
											log.info("设备唯一标识不唯一");
											flag = -2;
											msg = "设备唯一标识不可重复,错误序号为:" + ordernumber;
											map.put("result", flag);
											map.put("msg", msg);
											return map;
										}
										DeviceIdentificationList.add(DeviceIdentification);
									}

								}
							}

						}

						// 判断count是否大于0，如果大于就不走添加方法
						if (count > 0) {
							log.info("有错误数据count");

						} else {
							// 开始添加
							for (int i = 1; i < result1.size(); i++) {
								for (int j = 0; j < result1.get(i).size(); j = j + 20) {

									log.info("result1.get(i).size()=====" + result1.get(i).size());
									log.info("ppppppppppppp" + result1.get(i));

									if (result1.get(i).size() > 1) {

										log.info(i + "行 " + j + "列  " + result1.get(i).get(j).toString());
										ordernumber = result1.get(i).get(j).toString();
										// -----------------------设备名称---------------------------------------------------
										Object Obj_DeviceName = result1.get(i).get(j + 1);
										String DeviceName = StringUtil.objToString(Obj_DeviceName);
										log.info("DeviceName=======" + DeviceName);
										// -----------------------设备型号---------------------------------------------------
										Object Obj_DeviceModel = result1.get(i).get(j + 2);
										String DeviceModel = StringUtil.objToString(Obj_DeviceModel);
										log.info("DeviceModel=======" + DeviceModel);
										// -----------------------设备功能---------------------------------------------------
										Object Obj_Flag = result1.get(i).get(j + 3);
										Integer Flag = null;
										Flag = StringUtil.ObjToInteger(Obj_Flag);
										// -----------------------网络类型---------------------------------------------------
										Object Obj_NetType = result1.get(i).get(j + 4);
										log.info("Obj_NetType........" + Obj_NetType);
										Integer NetType = StringUtil.ObjToInteger(Obj_NetType);
										// -----------------------Wifi名称---------------------------------------------------
										Object Obj_DeviceWifiName = result1.get(i).get(j + 5);
										String DeviceWifiName = StringUtil.objToString(Obj_DeviceWifiName);
										// -----------------------Wifi密码---------------------------------------------------
										Object Obj_DeviceWifiPassWord = result1.get(i).get(j + 6);
										String DeviceWifiPassWord = StringUtil.objToString(Obj_DeviceWifiPassWord);
										// -----------------------IP类型---------------------------------------------------
										Object Obj_Ip_Model = result1.get(i).get(j + 7);
										Integer Ip_Model = StringUtil.ObjToInteger(Obj_Ip_Model);
										// -----------------------IP,掩码,网关,Dns---------------------------------------------------
										Object Obj_DeviceIP = result1.get(i).get(j + 8);
										Object Obj_Mask = result1.get(i).get(j + 9);
										Object Obj_Gateway = result1.get(i).get(j + 10);
										Object Obj_Dns = result1.get(i).get(j + 11);
										String DeviceIP = StringUtil.objToString(Obj_DeviceIP);
										String Mask = StringUtil.objToString(Obj_Mask);
										String Gateway = StringUtil.objToString(Obj_Gateway);
										String Dns = StringUtil.objToString(Obj_Dns);
										// -----------------------端口号---------------------------------------------------
										Object Obj_DevicePort = result1.get(i).get(j + 12);
										String DevicePort = StringUtil.objToString(Obj_DevicePort);
										// -----------------------设备类型---------------------------------------------------
										Object Obj_DeviceType = result1.get(i).get(j + 13);
										Integer DeviceType = StringUtil.ObjToInteger(Obj_DeviceType);
										// -----------------------设备状态---------------------------------------------------
										Object Obj_DeviceState = result1.get(i).get(j + 14);
										Integer DeviceState = StringUtil.ObjToInteger(Obj_DeviceState);
										// -----------------------服务器ip--------------------------------------------------
										Object Obj_ServerIP = result1.get(i).get(j + 15);
										String ServerIP = StringUtil.objToString(Obj_ServerIP);
										// -----------------------秘钥类型--------------------------------------------------
										Object Obj_SecretType = result1.get(i).get(j + 16);
										String SecretType = StringUtil.objToString(Obj_SecretType);
										// -----------------------原设备ID--------------------------------------------------
										Object Obj_OldDeviceID = result1.get(i).get(j + 17);
										String OldDeviceID = StringUtil.objToString(Obj_OldDeviceID);
										// -----------------------电梯偏值数--------------------------------------------------
										Object Obj_FloorDifference = result1.get(i).get(j + 18);
										String FloorDifference = Obj_FloorDifference.toString();
										if (DeviceType != 2) {
											FloorDifference = null;
										}
										// -----------------------设备唯一标识--------------------------------------------------
										Object Obj_DeviceIdentification = result1.get(i).get(j + 19);
										String DeviceIdentification = StringUtil.objToString(Obj_DeviceIdentification);
										log.info("走一次添加方法");
										Device Device = new Device(null,DeviceName, DeviceModel, DeviceWifiName,
												DeviceWifiPassWord, DeviceIP, DevicePort, DeviceType, DeviceState, Flag,
												Ip_Model, Mask, Gateway, Dns, NetType, ServerIP, SecretType,
												OldDeviceID, DeviceIdentification, FloorDifference);
										Integer row = deviceservice.insertDevice(Device);
									}

								}
							}
						}
					} else {
						log.info("请上传execl表格，");
						msg = "请上传execl表格";
						flag = -3;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		flag = 0;
		map.put("result", flag);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 批量配置码
	 *
	 * @param sessionid
	 * @param DeviceIds
	 * @param Email
	 * @param request
	 * @param response
	 * @return已测试
	 */
	@CrossOrigin
	@RequestMapping(value = "deviceconfigcode", method = RequestMethod.POST)
	@ResponseBody
	public Object deviceconfigcode(@RequestParam String sessionid,
								   @RequestParam String DeviceIds,
								   @RequestParam(required = false) String Email
			, HttpServletRequest request,
								   HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		List<HashMap<String, Object>> pic = new ArrayList<>();
		List<String> DeviceIds_list = Arrays.asList(DeviceIds.split(","));
		for (int i = 0; i < DeviceIds_list.size(); i++) {
			HashMap deviceMap = new HashMap<>();
			String Str_deviceID = DeviceIds_list.get(i);
			Integer deviceID = Integer.valueOf(Str_deviceID);
			log.info("deviceID------->" + deviceID);
			// 查询数据
			HashMap<String, Object> Device = deviceservice.querydeviceByID(deviceID);
			Integer Ip_Model = StringUtil.ObjToInteger(Device.get("Ip_Mode"));
			System.out.println(Ip_Model);
			String DeviceWifiName = StringUtil.objToString(Device.get("DeviceWifiName"));
			System.out.println(DeviceWifiName);
			String DeviceWifiPassWord = StringUtil.objToString(Device.get("DeviceWifiPassWord"));
			String DeviceIP = StringUtil.objToString(Device.get("DeviceIP"));
			String Mask = StringUtil.objToString(Device.get("Mask"));
			String Gateway = StringUtil.objToString(Device.get("Gateway"));
			String Dns = StringUtil.objToString(Device.get("Dns"));
			String DevicePort = StringUtil.objToString(Device.get("DevicePort"));
			Integer DeviceType = StringUtil.ObjToInteger(Device.get("DeviceType"));
			Integer Flag = StringUtil.ObjToInteger(Device.get("Flag"));
			Integer NetType = StringUtil.ObjToInteger(Device.get("NetType"));
			String ServerIP = StringUtil.objToString(Device.get("ServerIP"));
			String SecretType = StringUtil.objToString(Device.get("SecretType"));
			String DeviceIdentification = StringUtil.objToString(Device.get("DeviceIdentification"));
			String Url = deviceservice.loadQrcode(deviceID, Ip_Model, DeviceWifiName, DeviceWifiPassWord, DeviceIP,
					Mask, Gateway, Dns, DevicePort, DeviceType, Flag, NetType, ServerIP, SecretType, DeviceIdentification);

			String DeviceName = Device.get("DeviceName").toString();
			deviceMap.put("DeviceName", DeviceName);
			deviceMap.put("Url", Url);
			pic.add(i, deviceMap);
		}
		map.put("DeviceUrl", pic);
		return map;
	}



	/**
	 * 导出模板
	 */
	@PostMapping("/exportModel")
	public Object exportModel(HttpServletResponse response,
	@RequestParam String sessionid) throws Exception {
		List<DeviceExceBase> data = new ArrayList<>();
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
		String fileName = URLEncoder.encode("设备模板", "UTF-8").replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), DeviceExceBase.class).sheet("用户").doWrite(data);
		return null;
	}

	/**
	 * 导入单条数据
	 * @param sessionid
	 * @param DeviceName
	 * @param DeviceIdentification
	 * @param DeviceType
	 * @param DeviceModel
	 * @param DeviceInstalTimel
	 * @param request
	 * @param response
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "addDevice", method = RequestMethod.POST)
	@ResponseBody
	public Object addDevice(@RequestParam String sessionid,
							 @RequestParam String DeviceName,
							 @RequestParam String DeviceIdentification,
							 @RequestParam Integer DeviceType,
							 @RequestParam String DeviceModel,
							 @RequestParam String DeviceInstalTimel,
							 HttpServletRequest request,
							 HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg="添加失败";
		Integer integer = deviceservice.insertDeviceE(DeviceName, DeviceIdentification, DeviceType, DeviceModel, DeviceInstalTimel);
			if(integer==1){
				result=0;
				msg="添加成功";
				map.put("result",result);
				map.put("msg",msg);
			}
			map.put("result",result);
			map.put("msg",msg);
					
		return map;
	}

	/**
	 * 前端循环获取刷卡的卡信息(刷完卡后5秒钟)
	 * @param sessionid
	 * @param DeviceID
	 * @param request
	 * @param response
	 * @return完成已测试接口文档已更新
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 */
	@RequestMapping(value = "queryreturncard", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object queryreturncard(
			@RequestParam String sessionid,
			@RequestParam Integer DeviceID,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
		//log.info("前端循环获取刷卡的卡信息DeviceID===" + DeviceID);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String Key = DeviceID.toString();
		String bb = jedisManager.getValueByStr(0, Key);
		jedisManager.delValueByStr(0, Key);
		log.info("bb==================" + bb);
		if (bb == null) {
			result = -3;
			map.put("Cards", bb);
			map.put("result", result);
		} else {
			String card_number = bb.substring(10, 18);
			System.out.println("card_number========" + card_number);
			String years=bb.substring(30,32);
			int i = ByteUtil.Str16toint10(years);
			String f="20";
			String year =f+i;
			String months=bb.substring(32,34);
			byte[] bytes = ByteUtil.hex2Byte(months);
			bytes[0]=(byte) (bytes[0] & ~(1 << 4));
			bytes[0]=(byte) (bytes[0] & ~(1 << 7));
			int monthss = ByteUtil.Str16toint10(ByteUtil.byte2Hex(bytes));
			String month = String.valueOf(monthss);
			String days=bb.substring(94,96);
			System.out.println(days);
			byte[] bytes1 = ByteUtil.hex2Byte(days);
			System.out.println("前"+Arrays.toString(bytes1));
			bytes1[0]=(byte) (bytes1[0] & ~(1 << 7));
			System.out.println("后"+Arrays.toString(bytes1));
			int dayss = ByteUtil.Str16toint10(ByteUtil.byte2Hex(bytes1));
			System.out.println("int形的日"+dayss);
			String day = String.valueOf(dayss);
			String time=year+"-"+month+"-"+day;
			System.out.println("权限时间----"+time);
			String CardType = bb.substring(67, 68);
			String CardSonType = bb.substring(68, 70);
			System.out.println("卡类型"+CardType);
			System.out.println("子卡类型"+CardSonType);
		if(CardSonType.equals("02")){
			map.put("cardNumber",card_number);
			map.put("time",time);
			map.put("CardType",CardType);
			map.put("CardSonType",1);
			return map;
		}if(CardSonType.equals("04")){
				map.put("cardNumber",card_number);
				map.put("time",time);
				map.put("CardType",CardType);
				map.put("CardSonType",2);
				return map;
		}
			map.put("cardNumber",card_number);
			map.put("time",time);
			map.put("CardType",CardType);
			map.put("CardSonType",0);

		}
		return map;

	}


	/**
	 * 发卡乘梯卡
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "rl_distributioncards", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object rl_distributioncards(@RequestBody HashMap <String , Object> map1) throws IOException, InterruptedException {
		Integer DeviceID = (Integer) map1.get("DeviceID");
		Integer CardID = (Integer)map1.get("CardID");
		String Supply_CardNumber = (String) map1.get("Supply_CardNumber");
		String ReportLostCardNumber = (String) map1.get("ReportLostCardNumber");
		Integer CardType = (Integer) map1.get("CardType");
		Integer CardSonType = (Integer) map1.get("CardSonType");
		String StartTime = (String) map1.get("StartTime");
		String FullName = (String) map1.get("FullName");
		String PhoneNum = (String) map1.get("PhoneNum");
		String EndTime = (String) map1.get("EndTime");
		String CardValidPeriod = (String) map1.get("CardValidPeriod");
		String Frequency = (String) map1.get("Frequency");
		List<HashMap<String,Object>> Authority = (List<HashMap<String, Object>>) map1.get("Authority");
		String OperatorID = (String) map1.get("OperatorID");
		Integer PersonID = (Integer) map1.get("PersonID");
		if(CardValidPeriod==null){
			CardValidPeriod="2099-12-31";
		}
		Integer result = -1;
		String msg = "";
		Map<String, Object> map = new HashMap<>();
		ScoketServer ScoketServer = new ScoketServer();
		if(CardType==1){
			log.info("进入卡类型为1的代码--------------------------------------------------------------------------");
			List<Map<String, Object>>s = RuleService.queryRulesIDByPersonID(PersonID);
			String Powers="";
			for (Map<String, Object> stringObjectMap : s) {
				Integer ruleID = (Integer) stringObjectMap.get("RuleID");
				Powers=Powers+ruleID+",";
			}
			Powers = Powers.substring(0,Powers.length() - 1);
			log.info("该人员的权限为===="+Powers);
			List<String> powers_rule_list = Arrays.asList(Powers.split(","));// 内容为ruleid,梯控使用
			log.info("powers_rule_list:"+powers_rule_list);
			// 根据卡号查询是否存在于系统中
			Integer CardState = 0;
			Integer Supply_CardID = CardService.queryCardIDbyNumber(Supply_CardNumber, CardState);
			log.info("当前卡片数据库有没有 1为有  0为没有------"+Supply_CardID);
			// 获取所有权限的总条数----以层为单位 1号楼1层 1号楼2层
			int totle =0;
			log.info("powers_rule_list.size():"+powers_rule_list.size());
			List<HashMap<String, Object>> all_powers_list_map = new ArrayList<>();
			for (int i = 0; i < powers_rule_list.size(); i++) {
				log.info("totle:"+totle);
				String powers_ruleid = powers_rule_list.get(i);
				log.info("powers_ruleid:"+powers_ruleid);
				String LiftRules = RuleService.queryLiftRuleByRuleid(powers_ruleid);
				log.info("LiftRules:"+LiftRules);
				String deviceid = RuleService.queryDeviceidByRuleid(powers_ruleid);
				log.info("deviceid:"+deviceid);
				if (LiftRules.equals("0")) {
					log.info("整楼权限，直接赋值0即可");
					HashMap<String, Object> power = new HashMap<>();
					power.put("LiftRule", 0);
					power.put("deviceid", deviceid);
					all_powers_list_map.add(totle, power);
					totle = totle+1;
				} else {
					log.info("非通用权限");
					List<String> LiftRule_list = Arrays.asList(LiftRules.split(","));
					for (int j = 0; j < LiftRule_list.size(); j++) {
						String LiftRule = LiftRule_list.get(j);
						Integer power_len = Integer.valueOf(LiftRule);
						//log.info("计算偏值");// 因协议中的楼层00为一层
						// 获取设备偏执
						Integer FloorDifference = deviceservice.queryFloorDifferenceByDeviceID(Integer.valueOf(deviceid));
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
						all_powers_list_map.add(i, power);
						totle = totle+1;
					}
				}
			}

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
			byte[] flag_3 =  new byte[2]; // 2字节标识
			String substring = CardValidPeriod.substring(8, 10);
			//StringBuilder reverse = new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse();
			String dd=new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse().toString();
			byte[] bytes9 = ByteUtils.long2Hbytes1byte(ByteUtils.binary2To10(dd));
			bytes9[0]=(byte) (bytes9[0] | (1 << 0));
//			flag_3 = ByteUtil.unlong2H2bytes(Integer.valueOf(CardValidPeriod.substring(8, 10)));
			flag_3[0]=bytes9[0];
			flag_3[1] = (byte) 0X01;
			byte[] forth_powor = new byte[8];// 8字节的扇区第二块权限
			// 以下扇区的长度根据权限长度定,无需列举
			// ---------------------第一扇区第一块----------------
			village_code = ByteUtil.Str16toBytes("010101");//小区码暂时写死
			log.info("小区码3位=-=="+village_code[0]+"---"+village_code[1]+"---"+village_code[2]);
			byte[] bytes4 = ByteUtils.hex2Byte(Supply_CardNumber);
			card_number[0]=bytes4[bytes4.length-1];
			card_number[1]=bytes4[0];
			//log.info("卡号字段=-=="+card_number[0]+"---"+card_number[1]+"---"+card_number[2]+"---"+card_number[3]);
			byte[] bytes1 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(2, 4)));
			flag_2[0]= bytes1[0];
			//log.info("有效年份字段=-=="+flag_3[0]);
			byte[] bytes2 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(5, 7)));
			bytes2[0]=(byte) (bytes2[0] | (1 << 4));
			bytes2[0]=(byte) (bytes2[0] & ~ (1 << 5));
			bytes2[0]=(byte) (bytes2[0] & ~ (1 << 6));
			bytes2[0]=(byte) (bytes2[0] | (1 << 7));
			flag_2[1]=bytes2[0];
			//log.info("有效月份字段=-=="+flag_3[1]);
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							first_powor[first * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							first_powor_new[0] = (byte) (first_powor_new[0] & ~ (1 << 7));
							first_powor[first * 2] = first_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							second_powor[second * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							second_powor_new[0] = (byte) (second_powor_new[0] & ~ (1 << 7));
							second_powor[second * 2] = second_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
					b_CardSonType[0]=(byte) (b_CardSonType[0] & ~(1 << 0));
					b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
					card_type[1] = b_CardSonType[0];
					log.info("开类型第2字段——————"+card_type[1]);
				}else if(CardSonType==2){
					byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
					b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
					b_CardSonType[0] = (byte) (b_CardSonType[0] & ~(1 << 1));
					card_type[1] = b_CardSonType[0];
				}else {
					card_type[1]= (byte) 0X00;
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
					b_CardSonType[0]=(byte) (b_CardSonType[0] & ~(1 << 0));
					b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
					card_type[1] = b_CardSonType[0];
					log.info("开类型第2字段——————"+card_type[1]);
				}
				if(CardSonType==2) {
					byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
					b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
					b_CardSonType[0] = (byte) (b_CardSonType[0] & ~(1 << 1));
					card_type[1] = b_CardSonType[0];
				}else {
					card_type[1]= (byte) 0X00;
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
			if(CardType==4||CardType==5){
				byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
				card_type[0] = b_CardType[0];
				card_type[1]=(byte) 0X00;
				card_type[2]=(byte) 0X17;
				card_type[3]=(byte) 0X3B;
				card_type[4]=(byte) 0X00;
				card_type[5]=(byte) 0X00;
				card_type[6]=(byte) 0X00;
				card_type[7]=(byte) 0X00;

			}
			if(CardType==3){
				byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
				card_type[0] = b_CardType[0];
				card_type[1]=(byte) 0X00;
				card_type[2]=(byte) 0X17;
				card_type[3]=(byte) 0X3B;
				card_type[4]=(byte) 0X00;
				card_type[5]=(byte) 0X00;
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
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						third_powor[1] = LiftRule_new[0];
					} else {//手动登记楼层
						//电梯组
						byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						third_powor_new[0] = (byte) (third_powor_new[0] & ~ (1 << 7));
						third_powor[0] = third_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							forth_powor[forth * 2 + 1] = LiftRule_new[0];
						} else {//标识手动登记楼层
							//电梯组
							byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							forth_powor_new[0] = (byte) (forth_powor_new[0] & ~ (1 << 7));
							forth_powor[forth * 2] = forth_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
			byte[] surplus_powor = new byte[b_len*2];// 剩余权限,字节不固定
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							surplus_powor_new[0] = (byte) (surplus_powor_new[0] & ~ (1 << 7));
							surplus_powor[surplus * 2] = surplus_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
					first_powor,second_powor,card_type,third_powor,powor_lenth,flag_3,forth_powor,surplus_powor);
			System.out.println("textm：：" + ByteUtil.byte2Hex(textm));
			byte[] makeUp=new byte[240-textm.length];
			byte[] textss=ByteUtil.concatBytes(textm,makeUp);

			//下发设备
			log.info("实际发送指令为="+ByteUtil.byte2Hex(textss));
			boolean report = ScoketServerTKUtils.sendCommand(DeviceID.toString(), "ISSUED", ByteUtil.byte2Hex(textss));
			log.info("下发是否成功+++"+report);
			String key2="sentSuccessfully";
			String bb = jedisManager.getValueByStr(0, key2);
			log.info("判断发卡是否成功，发卡器返回的数据为="+bb);

			if(report!=true){
				msg="下发失败";
				result =-1;
			}else {

				log.info("权限下发成功，进行添加卡信息");
				Integer cardState = 0;
				Integer CardIDs = CardService.queryCardIDbyNumber(Supply_CardNumber, cardState);
				String CarId = "";
				CarId = CarId + CardIDs;
				String phone = personService.queryPhoneByPersonID(PersonID);
				String name = personService.queryFullNameByPersonID(PersonID);
				String email = personService.queryEmailByPersonID(PersonID);
//			Card card = new Card(Supply_CardNumber, CardType, CardSonType, 0, null,CardValidPeriod, name, phone,
//					null, null, null, null,null,PersonID);
				if (CardIDs == null) {
					log.info("发卡成功，开始添加卡信息至数据库");
					Integer row = CardService.addCards(PersonID, Supply_CardNumber, CardType, CardSonType, 0, null, CardValidPeriod, name, phone, email, null);
					if (row > 0) {
						result = 0;
						msg = "权限下发成功，添加卡信息至数据库成功";
					} else {
						result = -1;
						msg = "权限下发成功，添加卡信息至数据库失败";
					}
				} else {
					log.info("卡号已存在做更新处理" + "CarId==" + CarId);

					Integer row = CardService.updateCard(Supply_CardNumber, CardType, CardSonType, 0, null, CardValidPeriod, name, phone, email, CarId, PersonID);
					if (row > 0) {
						result = 0;
						msg = "下发成功，此卡存在，更新卡信息至数据库成功";
					} else {
						result = -1;
						msg = "下发成功，此卡存在，更新卡信息至数据库失败";
					}
				}
			}



		}
		if(CardType==2) {
			log.info("进入卡类型不为1的代码--------------------------------------------------------------------------");
			List<HashMap<String, Object>> all_powers_list_map = new ArrayList<>();
			for (int i = 0; i < Authority.size(); i++) {
				String deviceid = (String) Authority.get(i).get("deviceid");
				String LiftRules = (String) Authority.get(i).get("LiftRules");
				log.info("非通用权限");
				List<String> LiftRule_list = Arrays.asList(LiftRules.split(","));
				for (int j = 0; j < LiftRule_list.size(); j++) {
					String LiftRule = LiftRule_list.get(j);
					Integer power_len = Integer.valueOf(LiftRule);
					//log.info("计算偏值");// 因协议中的楼层00为一层
					// 获取设备偏执
					Integer FloorDifference = deviceservice.queryFloorDifferenceByDeviceID(Integer.valueOf(deviceid));
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
					all_powers_list_map.add(i, power);
				}
			}
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
//			byte[] flag_3 =  new byte[2]; // 2字节标识
//			CardValidPeriod.substring(8,9);
//			byte[] bytes3 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(8, 10)));
//			bytes3[0]=(byte) (bytes3[0] & ~ (1 << 5));
//			bytes3[0]=(byte) (bytes3[0] & ~ (1 << 6));
//			bytes3[0]=(byte) (bytes3[0] | (1 << 7));
//			flag_3[0]=bytes3[0];
//			log.info("有效日-------------"+flag_3[0]);
//			flag_3[1]=(byte) 0x01;
			byte[] flag_3 =  new byte[2]; // 2字节标识
			String substring = CardValidPeriod.substring(8, 10);
			//StringBuilder reverse = new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse();
			String dd=new StringBuilder(ByteUtils.decimalToBinary(Integer.valueOf(substring), 8)).reverse().toString();
			byte[] bytes9 = ByteUtils.long2Hbytes1byte(ByteUtils.binary2To10(dd));
			bytes9[0]=(byte) (bytes9[0] | (1 << 0));
//			flag_3 = ByteUtil.unlong2H2bytes(Integer.valueOf(CardValidPeriod.substring(8, 10)));
			flag_3[0]=bytes9[0];
			flag_3[1] = (byte) 0X01;

			byte[] forth_powor = new byte[8];// 8字节的扇区第二块权限
			// 以下扇区的长度根据权限长度定,无需列举
			// ---------------------第一扇区第一块----------------
			village_code = ByteUtil.Str16toBytes("010101");//小区码暂时写死
			log.info("小区码3位=-=="+village_code[0]+"---"+village_code[1]+"---"+village_code[2]);
			byte[] bytes4 = ByteUtils.hex2Byte(Supply_CardNumber);
			card_number[0]=bytes4[0];
			card_number[1]=bytes4[bytes4.length-1];
			//log.info("卡号字段=-=="+card_number[0]+"---"+card_number[1]+"---"+card_number[2]+"---"+card_number[3]);
			byte[] bytes1 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(2, 4)));
			flag_2[0]= bytes1[0];
			byte[] bytes2 = ByteUtil.int2Hbytes1byte(Integer.valueOf(CardValidPeriod.substring(5, 7)));
			bytes2[0]=(byte) (bytes2[0] | (1 << 4));
			bytes2[0]=(byte) (bytes2[0] & ~ (1 << 5));
			bytes2[0]=(byte) (bytes2[0] & ~ (1 << 6));
			bytes2[0]=(byte) (bytes2[0] | (1 << 7));
			flag_2[1]=bytes2[0];

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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							first_powor[first * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] first_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							first_powor_new[0] = (byte) (first_powor_new[0] & ~ (1 << 7));
							first_powor[first * 2] = first_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							second_powor[second * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] second_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							second_powor_new[0] = (byte) (second_powor_new[0] & ~ (1 << 7));
							second_powor[second * 2] = second_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
					b_CardSonType[0]=(byte) (b_CardSonType[0] & ~(1 << 0));
					b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
					card_type[1] = b_CardSonType[0];
					log.info("开类型第2字段——————"+card_type[1]);
				}else if(CardSonType==2) {
					byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
					b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
					b_CardSonType[0] = (byte) (b_CardSonType[0] & ~(1 << 1));
					card_type[1] = b_CardSonType[0];
				}else {
					card_type[1]= (byte) 0X00;
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
					b_CardSonType[0]=(byte) (b_CardSonType[0] & ~(1 << 0));
					b_CardSonType[0]=(byte) (b_CardSonType[0] | (1 << 1));
					card_type[1] = b_CardSonType[0];
					log.info("开类型第2字段——————"+card_type[1]);
				} else if(CardSonType==2) {
					byte[] b_CardSonType = ByteUtil.int2Hbytes1byte(CardSonType);
					b_CardSonType[0] = (byte) (b_CardSonType[0] | (1 << 2));
					b_CardSonType[0] = (byte) (b_CardSonType[0] & ~(1 << 1));
					card_type[1] = b_CardSonType[0];
				}else {
					card_type[1]= (byte) 0X00;
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
			if(CardType==3||CardType==4||CardType==5){
				byte[] b_CardType = ByteUtil.int2Hbytes1byte(CardType);
				card_type[0] = b_CardType[0];
				card_type[1] = (byte) 0X00;
				card_type[2] = (byte) 0X17;
				card_type[3] = (byte) 0X3B;
				card_type[4] = (byte) 0X00;
				card_type[5] = (byte) 0X00;
				card_type[6] = (byte) 0X00;
				card_type[7] = (byte) 0X00;
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
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
						log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
						third_powor[1] = LiftRule_new[0];
					} else {//手动登记楼层
						//电梯组
						byte[] third_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
						third_powor_new[0] = (byte) (third_powor_new[0] & ~ (1 << 7));
						third_powor[0] = third_powor_new[0];
						//楼层
						byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
						LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							forth_powor[forth * 2 + 1] = LiftRule_new[0];
						} else {//标识手动登记楼层
							//电梯组
							byte[] forth_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							forth_powor_new[0] = (byte) (forth_powor_new[0] & ~ (1 << 7));
							forth_powor[forth * 2] = forth_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
			byte[] surplus_powor = new byte[b_len*2];// 剩余权限,字节不固定
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
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
							log.info("进入s==1:LiftRule_new后===" + LiftRule_new[0]);
							surplus_powor[surplus * 2 + 1] = LiftRule_new[0];
						} else {//手动登记楼层
							//电梯组
							byte[] surplus_powor_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(deviceid));
							surplus_powor_new[0] = (byte) (surplus_powor_new[0] & ~ (1 << 7));
							surplus_powor[surplus * 2] = surplus_powor_new[0];
							//楼层
							byte[] LiftRule_new = ByteUtil.int2Hbytes1byte(Integer.valueOf(LiftRule));
							LiftRule_new[0] = (byte) (LiftRule_new[0] & ~ (1 << 6));//主门有效
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
					first_powor,second_powor,card_type,third_powor,powor_lenth,flag_3,forth_powor,surplus_powor);
			System.out.println("textm：：" + ByteUtil.byte2Hex(textm));
			byte[] makeUp=new byte[240-textm.length];
			byte[] textss=ByteUtil.concatBytes(textm,makeUp);
			//下发设备
			log.info("实际发送指令为="+ByteUtil.byte2Hex(textss));
			boolean report = ScoketServerTKUtils.sendCommand(DeviceID.toString(), "ISSUED", ByteUtil.byte2Hex(textss));
			if(report!=true){
				msg="权限下发失败";
				result =-1;
			}
			else {
				log.info("权限下发成功,修改卡数据");
				//开始添加信息或者更新信息
				log.info("卡号为" + Supply_CardNumber);
				HashMap<String, Object> card = CardService.queryCardByCardNumber(Supply_CardNumber);
				if (card != null) {
					log.info("通过卡号查询不等于null");
					Integer personID = (Integer) card.get("PersonID");
					Integer cardID = (Integer) card.get("CardID");
					log.info("计次卡已存在进行更新处理");
					//更新人员信息
					log.info("人员id" + card.get("PersonID"));
					SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String InputTime = fff.format(new Date());
					Integer update = personService.update(personID, FullName, PhoneNum, InputTime, 0);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String CardStartTime = sdf.format(new Date());
					//更新卡信息
					Integer integer = CardService.updateCard(Supply_CardNumber, CardType, CardSonType, 0, CardStartTime, CardValidPeriod, FullName, PhoneNum, null, cardID.toString(), personID);
					//先删除卡权限
					Integer dele = RuleService.delRuleByPersonID(personID);
					for (int i = 0; i < Authority.size(); i++) {
						String deviceid = (String) Authority.get(i).get("deviceid");
						String liftRules = (String) Authority.get(i).get("LiftRules");
						//开始插入权限表数据
						Integer integer1 = RuleService.addRule(personID, Integer.valueOf(deviceid), StartTime, EndTime, liftRules);
						log.info("权限是否添加成功——————————==" + integer1);
					}
					if (integer == 1) {
						result = 0;
						msg = "权限下发成功，卡已存在，更新卡信息成功";
					}else {
						result = -1;
						msg = "权限下发成功，卡已存在，更新卡信息失败";
					}
				}
				else {
					log.info("该卡为新卡，未下发权限数据库无信息，进行添加操作");
					Integer pid = personService.queryPersonIDByFullNameAndPhoneNum(FullName, PhoneNum);
					if (pid != null) {
						msg = "人员信息已存在，请勿填写相同信息进行下发权限以防错乱";
						result = 0;
					} else{
						log.info("添加人员信息");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String InputTime = sdf.format(new Date());
					Person person = new Person(FullName, PhoneNum, InputTime, 4);
					//添加到数据库中
					Integer integer = personService.addCount(person);
					log.info("人员添加结果--" + integer);
					//添加卡信息
					//查询刚才添加人员的id
					Integer personID = personService.queryPersonIDByFullNameAndPhoneNum(FullName, PhoneNum);
					SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String CardStartTime = fff.format(new Date());
					//添加卡信息
					Integer row = CardService.addCards(personID, Supply_CardNumber, CardType, CardSonType, 0, CardStartTime, CardValidPeriod, FullName, PhoneNum, null, CardValidPeriod);
					log.info("添加卡信息的卡号=" + Supply_CardNumber);
					//查询刚才添加卡的id
					Integer cardID = CardService.queryCardIDByPersonID(personID);
					System.out.println("添加成功的卡id=" + cardID);
					for (int i = 0; i < Authority.size(); i++) {
						String deviceid = (String) Authority.get(i).get("deviceid");
						String liftRules = (String) Authority.get(i).get("LiftRules");
						//开始插入权限表数据
						Integer integer1 = RuleService.addRule(personID, Integer.valueOf(deviceid), StartTime, EndTime, liftRules);
						log.info("权限是否添加成功——————————==" + integer1);
					}
					if ((integer + row) == 2) {
						result = 0;
						msg = "权限下发成功，人员信息、卡信息、卡权限添加成功";
					} else {
						result = -1;
						msg = "权限下发成功，人员信息、卡信息、卡权限添加失败";
					}
				}
			}

			}

		}
		if(CardType==3){
			log.info("投入退出卡");
			byte[] head = ByteUtil.Str16toBytes("55aa");
			byte[] order = ByteUtil.Str16toBytes("82");
			byte[] length = ByteUtil.Str16toBytes("F400");
			byte[] village_code = new byte[3];// 小区码
			village_code = ByteUtil.Str16toBytes("010101");
			byte[] flag = ByteUtil.Str16toBytes("00");
			byte[] card_number = new byte[2];
			card_number = ByteUtil.unlong2H2bytes(Integer.valueOf(Supply_CardNumber));
			byte[] flag2 = new byte[10];
			byte[] flag3 = new byte[8];
			byte[] cardtype = new byte[1];
			cardtype = ByteUtil.int2Hbytes1byte(CardType);
			byte[] flag4 = new byte[23];

			byte[] flag6 = new byte[192];
			byte[] textm = ByteUtil.concatBytes(village_code, flag, card_number, flag2, flag3, cardtype, flag4, flag6);
			log.info("投入退出卡下发数据为="+ByteUtils.byte2Hex(textm));
			boolean issued = ScoketServerTKUtils.sendCommand(DeviceID.toString(), "ISSUED", ByteUtils.byte2Hex(textm));
			if(issued!=true){
				msg="投入退出卡权限下发失败";
				result=-1;
			}else{
				msg="投入退出卡权限下发成功";
				result=0;
			}
		}
		if(CardType==4){
				log.info("挂失卡");
				byte[] head = ByteUtil.Str16toBytes("55aa");
				byte[] order = ByteUtil.Str16toBytes("82");
				byte[] length = ByteUtil.Str16toBytes("F400");
				byte[] village_code = new byte[3];// 小区码
				village_code = ByteUtil.Str16toBytes("010101");
				byte[] flag = ByteUtil.Str16toBytes("00");
				byte[] card_number = new byte[2];
				card_number = ByteUtil.unlong2H2bytes(Integer.valueOf(Supply_CardNumber));
				byte[] flag2 = new byte[10];
				byte[] flag3 = new byte[8];
				byte[] cardtype = new byte[1];
				cardtype = ByteUtil.int2Hbytes1byte(CardType);
				byte[] flag4 = new byte[1];
				byte[] cardID = new byte[4];
				cardID = ByteUtil.long2H4bytes(Integer.valueOf(ReportLostCardNumber));
				byte[] flag5 = new byte[18];
				byte[] flag6 = new byte[192];
				byte[] textm = ByteUtil.concatBytes(village_code, flag, card_number, flag2, flag3, cardtype, flag4, cardID, flag5, flag6);
			log.info("投入退出卡下发数据为="+ByteUtils.byte2Hex(textm));
			boolean issued = ScoketServerTKUtils.sendCommand(DeviceID.toString(), "ISSUED", ByteUtils.byte2Hex(textm));
			if(issued!=true){
				msg="挂失卡权限下发失败";
				result=-1;
			}else{
				msg="挂失卡权限下发成功";
				result=0;
			}
		}
		if(CardType==5){
				log.info("恢复卡");
			byte[] head = ByteUtil.Str16toBytes("55aa");
			byte[] order = ByteUtil.Str16toBytes("82");
			byte[] length = ByteUtil.Str16toBytes("F400");
			byte[] village_code = new byte[3];// 小区码
			village_code = ByteUtil.Str16toBytes("010101");
			byte[] flag = ByteUtil.Str16toBytes("00");
			byte[] card_number = new byte[2];
			card_number = ByteUtil.unlong2H2bytes(Integer.valueOf(Supply_CardNumber));
			byte[] flag2 = new byte[10];
			byte[] flag3 = new byte[8];
			byte[] cardtype = new byte[1];
			cardtype = ByteUtil.int2Hbytes1byte(CardType);
			byte[] flag4 = new byte[1];
			byte[] cardID = new byte[4];
			cardID = ByteUtil.long2H4bytes(Integer.valueOf(ReportLostCardNumber));
			byte[] flag5 = new byte[18];
			byte[] flag6 = new byte[192];
			byte[] textm = ByteUtil.concatBytes(village_code, flag, card_number, flag2, flag3, cardtype, flag4, cardID, flag5, flag6);
			log.info("投入退出卡下发数据为="+ByteUtils.byte2Hex(textm));
			boolean issued = ScoketServerTKUtils.sendCommand(DeviceID.toString(), "ISSUED", ByteUtils.byte2Hex(textm));
			if(issued !=true){
				msg="恢复卡权限下发失败";
				result=-1;
			}else{
				msg="恢复卡权限下发成功";
				result=0;
			}

		}
		map.put("result",result);
		map.put("msg",msg);
		return map;
	}

	/**
	 * 更新设备只能更新设备名称
	 * @param sessionid
	 * @param DeviceID
	 * @param DeviceName
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "updateDevice", method = RequestMethod.POST)
	@ResponseBody
	public Object updateDevice(@RequestParam String sessionid,
							   @RequestParam Integer DeviceID,
								@RequestParam String DeviceName){
		log.info("修改设备数据DeviceName===" + DeviceName);
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		Integer integer = deviceservice.updatDeviceNameByID(DeviceID, DeviceName);
		System.out.println(integer);
		if(integer==1){
			result=0;
			map.put("msg","修改成功");
			map.put("result",result);
			return map;
		}
		result=-1;
		map.put("result",result);
		map.put("msg","修改失败");
		return map;
	}

	/**
	 * hui Execl上传导入数据库
	 *
	 * @param
	 * @param
	 * @param
	 * @param request
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@RequestMapping(value = "carduploads", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin
	public Object carduploads(HttpServletRequest request) throws IllegalStateException, IOException {
		log.info("上传execl文件:filename");
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		String msg = "请上传正确的表格内容";
		Integer Ctotle = null;
		ArrayList list2 = new ArrayList<>();
		ArrayList list3 = new ArrayList<>();
		// 1、解析上传文件字段信息
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<?> items = null;
		try {
			items = upload.parseRequest(request);
			log.info("是否还有数据：{}", items.size());
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		Iterator<?> iter = items.iterator();

		// 2、遍历文件字段信息
		String fieldName = null, fileName = null, fileVer = null, replyTopic = null;
		Integer allflag = null;
		long fileLen = 0;
		byte[] payload = null;
		List<Integer> devlist = new ArrayList<>();
		Map<String, ? super Object> params = new HashMap<>();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// 如果是普通表单字段
				log.info("fieldname:{}, value:{}, name:{}", item.getFieldName(), item.getString(), item.getName());
				if ("allflag".equals(item.getFieldName())) {
					allflag = Integer.valueOf(item.getString());
				} else if ("fileversion".equals(item.getFieldName())) {
					fileVer = item.getString();
				} else {
					params.put(item.getFieldName(), item.getString());
				}
			} else {
				// 如果是文件字段:filedName=file fileName=vbar.tar.xz
				fieldName = item.getFieldName();
				fileName = item.getName();
				fileLen = item.getSize();
				log.info("fieldName:{}, len:{}, fileName:{}", fieldName, fileLen, fileName);
				try {
					// 两种方式获取文件流
					// payload = ByteUtil.toByteArray(item.getInputStream());
					payload = item.get();
					log.info("==读取成功==");
					String execlcard = sysService.queryValueByKey("execlcard");
					System.out.println("读取成功后获取到表格卡————————————————————————————"+execlcard);
					String fileName1 = new String(fileName.getBytes("GBK"), "UTF-8");
					String path = execlcard + fileName1;
					log.info("path==" + path);
					log.info("FilePath:{}", path);
					File newFile = new File(path);

					item.write(newFile);
					log.info("成功");
					// 判断是否为execl表格
					if (newFile.getName().endsWith("xlsx") || newFile.getName().endsWith("xls")) {
						log.info("是execl表格");

						// 上传成功，读取表格内容
						ArrayList<ArrayList<Object>> result1 = ExcelUtil.readExcel(newFile);
						log.info("返回的list++++++++++++++" + result1);
						// 判断跳水
						int size = result1.size();
						log.info("卡号条数为----------" + size);
						if (size > 1000) {
							log.info("卡号超过1000条");
							flag = -2;
							msg = "卡号超过1000条,请分批次导入";
							map.put("result", flag);
							map.put("msg", msg);
							return map;
						}
						Map<String, Object> params1 = new HashMap<String, Object>();
						ArrayList OldCardIDList = new ArrayList<>();
						// 检出表格内容是否有错
						int count = 0;

						for (int i = 1; i < result1.size(); i++) {
							for (int j = 0; j < result1.get(i).size(); j = j + 10) {
								log.info("result1.get(i).size()=====" + result1.get(i).size());
								log.info("ppppppppppppp" + result1.get(i));

								if (result1.get(i).size() > 1) {

									log.info(i + "行 " + j + "列  " + result1.get(i).get(j).toString());
									String DeviceName = result1.get(i).get(j).toString();
									if (DeviceName.equals("")) {
										//log.info("时段卡缺少结束时间");
										flag = -2;
										msg = "设备名称不能为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									}
										String DeviceIdentification = result1.get(i).get(j + 1).toString();
									if (DeviceIdentification.equals("")) {
										//log.info("时段卡缺少结束时间");
										flag = -2;
										msg = "设备标识不能为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									}
									String DeviceType = result1.get(i).get(j + 2).toString();
									if (DeviceType.equals("")) {
										//log.info("时段卡缺少结束时间");
										flag = -2;
										msg = "设备类型不能为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									}
									String DeviceModel = result1.get(i).get(j + 3).toString();
									if (DeviceModel.equals("")) {
										//log.info("时段卡缺少结束时间");
										flag = -2;
										msg = "设备型号不能为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									}
									String DeviceInstalTimel = result1.get(i).get(j + 4).toString();
									if (DeviceInstalTimel.equals("")) {
										//log.info("时段卡缺少结束时间");
										flag = -2;
										msg = "设备安装时间不能为空";
										map.put("result", flag);
										map.put("msg", msg);
										return map;
									}


								}
							}
						}
						// 判断count是否大于0，如果大于就不走添加方法
						if (count > 0) {
							log.info("有错误数据count");

						} else {
							// 开始添加
							for (int i = 1; i < result1.size(); i++) {
								for (int j = 0; j < result1.get(i).size(); j = j + 10) {
									log.info("result1.get(i).size()=====" + result1.get(i).size());
									if (result1.get(i).size() > 1) {
										String DeviceName = result1.get(i).get(j).toString();
										String DeviceIdentification = result1.get(i).get(j + 1).toString();
										String DeviceType = result1.get(i).get(j + 2).toString();
										String DeviceModel = result1.get(i).get(j + 3).toString();
										String DeviceInstalTimel = result1.get(i).get(j + 4).toString();
										log.info("设备类型转化为int===="+Integer.valueOf(DeviceModel));


									//开始添加设备
										Integer row = deviceservice.insertDeviceE(DeviceName,DeviceIdentification,Integer.valueOf(DeviceType),DeviceModel,DeviceInstalTimel);
											log.info("添加成功————————————");
										msg = "添加成功";
									}
								}
							}
						}
					} else {
						log.info("请上传execl表格，");
						msg = "请上传execl表格";
						flag = -3;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		flag = 0;
		map.put("result", flag);
		map.put("msg", msg);
		return map;
	}

}
