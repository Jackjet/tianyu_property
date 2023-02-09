package com.vguang.eneity;

public class BlackList {

	private Integer	BlackListID;
	
	private Integer CardID;
	
	private String BlackTime;
	
	private Integer OperatorID;

	public BlackList(Integer cardID2, Integer operatorID2) {
		this.CardID = cardID2;
		this.OperatorID = operatorID2;
	}

	public Integer getBlackListID() {
		return BlackListID;
	}

	public void setBlackListID(Integer blackListID) {
		BlackListID = blackListID;
	}

	public Integer getCardID() {
		return CardID;
	}

	public void setCardID(Integer cardID) {
		CardID = cardID;
	}

	public String getBlackTime() {
		return BlackTime;
	}

	public void setBlackTime(String blackTime) {
		BlackTime = blackTime;
	}

	public Integer getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(Integer operatorID) {
		OperatorID = operatorID;
	}
	
	
}
