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
		//?????????????????????,??????????????????
		qrcode = genQrcodeContent(deviceID, ip_Mode, deviceWifiName,deviceWifiPassWord,deviceIP,mask,gateway,dns,devicePort,deviceType, flag,  netType, serverIP, secretType,deviceIdentification);
		log.info("loadQrcode ??????qrcode:{}", qrcode);
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
			log.info("????????????");
			if(1==deviceType) {
				log.info("????????????");
				//????????????????????????
				 Power_Sector_Secret = sysService.queryValueByKey("M_Power_Sector_Secret");			//????????????????????????
				 Blacklist_Sector_Secret = sysService.queryValueByKey("M_Blacklist_Sector_Secret"); //???????????????????????????
				 Power_Sector = sysService.queryValueByKey("M_Power_Sector");						//??????????????????
				 Blacklist_Sector = sysService.queryValueByKey("M_Blacklist_Sector");				//?????????????????????
				 if(secretType.equals("A")) {
					 log.info("A??????");
					 //?????????12???
					  Power_Sector_Secret = Power_Sector_Secret.substring(0, 12);					//????????????????????????
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12);			//???????????????????????????
					 
				 }else if(secretType.equals("B")){
					 log.info("B??????");
					  Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length()-12, Power_Sector_Secret.length());					//????????????????????????
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length()-12, Blacklist_Sector_Secret.length());	//???????????????????????????
				 }
			}else if(2==deviceType) {
				log.info("????????????");
				 Power_Sector_Secret = sysService.queryValueByKey("T_Power_Sector_Secret");			//????????????????????????
				 Blacklist_Sector_Secret = sysService.queryValueByKey("T_Blacklist_Sector_Secret"); //???????????????????????????
				 Power_Sector = sysService.queryValueByKey("T_Power_Sector");						//??????????????????
				 Blacklist_Sector = sysService.queryValueByKey("T_Blacklist_Sector");				//?????????????????????
				 if(secretType.equals("A")) {
					 log.info("A??????");
					 //?????????12???
					  Power_Sector_Secret = Power_Sector_Secret.substring(0, 12);					//????????????????????????
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(0, 12);			//???????????????????????????
					 
				 }else if(secretType.equals("B")){
					 log.info("B??????");
					  Power_Sector_Secret = Power_Sector_Secret.substring(Power_Sector_Secret.length()-12, Power_Sector_Secret.length());					//????????????????????????
					  Blacklist_Sector_Secret = Blacklist_Sector_Secret.substring(Blacklist_Sector_Secret.length()-12, Blacklist_Sector_Secret.length());	//???????????????????????????
				 }
			}
			//??????
			log.info("????????????");
			 P_Power_Sector_Secret = sysService.queryValueByKey("P_Power_Sector_Secret");			//????????????????????????
			 P_Blacklist_Sector_Secret = sysService.queryValueByKey("P_Blacklist_Sector_Secret"); //???????????????????????????
			 P_Power_Sector = sysService.queryValueByKey("P_Power_Sector");						//??????????????????
			 P_Blacklist_Sector = sysService.queryValueByKey("P_Blacklist_Sector");				//?????????????????????
			 if(secretType.equals("A")) {
				 log.info("A??????");
				 //?????????12???
				 P_Power_Sector_Secret =  P_Power_Sector_Secret.substring(0, 12);					//?????????????????????
				 P_Blacklist_Sector_Secret =  P_Blacklist_Sector_Secret.substring(0, 12);			//???????????????????????????
				 
			 }else if(secretType.equals("B")){
				 log.info("B??????");
				 P_Power_Sector_Secret =  P_Power_Sector_Secret.substring(P_Power_Sector_Secret.length()-12, P_Power_Sector_Secret.length());					//?????????????????????
				 P_Blacklist_Sector_Secret =  P_Blacklist_Sector_Secret.substring(P_Blacklist_Sector_Secret.length()-12, P_Blacklist_Sector_Secret.length());	//???????????????????????????
			 }
			 
			 log.info("???????????????");
				D_Power_Sector_Secret = sysService.queryValueByKey("D_Power_Sector_Secret"); // ???????????????????????????
				D_Blacklist_Sector_Secret = sysService.queryValueByKey("D_Blacklist_Sector_Secret"); // ??????????????????????????????
				D_Power_Sector = sysService.queryValueByKey("D_Power_Sector"); // ?????????????????????
				D_Blacklist_Sector = sysService.queryValueByKey("D_Blacklist_Sector"); // ????????????????????????
				if (secretType.equals("A")) {
					log.info("A??????");
					// ?????????12???
					D_Power_Sector_Secret = D_Power_Sector_Secret.substring(0, 12); //???????????????????????????
					D_Blacklist_Sector_Secret = D_Blacklist_Sector_Secret.substring(0, 12); // ??????????????????????????????

				} else if (secretType.equals("B")) {
					log.info("B??????");
					D_Power_Sector_Secret = D_Power_Sector_Secret.substring(D_Power_Sector_Secret.length() - 12,
							D_Power_Sector_Secret.length()); // ???????????????????????????
					D_Blacklist_Sector_Secret = D_Blacklist_Sector_Secret.substring(D_Blacklist_Sector_Secret.length() - 12,
							D_Blacklist_Sector_Secret.length()); // ??????????????????????????????
				}
		}
		log.info("????????????????????????");
		int random = StringUtil.random();
		String SysConfigName ="??????????????????";
		Integer SysConfigType =2;
		String SysConfigKey ="OrgIdentity";
		String SysConfigValue =String.valueOf(random);
		String OrgIdentity = SysConfig.GetSysConfig(SysConfigName, SysConfigType, SysConfigKey, SysConfigValue, null, 1, 1);
		log.info("OrgIdentity====="+OrgIdentity);
		//??????????????????
		 SysConfigName ="????????????";
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
			log.info("genQrcodeContent ??????content:{} hexcontent:{} qrcode:{} key:{} iv:{}", content, HexUtil.bytesToHexFun3(content.getBytes()), qrcode, key, iv);
		}
		
		
		return qrcode;
	}
	@Override
	public boolean writePic2Disk(String content, String path, String fileName) {
		boolean flag = false;
		
		byte[] bytes = null;
		File file = new File(path + fileName);
		//????????????????????????
		file.deleteOnExit();
		OutputStream out = null;
		try {
			//????????????????????????????????????
			bytes = QcodeUtil.generate(content, "png", 200, 200);
			out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
			
			flag = true;
		} catch (IOException e) {
			log.error("???????????????????????????:{}", e);
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
