package com.vguang.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vguang.eneity.Device;

@Repository
public interface IDeviceDao {

	Integer insertDevice(Device device);
	Integer insertDeviceE(String DeviceName, String DeviceIdentification, Integer DeviceType, String DeviceModel, String DeviceInstalTimel);
	Integer updateUn_DeviceFlagByID(Integer flag, Integer deviceID);

	Integer queryCountDevice(Map<String, Object> params);

	List<Map<String, Object>> queryDevice(Map<String, Object> params);

	Integer updateDeviceByID(String deviceName, Integer deviceType, Integer deviceState, String deviceWifiName,
			String deviceWifiPassWord, String deviceIP, String devicePort, Integer deviceID, Integer netType, Integer ip_Mode, String mask, String gateway, String dns, String serverIP, String secretType, Integer flag, String floorDifference);

	String queryDeviceNameByID(Integer deviceid);

	Integer queryCountDeviceByIdentificationAndOrdDeviceID(String deviceIdentification, String ordDeviceID);

	Integer queryDeviceIDByOrgDeviceID(String ordDeviceID);

	Integer queryCountDeviceIDByOrgDeviceID(String ordDeviceID);

	HashMap<String, Object> querydeviceByID(Integer deviceID);

	Integer queryCountDeviceIDByFlag(Integer flag);

	Integer queryDeviceIDByDeviceIdentification(String deviceIdentification);
	Device queryDeviceByDeviceIdentification(String deviceIdentification);

	Integer queryDeviceTypeByDeviceID(Integer deviceID);

	Integer queryFloorDifferenceByDeviceID(int deviceid);

	List<Map<String, Object>> queryRuleDevice(Map<String, Object> params);

	String queryOldDeviceIDByDeviceID(String deviceID);

	Integer delDeviceByDeviceID(Integer deviceID);
	String querydeviceIdentificationByDeviceID(String deviceID);
	Integer updatDeviceNameByID(Integer deviceID,String DeviceName);
	Integer updateDevice(Device device);
	Device queryDeviceByDeviceAndType(Integer DeviceID);
}
