package com.vguang.eneity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

public class Card {

	private Integer CardID;
	
	private Integer PersonID;

	private String CardNumber;
	
	private Integer CardType;
	
	private Integer CardSonType;
	
	private Integer CardState;

	private String CardStartTime;
	
	private String CardEndTime;
	
	private String FullName;
	
	private String Phone;
	
	private String Email;
	
	private String InputTime;
	
	private String Power;
	
	private String CardPower;
	
	private String OldCardID;
	
	private String PersonIdentification;

	private String HouseNum;


	public Card(String cardNumber2, Integer cardType2, Integer cardSonType2, Integer cardState2, String cardStartTime2,
			String cardEndTime2, String fullName2, String phone2, String email2, String power2, String ordCardID2,
			String personIdentification2, String houseNum, Integer personID2) {
		this.CardNumber = cardNumber2;
		this.CardType = cardType2;
		this.CardSonType = cardSonType2;
		this.CardState = cardState2;
		this.CardStartTime = cardStartTime2;
		this.CardEndTime = cardEndTime2;
		this.FullName = fullName2;
		this.Phone = phone2;
		this.Email = email2;
		this.Power = power2;
		this.OldCardID = ordCardID2;
		this.PersonIdentification = personIdentification2;
		this.HouseNum = houseNum;
		this.PersonID = personID2;
	}

	public Card(Integer cardID, Integer personID, String cardNumber, Integer cardType, Integer cardSonType, Integer cardState, String cardStartTime, String cardEndTime, String fullName, String phone, String email, String inputTime, String power, String cardPower,String personIdentification) {
		CardID = cardID;
		PersonID = personID;
		CardNumber = cardNumber;
		CardType = cardType;
		CardSonType = cardSonType;
		CardState = cardState;
		CardStartTime = cardStartTime;
		CardEndTime = cardEndTime;
		FullName = fullName;
		Phone = phone;
		Email = email;
		InputTime = inputTime;
		Power = power;
		CardPower = cardPower;
		PersonIdentification = personIdentification;

	}

	@Override
	public String toString() {
		return "Card{" +
				"CardID=" + CardID +
				", PersonID=" + PersonID +
				", CardNumber='" + CardNumber + '\'' +
				", CardType=" + CardType +
				", CardSonType=" + CardSonType +
				", CardState=" + CardState +
				", CardStartTime='" + CardStartTime + '\'' +
				", CardEndTime='" + CardEndTime + '\'' +
				", FullName='" + FullName + '\'' +
				", Phone='" + Phone + '\'' +
				", Email='" + Email + '\'' +
				", InputTime='" + InputTime + '\'' +
				", Power='" + Power + '\'' +
				", CardPower='" + CardPower + '\'' +
				", OldCardID='" + OldCardID + '\'' +
				", PersonIdentification='" + PersonIdentification + '\'' +
				", HouseNum='" + HouseNum + '\'' +
				'}';
	}

	public String getHouseNum() {
		return HouseNum;
	}



	public void setHouseNum(String houseNum) {
		HouseNum = houseNum;
	}



	public Integer getPersonID() {
		return PersonID;
	}



	public void setPersonID(Integer personID) {
		PersonID = personID;
	}



	public Integer getCardID() {
		return CardID;
	}

	public void setCardID(Integer cardID) {
		CardID = cardID;
	}

	public String getCardNumber() {
		return CardNumber;
	}

	public void setCardNumber(String cardNumber) {
		CardNumber = cardNumber;
	}

	public Integer getCardType() {
		return CardType;
	}

	public void setCardType(Integer cardType) {
		CardType = cardType;
	}

	public Integer getCardSonType() {
		return CardSonType;
	}

	public void setCardSonType(Integer cardSonType) {
		CardSonType = cardSonType;
	}

	public Integer getCardState() {
		return CardState;
	}

	public void setCardState(Integer cardState) {
		CardState = cardState;
	}

	public String getCardStartTime() {
		return CardStartTime;
	}

	public void setCardStartTime(String cardStartTime) {
		CardStartTime = cardStartTime;
	}

	public String getCardEndTime() {
		return CardEndTime;
	}

	public void setCardEndTime(String cardEndTime) {
		CardEndTime = cardEndTime;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getInputTime() {
		return InputTime;
	}

	public void setInputTime(String inputTime) {
		InputTime = inputTime;
	}

	public String getPower() {
		return Power;
	}

	public void setPower(String power) {
		Power = power;
	}

	public String getCardPower() {
		return CardPower;
	}

	public void setCardPower(String cardPower) {
		CardPower = cardPower;
	}


	public String getOldCardID() {
		return OldCardID;
	}

	public void setOldCardID(String oldCardID) {
		OldCardID = oldCardID;
	}

	public String getPersonIdentification() {
		return PersonIdentification;
	}

	public void setPersonIdentification(String personIdentification) {
		PersonIdentification = personIdentification;
	}


}
