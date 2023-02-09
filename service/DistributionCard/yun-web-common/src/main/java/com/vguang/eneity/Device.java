package com.vguang.eneity;

import java.io.Serializable;

public class Device implements Serializable {
	
	private Integer	DeviceID;
	
	private String DeviceName;
	
	private String DeviceModel;
	
	private String DeviceWifiName;
	
	private String DeviceWifiPassWord;
	
	private String DeviceIP;
	
	private String DevicePort;
	
	private Integer DeviceType;
	
	private Integer DeviceState;
	
	private String DeviceInstalTimel;

	private Integer Flag;
	//2020-9-8
	private Integer Ip_Mode;
	
	private String Mask;
	
	private String Gateway;
	
	private String Dns;

	public Device() {
	}

	private Integer NetType;
	
	private String ServerIP;
	
	private String SecretType;
	
	private String OldDeviceID;
	
	private String DeviceIdentification;
	
	private String FloorDifference;
	
	public Device(Integer DeviceIDs,String deviceName2, String deviceModel2, String deviceWifiName2, String deviceWifiPassWord2,
			String deviceIP2, String devicePort2, Integer deviceType2, Integer deviceState2, Integer flag, Integer ip_Mode2, String mask2, String gateway2, String dns2, Integer netType2,
			String serverIP, String secretType, String oldDeviceID, String deviceIdentification, String floorDifference2) {
			this.DeviceID=DeviceIDs;
			this.DeviceName =deviceName2;
			this.DeviceModel =deviceModel2;
			this.DeviceWifiName =deviceWifiName2;
			this.DeviceWifiPassWord =deviceWifiPassWord2;
			this.DeviceIP =deviceIP2;
			this.DevicePort =devicePort2;
			this.DeviceType =deviceType2;
			this.DeviceState =deviceState2;
			this.Flag =flag;
			this.Ip_Mode =ip_Mode2;
			this.Mask =mask2;
			this.Gateway =gateway2;
			this.Dns =dns2;
			this.NetType = netType2;
			this.ServerIP = serverIP;
			this.SecretType = secretType;
			this.OldDeviceID = oldDeviceID;
			this.DeviceIdentification= deviceIdentification;
			this.FloorDifference= floorDifference2;
	}

	public Integer getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(Integer deviceID) {
		DeviceID = deviceID;
	}

	public String getDeviceName() {
		return DeviceName;
	}

	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}

	public String getDeviceModel() {
		return DeviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		DeviceModel = deviceModel;
	}

	public String getDeviceWifiName() {
		return DeviceWifiName;
	}

	public void setDeviceWifiName(String deviceWifiName) {
		DeviceWifiName = deviceWifiName;
	}

	public String getDeviceWifiPassWord() {
		return DeviceWifiPassWord;
	}

	public void setDeviceWifiPassWord(String deviceWifiPassWord) {
		DeviceWifiPassWord = deviceWifiPassWord;
	}

	public String getDeviceIP() {
		return DeviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		DeviceIP = deviceIP;
	}

	public String getDevicePort() {
		return DevicePort;
	}

	public void setDevicePort(String devicePort) {
		DevicePort = devicePort;
	}

	public Integer getDeviceType() {
		return DeviceType;
	}

	public void setDeviceType(Integer deviceType) {
		DeviceType = deviceType;
	}

	public Integer getDeviceState() {
		return DeviceState;
	}

	public void setDeviceState(Integer deviceState) {
		DeviceState = deviceState;
	}

	public String getDeviceInstalTimel() {
		return DeviceInstalTimel;
	}

	public void setDeviceInstalTimel(String deviceInstalTimel) {
		DeviceInstalTimel = deviceInstalTimel;
	}

	public Integer getFlag() {
		return Flag;
	}

	public void setFlag(Integer flag) {
		Flag = flag;
	}

	public Integer getIp_Mode() {
		return Ip_Mode;
	}

	public void setIp_Mode(Integer ip_Mode) {
		Ip_Mode = ip_Mode;
	}

	public String getMask() {
		return Mask;
	}

	public void setMask(String mask) {
		Mask = mask;
	}

	public String getGateway() {
		return Gateway;
	}

	public void setGateway(String gateway) {
		Gateway = gateway;
	}

	public String getDns() {
		return Dns;
	}

	public void setDns(String dns) {
		Dns = dns;
	}

	public Integer getNetType() {
		return NetType;
	}

	public void setNetType(Integer netType) {
		NetType = netType;
	}

	public String getServerIP() {
		return ServerIP;
	}

	public void setServerIP(String serverIP) {
		ServerIP = serverIP;
	}

	public String getSecretType() {
		return SecretType;
	}

	public void setSecretType(String secretType) {
		SecretType = secretType;
	}

	public String getOldDeviceID() {
		return OldDeviceID;
	}

	public void setOldDeviceID(String oldDeviceID) {
		OldDeviceID = oldDeviceID;
	}

	public String getDeviceIdentification() {
		return DeviceIdentification;
	}

	public void setDeviceIdentification(String deviceIdentification) {
		DeviceIdentification = deviceIdentification;
	}

	public String getFloorDifference() {
		return FloorDifference;
	}

	public void setFloorDifference(String floorDifference) {
		FloorDifference = floorDifference;
	}
	
	
	

}
