package com.vguang.eneity;

/**
 * @author wangsir
 *
 * 2018年1月8日
 */
public class WxPhone {
	private String model;
	private String pixelRatio;
	private String windowWidth;
	private String windowHeight;
	private String version;
	private String platform;
	private String system;
	
	public WxPhone() {
		super();
	}

	public WxPhone(String model, String pixelRatio, String windowWidth, String windowHeight, String version, String platform, String system) {
		super();
		this.model = model;
		this.pixelRatio = pixelRatio;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.version = version;
		this.platform = platform;
		this.system = system;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPixelRatio() {
		return pixelRatio;
	}

	public void setPixelRatio(String pixelRatio) {
		this.pixelRatio = pixelRatio;
	}

	public String getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(String windowWidth) {
		this.windowWidth = windowWidth;
	}

	public String getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(String windowHeight) {
		this.windowHeight = windowHeight;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}
	
	

}
