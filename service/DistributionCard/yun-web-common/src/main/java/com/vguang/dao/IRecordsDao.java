package com.vguang.dao;

import java.util.List;
import java.util.Map;

import com.vguang.eneity.BlackList;

public interface IRecordsDao {

	Integer queryCountOperationRecords(Map<String, Object> params);

	List<Map<String, Object>> queryOperationRecords(Map<String, Object> params);

	Integer queryCountblacklist(Map<String, Object> params);

	List<Map<String, Object>> queryblacklist(Map<String, Object> params);

	Integer queryCountauthpassrecord(Map<String, Object> params);

	List<Map<String, Object>> queryauthpassrecord(Map<String, Object> params);

	Integer insertBlackList(BlackList blackList);

	Integer InsertRecords(String cardID, Integer deviceID, Integer blackListID, Integer state,
			Integer operatorID);

	Integer deleteBlackListByCardID(String cardID);

	Integer delAuthPassRecordByCardID(String cardID);

	Integer delAuthPassRecordByDeviceID(Integer deviceID);

	Integer delRecordsByDeviceID(Integer deviceID);

	Integer delRecordsByCardID(String cardID);

}
