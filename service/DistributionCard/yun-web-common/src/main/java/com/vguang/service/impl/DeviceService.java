package com.vguang.service.impl;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.vguang.utils.*;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vguang.dao.IDeviceDao;
import com.vguang.eneity.Device;
import com.vguang.service.IDeviceService;
import com.vguang.service.ISyncService;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;
import com.vguang.utils.encrypt.AESUtil;
import com.vguang.utils.encrypt.B64Util;
import org.springframework.web.multipart.MultipartFile;

@Service("deviceService")
public class DeviceService implements IDeviceService{
	@Resource
	private IDeviceDao DeviceDao;
	@Autowired
	private ISyncService sysService;
	private Logger log = LoggerFactory.getLogger(DeviceService.class);
	
	@Override
	public Integer insertDevice(Device device) {
		// TODO Auto-generated method stub
		return DeviceDao.insertDevice(device);
	}

	@Override
	public Integer updateDevice(Device device) {
		return DeviceDao.updateDevice(device);
	}

	@Override
	public Integer insertDeviceE(String DeviceName, String DeviceIdentification, Integer DeviceType, String DeviceModel, String DeviceInstalTimel) {
		return DeviceDao.insertDeviceE(DeviceName,DeviceIdentification,DeviceType,DeviceModel,DeviceInstalTimel);
	}

	@Override
	public Integer updateUn_DeviceFlagByID(Integer flag, Integer deviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.updateUn_DeviceFlagByID(flag,deviceID);
	}
	@Override
	public Integer queryCountDevice(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return DeviceDao.queryCountDevice(params);
	}
	@Override
	public List<Map<String, Object>> queryDevice(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return DeviceDao.queryDevice(params);
	}
	@Override
	public Integer updateDeviceByID(String deviceName, Integer deviceType, Integer deviceState, String deviceWifiName,
			String deviceWifiPassWord, String deviceIP, String devicePort, Integer DeviceID,Integer netType, Integer ip_Mode,
			String mask, String gateway, String dns, String serverIP, String secretType, Integer flag ,String floorDifference) {
		// TODO Auto-generated method stub
		return DeviceDao.updateDeviceByID(deviceName,deviceType,deviceState,deviceWifiName,deviceWifiPassWord,deviceIP,
				devicePort,DeviceID,netType,ip_Mode,mask,gateway,dns,serverIP,secretType,flag,floorDifference);
	}

	@Override
	public Integer updatDeviceNameByID(Integer deviceID,String DeviceName) {
		return DeviceDao.updatDeviceNameByID(deviceID,DeviceName);
	}

