package com.vguang.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vguang.eneity.Card;

@Repository
public interface ICardDao {

	Integer queryCountByNumber(String cardnumber);

	Integer insertCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState,
			String cartStartTime, String cartEndTime, String fullName, String phone, String email, String power, String ordCardID, String personIdentification);

	Integer updateCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState, String cartStartTime,
					   String cartEndTime, String fullName, String phone, String email,String cardID,Integer personID);

	Integer queryCountCards(Map<String, Object> params);

	List<Map<String, Object>> queryCards(Map<String, Object> params);
	List<Map<String, Object>> queryC();

	Integer addCard(Card card);

	Integer queryCardIDbyNumber(String carfNumber, Integer cardState);

	Integer queryCardTypeByID(Integer cardID);

	Integer queryCardSonTypeByID(Integer cardID);

	Integer updatePowerByCardID(String PersonID, String power);

	Integer queryCountOrdCardID(Object ordCardID);

	Integer queryCountCardIdByNumAndOrdCardID(String cardNumber, String ordCardID);

	Integer queryCountCardIdByPhone(String phone);

	String queryOldCardIDByCardID(Integer cardID);

	Integer delBlackListByCardID(String cardID);

	Integer delRuleByCardID(String cardID);

	Integer delCardByCardID(String cardID);

	Integer queryPersonIDbyNumber(String cardnumber, Integer cardState);

	Integer[] queryCardIDsByPersonID(int personID);
	Card queryCardByNumber(String cardnumber);
	Integer queryCardTypeByCardNumber(String CardNumber);
	String queryCardNumberBypersonIDAndCardType(Integer personID, Integer CardType);
	Integer addCards(Integer PersonID, String CardNumber, Integer CardType, Integer CardSonType, Integer CardState, String CardStartTime, String CardEndTime, String FullName, String Phone, String Email, String InputTime);
	HashMap<String ,Object> queryCardByCardNumber(String CardNumber);
	Integer updateCardTypeByCardID(Integer CardID);
	Integer queryCardIDByPersonID(Integer personID);
}
