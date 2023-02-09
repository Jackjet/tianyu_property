package com.vguang.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vguang.eneity.Device;
import org.springframework.web.multipart.MultipartFile;

public interface IDeviceService {

	Integer insertDevice(Device device);
	Integer updateDevice(Device device);
	Integer insertDeviceE(String DeviceName,String DeviceIdentification,Integer DeviceType,String DeviceModel,String DeviceInstalTimel);

	Integer updateUn_DeviceFlagByID(Integer flag, Integer deviceID);

	Integer queryCountDevice(Map<String, Object> params);

	List<Map<String, Object>> queryDevice(Map<String, Object> params);

	Integer updateDeviceByID(String deviceName, Integer deviceType, Integer deviceState, String deviceWifiName,
			String deviceWifiPassWord, String deviceIP, String devicePort, Integer DeviceID, Integer netType, Integer ip_Mode, String mask, String gateway, String dns, String serverIP, String secretType, Integer flag, String floorDifference);
	Integer updatDeviceNameByID(Integer DeviceID,String DeviceName);
	String queryDeviceNameByID(Integer deviceid);

	String loadQrcode(Integer deviceID, Integer ip_Mode, String deviceWifiName, String deviceWifiPassWord,
			String deviceIP, String mask, String gateway, String dns, String devicePort, Integer deviceType, Integer flag, Integer netType, String serverIP, String secretType
			, String deviceIdentification);

	boolean writePic2Disk(String content, String path, String fileName);

	Integer queryCountDeviceByIdentificationAndOrdDeviceID(String deviceIdentification, String ordDeviceID);

	Integer queryDeviceIDByOrgDeviceID(String ordDeviceID);

	Integer queryCountDeviceIDByOrgDeviceID(String ordDeviceID);

	HashMap<String, Object> querydeviceByID(Integer deviceID);

	Integer queryCountDeviceIDByFlag(Integer flag);

	Integer queryDeviceIDByDeviceIdentification(String deviceIdentification);

	Integer queryDeviceTypeByDeviceID(Integer deviceID);

	Integer queryFloorDifferenceByDeviceID(int deviceid);

	List<Map<String, Object>> queryRuleDevice(Map<String, Object> params);

	String queryOldDeviceIDByDeviceID(String deviceID);
	String querydeviceIdentificationByDeviceID(String deviceID);

	Integer delDeviceByDeviceID(Integer deviceID);
	Device queryDeviceByDeviceAndType(Integer DeviceID);

}