	@Override
	public String queryDeviceNameByID(Integer deviceid) {
		// TODO Auto-generated method stub
		return DeviceDao.queryDeviceNameByID(deviceid);
	}
	@Override
	public String loadQrcode(Integer deviceID, Integer ip_Mode, String deviceWifiName, String deviceWifiPassWord,
			String deviceIP, String mask, String gateway, String dns, String devicePort,Integer deviceType,Integer flag, Integer netType, String serverIP, String secretType,String deviceIdentification) {
		String fileName = null,
				imgurl = null,
				qrcode = null;
		
		boolean flag1 = false;
		
		//SystemConfigs sys = (SystemConfigs) SpringContextUtil.getBean("sysConfigs");
		fileName = "device" + deviceID + ".png";
		//二维码配置内容,写入到服务器
		qrcode = genQrcodeContent(deviceID, ip_Mode, deviceWifiName,deviceWifiPassWord,deviceIP,mask,gateway,dns,devicePort,deviceType, flag,  netType, serverIP, secretType,deviceIdentification);
		log.info("loadQrcode 生成qrcode:{}", qrcode);
		if(null != qrcode){
			String devpicsev = sysService.queryValueByKey("devpicsev");
			//String devpicsev = "/usr/local/nginx/html/temp3/device/";
			flag1 = writePic2Disk(qrcode, devpicsev, fileName);
		}
		
		if(flag1){
			String devpicurl = sysService.queryValueByKey("devpicurl");
			//String devpicurl = "/usr/local/nginx/html/temp3/device/";
			//imgurl = devpicurl + fileName;
			imgurl = "temp3\\device" +fileName;
			log.info("imgurl---"+imgurl);
		}
		
		return imgurl;
		
	}
	
    
	public String genQrcodeContent(Integer deviceID, Integer ip_Mode, String deviceWifiName, String deviceWifiPassWord, String deviceIP, String mask, String gateway, String dns, String devicePort,
									Integer deviceType, Integer flag, Integer netType, String serverIP, String secretType,String deviceIdentification){
		String content = null,
				qrcode = null;
		//SystemConfigs sys = (SystemConfigs) SpringContextUtil.getBean("sysConfigs");
		String Power_Sector_Secret =null;
		String Blacklist_Sector_Secret = null;
		String Power_Sector = null;
		String Blacklist_Sector =null;
		
		String P_Power_Sector_Secret =null;
		String P_Blacklist_Sector_Secret =null;
		String P_Power_Sector =null;
		String P_Blacklist_Sector =null;
		
		String D_Power_Sector_Secret = null;
		String D_Blacklist_Sector_Secret = null;
		String D_Power_Sector = null;
		String D_Blacklist_Sector = null;
		if(flag ==1) {
			log.info("普通设备");
			if(1==deviceType) {
				log.info("门禁设备");
				//开始获取门禁秘钥
				 Power_Sector_Secret = sysService.queryValueByKey("M_Power_Sector_Secret");			//门禁权限扇区密钥
				 Blacklist_Sector_Secret = sysService.queryValueByKey("M_Blacklist_Sector_Secret"); //门禁黑名单扇区密钥
				 Power_Sector = sysService.queryValueByKey("M_Power_Sector");						//门禁权限扇区
				 Blacklist_Sector = sysService.queryValueByKey("M_Blacklist_Sector");				//门禁黑名单扇区
				 if(secretType.equals("A")) {
					 log.info("A秘钥");
					 //截取前12位
					  Power_Sector_Secret = Power_Sector_Secret.substring(0, 12);					//门禁权限扇区密钥
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12);			//门禁黑名单扇区密钥
					 
				 }else if(secretType.equals("B")){
					 log.info("B秘钥");
					  Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length()-12, Power_Sector_Secret.length());					//门禁权限扇区密钥
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length()-12, Blacklist_Sector_Secret.length());	//门禁黑名单扇区密钥
				 }
			}else if(2==deviceType) {
				log.info("梯控设备");
				 Power_Sector_Secret = sysService.queryValueByKey("T_Power_Sector_Secret");			//梯控权限扇区密钥
				 Blacklist_Sector_Secret = sysService.queryValueByKey("T_Blacklist_Sector_Secret"); //梯控黑名单扇区密钥
				 Power_Sector = sysService.queryValueByKey("T_Power_Sector");						//梯控权限扇区
				 Blacklist_Sector = sysService.queryValueByKey("T_Blacklist_Sector");				//梯控黑名单扇区
				 if(secretType.equals("A")) {
					 log.info("A秘钥");
					 //截取前12位
					  Power_Sector_Secret = Power_Sector_Secret.substring(0, 12);					//门禁权限扇区密钥
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12);			//门禁黑名单扇区密钥
					 
				 }else if(secretType.equals("B")){
					 log.info("B秘钥");
					  Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length()-12, Power_Sector_Secret.length());					//门禁权限扇区密钥
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length()-12, Blacklist_Sector_Secret.length());	//门禁黑名单扇区密钥
				 }
			}
			//共有
			log.info("人员权限");
			 P_Power_Sector_Secret = sysService.queryValueByKey("P_Power_Sector_Secret");			//人员权限扇区密钥
			 P_Blacklist_Sector_Secret = sysService.queryValueByKey("P_Blacklist_Sector_Secret"); //人员黑名单扇区密钥
			 P_Power_Sector = sysService.queryValueByKey("P_Power_Sector");						//人员权限扇区
			 P_Blacklist_Sector = sysService.queryValueByKey("P_Blacklist_Sector");				//人员黑名单扇区
			 if(secretType.equals("A")) {
				 log.info("A秘钥");
				 //截取前12位
				 P_Power_Sector_Secret =  P_Power_Sector_Secret.substring(0, 12);					//员权限扇区密钥
				 P_Blacklist_Sector_Secret =  P_Blacklist_Sector_Secret.substring(0, 12);			//人员黑名单扇区密钥
				 
			 }else if(secretType.equals("B")){
				 log.info("B秘钥");
				 P_Power_Sector_Secret =  P_Power_Sector_Secret.substring(P_Power_Sector_Secret.length()-12, P_Power_Sector_Secret.length());					//员权限扇区密钥
				 P_Blacklist_Sector_Secret =  P_Blacklist_Sector_Secret.substring(P_Blacklist_Sector_Secret.length()-12, P_Blacklist_Sector_Secret.length());	//人员黑名单扇区密钥
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

				} else if (secretType.equals("B")) {
					log.info("B秘钥");
					D_Power_Sector_Secret = D_Power_Sector_Secret.substring(D_Power_Sector_Secret.length() - 12,
							D_Power_Sector_Secret.length()); // 卡配置权限扇区密钥
					D_Blacklist_Sector_Secret = D_Blacklist_Sector_Secret.substring(D_Blacklist_Sector_Secret.length() - 12,
							D_Blacklist_Sector_Secret.length()); // 卡配置黑名单扇区密钥
				}
		}
		log.info("获取组织唯一标识");
		int random = StringUtil.random();
		String SysConfigName ="组织身份标识";
		Integer SysConfigType =2;
		String SysConfigKey ="OrgIdentity";
		String SysConfigValue =String.valueOf(random);
		String OrgIdentity = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue, null, 1, 1);
		log.info("OrgIdentity====="+OrgIdentity);
		//获取梯控开关
		 SysConfigName ="梯控开关";
		 SysConfigType =2;
		 SysConfigKey ="ElevatorSwitch";
		 SysConfigValue ="1";
		 String ElevatorSwitch = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue, null, 1, 1);
		log.info("ElevatorSwitch====="+ElevatorSwitch);
		if(null != deviceID){
				content = deviceID +"&"+ deviceWifiName +"&"+ deviceWifiPassWord +"&"+ deviceIdentification +"&"+ netType +"&"+ ip_Mode+"&"+ deviceIP+"&"+ mask+"&"+ gateway+"&"+ dns+"&"+null+"&"+null
						+"&"+deviceType+"&"+flag+"&"+secretType+"&"+serverIP+"&"+devicePort+"&"+Power_Sector_Secret+"&"+Blacklist_Sector_Secret+"&"+Power_Sector+"&"+Blacklist_Sector+"&"+
						P_Power_Sector_Secret+"&"+P_Power_Sector + "&" + D_Power_Sector +"&"+ D_Power_Sector_Secret + "&" +ElevatorSwitch +"&" +OrgIdentity;
			log.info("content+++++++++++++++++"+content);
			
			String key = "Nm-#km=IlXER=soP";
			String iv = "oBLEW#UkvmP=WXC=";
			
			byte[] abytes = AESUtil.cbcEncryptToBytes(content, key, iv);
			qrcode = "vgcfg" + B64Util.encode(abytes);
			log.info("genQrcodeContent 生成content:{} hexcontent:{} qrcode:{} key:{} iv:{}", content, HexUtil.bytesToHexFun3(content.getBytes()), qrcode, key, iv);
		}
		
		
		return qrcode;
	}
	@Override
	public boolean writePic2Disk(String content, String path, String fileName) {
		boolean flag = false;
		
		byte[] bytes = null;
		File file = new File(path + fileName);
		//如果存在，则删除
		file.deleteOnExit();
		OutputStream out = null;
		try {
			//生成图片，并保存到服务器
			bytes = QcodeUtil.generate(content, "png", 200, 200);
			out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
			
			flag = true;
		} catch (IOException e) {
			log.error("公司生成二维码失败:{}", e);
			e.printStackTrace();
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}
	@Override
	public Integer queryCountDeviceByIdentificationAndOrdDeviceID(String deviceIdentification, String ordDeviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.queryCountDeviceByIdentificationAndOrdDeviceID(deviceIdentification,ordDeviceID);
	}
	@Override
	public Integer queryDeviceIDByOrgDeviceID(String ordDeviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.queryDeviceIDByOrgDeviceID(ordDeviceID);
	}
	@Override
	public Integer queryCountDeviceIDByOrgDeviceID(String ordDeviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.queryCountDeviceIDByOrgDeviceID(ordDeviceID);
	}
	@Override
	public HashMap<String, Object> querydeviceByID(Integer deviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.querydeviceByID(deviceID);
	}
	@Override
	public Integer queryCountDeviceIDByFlag(Integer flag) {
		// TODO Auto-generated method stub
		return DeviceDao.queryCountDeviceIDByFlag(flag);
	}
	@Override
	public Integer queryDeviceIDByDeviceIdentification(String deviceIdentification) {
		// TODO Auto-generated method stub
		return DeviceDao.queryDeviceIDByDeviceIdentification(deviceIdentification);
	}
	@Override
	public Integer queryDeviceTypeByDeviceID(Integer deviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.queryDeviceTypeByDeviceID(deviceID);
	}
	@Override
	public Integer queryFloorDifferenceByDeviceID(int deviceid) {
		// TODO Auto-generated method stub
		return DeviceDao.queryFloorDifferenceByDeviceID(deviceid);
	}
	@Override
	public List<Map<String, Object>> queryRuleDevice(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return DeviceDao.queryRuleDevice(params);
	}
	@Override
	public String queryOldDeviceIDByDeviceID(String deviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.queryOldDeviceIDByDeviceID(deviceID);
	}

	@Override
	public String querydeviceIdentificationByDeviceID(String deviceID) {
		return DeviceDao.querydeviceIdentificationByDeviceID(deviceID);
	}

	@Override
	public Integer delDeviceByDeviceID(Integer deviceID) {
		// TODO Auto-generated method stub
		return DeviceDao.delDeviceByDeviceID(deviceID);
	}

	@Override
	public Device queryDeviceByDeviceAndType(Integer DeviceID) {
		return DeviceDao.queryDeviceByDeviceAndType(DeviceID);
	}

}
