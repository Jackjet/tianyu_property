package com.vguang.eneity;

/**
 * @author wangsir
 *
 * 2018年1月8日
 */
public class WxUserInfo {
	
	private Integer wxinfoid;
	private Integer personid;
	private String openId;
	private String nickName;
	private short gender;
	private String language;
	private String city;
	private String province;
	private String country;
	private String avatarUrl;
	private String unionId;
	private String ipaddress;
	private WxPhone wxphone;
	
	public Integer getWxinfoid() {
		return wxinfoid;
	}
	public void setWxinfoid(Integer wxinfoid) {
		this.wxinfoid = wxinfoid;
	}
	public Integer getPersonid() {
		return personid;
	}
	public void setPersonid(Integer personid) {
		this.personid = personid;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public short getGender() {
		return gender;
	}
	public void setGender(short gender) {
		this.gender = gender;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public WxPhone getWxphone() {
		return wxphone;
	}
	public void setWxphone(WxPhone wxphone) {
		this.wxphone = wxphone;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	@Override
	public String toString() {
		return "WxUserInfo [wxinfoid=" + wxinfoid + ", personid=" + personid + ", openId=" + openId + ", nickName="
				+ nickName + ", gender=" + gender + ", language=" + language + ", city=" + city + ", province="
				+ province + ", country=" + country + ", avatarUrl=" + avatarUrl + ", unionId=" + unionId
				+ ", ipaddress=" + ipaddress + ", wxphone=" + wxphone + "]";
	}
	
	
	
}
