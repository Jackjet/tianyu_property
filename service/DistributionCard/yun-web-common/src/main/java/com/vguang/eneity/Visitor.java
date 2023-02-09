package com.vguang.eneity;

public class Visitor {
	
	private Integer	VisitorApplyID;
	
	private Integer PersonID;
	
	private String VisitorApplyName;
	
	private String VisitorApplyPhone;
	
	private String VisitorBeginTime;
	
	private String VisitorEndTime;
	
	private Integer VisitorStatus;
	
	private Integer VisitorType;

	public Visitor(Integer personID2, String visitorApplyName2, String visitorBeginTime2, String visitorEndTime2,
			Integer visitorType2, String visitorApplyPhone2, Integer visitorStatus2) {
		this.PersonID = personID2;
		this.VisitorApplyName = visitorApplyName2;
		this.VisitorBeginTime = visitorBeginTime2;
		this.VisitorEndTime = visitorEndTime2;
		this.VisitorType = visitorType2;
		this.VisitorApplyPhone = visitorApplyPhone2;
		this.VisitorStatus = visitorStatus2;
	}

	public Integer getVisitorApplyID() {
		return VisitorApplyID;
	}

	public void setVisitorApplyID(Integer visitorApplyID) {
		VisitorApplyID = visitorApplyID;
	}

	public Integer getPersonID() {
		return PersonID;
	}

	public void setPersonID(Integer personID) {
		PersonID = personID;
	}

	public String getVisitorApplyName() {
		return VisitorApplyName;
	}

	public void setVisitorApplyName(String visitorApplyName) {
		VisitorApplyName = visitorApplyName;
	}

	public String getVisitorApplyPhone() {
		return VisitorApplyPhone;
	}

	public void setVisitorApplyPhone(String visitorApplyPhone) {
		VisitorApplyPhone = visitorApplyPhone;
	}

	public String getVisitorBeginTime() {
		return VisitorBeginTime;
	}

	public void setVisitorBeginTime(String visitorBeginTime) {
		VisitorBeginTime = visitorBeginTime;
	}

	public String getVisitorEndTime() {
		return VisitorEndTime;
	}

	public void setVisitorEndTime(String visitorEndTime) {
		VisitorEndTime = visitorEndTime;
	}

	public Integer getVisitorStatus() {
		return VisitorStatus;
	}

	public void setVisitorStatus(Integer visitorStatus) {
		VisitorStatus = visitorStatus;
	}

	public Integer getVisitorType() {
		return VisitorType;
	}

	public void setVisitorType(Integer visitorType) {
		VisitorType = visitorType;
	}
	
	
	

}
