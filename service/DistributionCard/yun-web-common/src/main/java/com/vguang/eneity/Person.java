package com.vguang.eneity;

public class Person {

	private Integer	PersonID;
	
	private String FullName;
	
	private String Email;
	
	private String PhoneNum;
	
	private String InputTime;
	
	private Integer	PersonStatus;
	
	private String wxcode;
	
	private String WxUnionID;

//	public Person(String nickName, String unionid, Integer personStatus2, String wxUnionID2) {
//		this.wxcode = nickName;
//		this.WxUnionID = unionid;
//		this.PersonStatus = personStatus2;
//		this.WxUnionID = wxUnionID2;
//	}
	public Person(String nickName, Integer personStatus2, String wxUnionID2) {
		this.wxcode = nickName;
		//this.WxUnionID = unionid;
		this.PersonStatus = personStatus2;
		this.WxUnionID = wxUnionID2;
	}
	public Person(String FullName ,String PhoneNum ,String InputTime,Integer PersonStatus) {
		this.FullName=FullName;
		this.PhoneNum=PhoneNum;
		this.InputTime=InputTime;
		this.PersonStatus=PersonStatus;
	}

	public Integer getPersonID() {
		return PersonID;
	}

	public void setPersonID(Integer personID) {
		PersonID = personID;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhoneNum() {
		return PhoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}

	public String getInputTime() {
		return InputTime;
	}

	public void setInputTime(String inputTime) {
		InputTime = inputTime;
	}

	public Integer getPersonStatus() {
		return PersonStatus;
	}

	public void setPersonStatus(Integer personStatus) {
		PersonStatus = personStatus;
	}

	public String getWxcode() {
		return wxcode;
	}

	public void setWxcode(String wxcode) {
		this.wxcode = wxcode;
	}

	public String getWxUnionID() {
		return WxUnionID;
	}

	public void setWxUnionID(String wxUnionID) {
		WxUnionID = wxUnionID;
	}
	
	
	
}
