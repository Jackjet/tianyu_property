package com.vguang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vguang.eneity.Card;

public interface ICardService {

	Integer queryCountByNumber(String cardnumber);

	Integer insertCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState,
			String cartStartTime, String cartEndTime, String fullName, String phone, String email, String power, String ordCardID, String personIdentification);

	Integer updateCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState, String cartStartTime,
			String cartEndTime, String fullName, String phone, String email,String cardID,Integer personID);

	Integer queryCountCards(Map<String, Object> params);

	List<Map<String, Object>> queryCards(Map<String, Object> params);
	List<Map<String, Object>>queryC();
	Integer addCard(Card card);
	Integer addCards(Integer PersonID,String CardNumber,Integer CardType,Integer CardSonType,Integer CardState, String CardStartTime,String CardEndTime, String
			FullName,String Phone,String Email,String InputTime);

	Integer queryCardIDbyNumber(String carfNumber, Integer cardState);

	Integer queryCardTypeByID(Integer cardID);

	Integer queryCardSonTypeByID(Integer cardID);

	Integer updatePowerByCardID(String PersonID, String power);
	Integer updateCardTypeByCardID(Integer CardID);

	Integer queryCountOrdCardID(Object ordCardID);

	Integer queryCountCardIdByNumAndOrdCardID(String cardNumber, String ordCardID);

	Map<String, Object> Receive_Value(Integer count_down_time, String key) throws InterruptedException;

	Integer queryCountCardIdByPhone(String phone);

	String queryOldCardIDByCardID(Integer cardID);

	Integer delBlackListByCardID(String cardID);

	Integer delRuleByCardID(String cardID);

	Integer delCardByCardID(String cardID);

	Integer queryPersonIDbyNumber(String cardnumber, Integer cardState);

	Integer[] queryCardIDsByPersonID(int personID);
	Integer queryCardIDByPersonID(Integer personID);
	Integer delete_card(String cardID);
	Integer queryCardTypeByCardNumber(String CardNumber);
	HashMap<String ,Object> queryCardByCardNumber(String CardNumber);
String queryCardNumberBypersonIDAndCardType(Integer personID,Integer CardType);



}