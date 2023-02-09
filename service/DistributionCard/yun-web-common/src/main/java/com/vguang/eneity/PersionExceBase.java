package com.vguang.eneity;

import com.alibaba.excel.annotation.ExcelProperty;


public class PersionExceBase {

    @ExcelProperty(value = "设备名称", index = 0)
    private String DeviceName;

    @ExcelProperty(value = "设备标识", index = 1)
    private String DeviceIdentification;

    @ExcelProperty(value = "设备类型", index = 2)
    private String DeviceType;

    @ExcelProperty(value = "设备偏执", index = 3)
    private String DeviceModel;

    @ExcelProperty(value = "设备安装时间", index = 4)
    private String DeviceInstalTimel;


    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDeviceIdentification() {
        return DeviceIdentification;
    }

    public void setDeviceIdentification(String deviceIdentification) {
        DeviceIdentification = deviceIdentification;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public String getDeviceInstalTimel() {
        return DeviceInstalTimel;
    }

    public void setDeviceInstalTimel(String deviceInstalTimel) {
        DeviceInstalTimel = deviceInstalTimel;
    }
}
