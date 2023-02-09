package com.vguang.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vguang.dao.ICardDao;
import com.vguang.eneity.Card;
import com.vguang.service.ICardService;
import com.vguang.service.IRecordsService;
import com.vguang.utils.JedisManager;

@Service("CardService")
public class CardService implements ICardService {

	@Resource
	private ICardDao CardDao;
	@Autowired
	private ICardService CardService;
	private Logger log = LoggerFactory.getLogger(CardService.class);
	@Autowired
	private JedisManager jedisManager;
	@Autowired
	private IRecordsService RecordsService ;
	@Override
	public Integer queryCountByNumber(String cardnumber) {
		// TODO Auto-generated method stub
		return CardDao.queryCountByNumber(cardnumber);
	}
	@Override
	public Integer insertCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState,
			String cartStartTime, String cartEndTime, String fullName, String phone, String email, String power,String ordCardID, String personIdentification) {
		// TODO Auto-generated method stub
		return CardDao.insertCard(carfNumber,cardType,cardSonType,cardState,cartStartTime,cartEndTime,fullName,phone,email,power,ordCardID,personIdentification);
	}
	@Override
	public Integer updateCard(String carfNumber, Integer cardType, Integer cardSonType, Integer cardState, String cartStartTime,
							  String cartEndTime, String fullName, String phone, String email,String cardID,Integer personID) {
		// TODO Auto-generated method stub
		return CardDao.updateCard(carfNumber,cardType,cardSonType,cardState,cartStartTime,cartEndTime,fullName,phone,email,cardID,personID);
	}
	@Override
	public Integer queryCountCards(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return CardDao.queryCountCards(params);
	}
	@Override
	public List<Map<String, Object>> queryCards(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return CardDao.queryCards(params);
	}

	@Override
	public List<Map<String, Object>> queryC() {
		return CardDao.queryC();
	}

	@Override
	public Integer addCard(Card card) {
		// TODO Auto-generated method stub
		return CardDao.addCard(card);
	}

	@Override
	public Integer addCards(Integer PersonID, String CardNumber, Integer CardType, Integer CardSonType, Integer CardState, String CardStartTime, String CardEndTime, String FullName, String Phone, String Email, String InputTime) {
		return CardDao.addCards(PersonID,CardNumber,CardType,CardSonType,CardState,CardStartTime,CardEndTime,FullName,Phone,Email,InputTime);
	}

	@Override
	public Integer queryCardIDbyNumber(String carfNumber, Integer cardState) {
		// TODO Auto-generated method stub
		return CardDao.queryCardIDbyNumber(carfNumber,cardState);
	}
	@Override
	public Integer queryCardTypeByID(Integer cardID) {
		// TODO Auto-generated method stub
		return CardDao.queryCardTypeByID(cardID);
	}
	@Override
	public Integer queryCardSonTypeByID(Integer cardID) {
		// TODO Auto-generated method stub
		return CardDao.queryCardSonTypeByID(cardID);
	}
	@Override
	public Integer updatePowerByCardID(String PersonID, String power) {
		// TODO Auto-generated method stub
		return CardDao.updatePowerByCardID(PersonID,power);
	}

	@Override
	public Integer updateCardTypeByCardID(Integer CardID) {
		return CardDao.updateCardTypeByCardID(CardID);
	}

	@Override
	public Integer queryCountOrdCardID(Object ordCardID) {
		// TODO Auto-generated method stub
		return CardDao.queryCountOrdCardID(ordCardID);
	}
	@Override
	public Integer queryCountCardIdByNumAndOrdCardID(String cardNumber, String ordCardID) {
		// TODO Auto-generated method stub
		return CardDao.queryCountCardIdByNumAndOrdCardID(cardNumber,ordCardID);
	}
	@Override
	public Map<String, Object> Receive_Value(Integer count_down_time, String key) throws InterruptedException {
		Integer result = -1;
		String msg = "失败";
		HashMap map = new HashMap<>();
		log.info("发送指令成功,开始获取返回值");
		for (int i = 0; i < count_down_time; i++) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = new GregorianCalendar();
			Date date = new Date();
			log.info("系统当前时间      ：" + df.format(date));
			Thread.sleep(1000);
			log.info("i============" + i);
			String Key = key;
			String Json = jedisManager.getValueByStr(0, Key);
			jedisManager.delValueByStr(0, Key);
			log.info("Json=========" + Json);
			if (Json != null) {
				log.info("获取到了返回值,判断是否为正确的返回值");
				if (Json.equals("010000")) {
					jedisManager.delValueByStr(0, Key);
					result = 0;
					msg = "返回成功";
					map.put("result", result);
					map.put("msg", msg);
					return map;
				} else {
					jedisManager.delValueByStr(0, Key);
					log.info("返回值错误,错误返回值为--" + Json);
					msg = "返回值错误";
					result = -3;
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}

			} else {
				if (i == 29) {
					log.info("进来了");
					msg = "设备不在线";
					result = -2;
					map.put("result", result);
					map.put("msg", msg);
					return map;
				}
			}

		}
		result = 0;
		msg = "下发成功";
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	@Override
	public Integer queryCountCardIdByPhone(String phone) {
		// TODO Auto-generated method stub
		return CardDao.queryCountCardIdByPhone(phone);
	}
	@Override
	public String queryOldCardIDByCardID(Integer cardID) {
		// TODO Auto-generated method stub
		return CardDao.queryOldCardIDByCardID(cardID);
	}
	@Override
	public Integer delBlackListByCardID(String cardID) {
		// TODO Auto-generated method stub
		return CardDao.delBlackListByCardID(cardID);
	}
	@Override
	public Integer delRuleByCardID(String cardID) {
		// TODO Auto-generated method stub
		return CardDao.delRuleByCardID(cardID);
	}
	@Override
	public Integer delCardByCardID(String cardID) {
		// TODO Auto-generated method stub
		return CardDao.delCardByCardID(cardID);
	}
	@Override
	public Integer queryPersonIDbyNumber(String cardnumber, Integer cardState) {
		// TODO Auto-generated method stub
		return CardDao.queryPersonIDbyNumber(cardnumber,cardState);
	}
	@Override
	public Integer[] queryCardIDsByPersonID(int personID) {
		// TODO Auto-generated method stub
		return CardDao.queryCardIDsByPersonID(personID);
	}

	@Override
	public Integer queryCardIDByPersonID(Integer personID) {
		return CardDao.queryCardIDByPersonID(personID);
	}

	@Override
	public Integer delete_card(String CardID) {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 删除黑名单
		Integer row = CardService.delBlackListByCardID(CardID);
		// // 删除权限
		// row = CardService.delRuleByCardID(CardID);
		// // 删除登陆
		// row = loginService.delAccountByCardID(CardID);
		// // 删除通行记录
		// row = RecordsService.delAuthPassRecordByCardID(CardID);
		// 删除操作记录
		row = RecordsService.delRecordsByCardID(CardID);
		// 删除卡号
		row = CardService.delCardByCardID(CardID);

		result = 0;
		return result;
	}

	@Override
	public Integer queryCardTypeByCardNumber(String CardNumber) {
		return CardDao.queryCardTypeByCardNumber(CardNumber);
	}

	@Override
	public HashMap<String ,Object> queryCardByCardNumber(String CardNumber) {
		return CardDao.queryCardByCardNumber(CardNumber);
	}

	@Override
	public String queryCardNumberBypersonIDAndCardType(Integer personID, Integer CardType) {
		return CardDao.queryCardNumberBypersonIDAndCardType(personID,CardType);
	}


}
