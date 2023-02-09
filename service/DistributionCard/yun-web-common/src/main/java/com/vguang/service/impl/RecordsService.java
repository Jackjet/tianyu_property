package com.vguang.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vguang.dao.IRecordsDao;
import com.vguang.eneity.BlackList;
import com.vguang.service.IRecordsService;
@Service("RecordsService")
public class RecordsService implements IRecordsService {
	@Resource
	private IRecordsDao RecordsDao;

	@Override
	public Integer queryCountOperationRecords(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryCountOperationRecords(params);
	}

	@Override
	public List<Map<String, Object>> queryOperationRecords(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryOperationRecords(params);
	}

	@Override
	public Integer queryCountblacklist(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryCountblacklist(params);
	}

	@Override
	public List<Map<String, Object>> queryblacklist(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryblacklist(params);
	}

	@Override
	public Integer queryCountauthpassrecord(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryCountauthpassrecord(params);
	}

	@Override
	public List<Map<String, Object>> queryauthpassrecord(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RecordsDao.queryauthpassrecord(params);
	}

	@Override
	public Integer insertBlackList(BlackList blackList) {
		// TODO Auto-generated method stub
		return RecordsDao.insertBlackList(blackList);
	}

	@Override
	public Integer InsertRecords(String cardID, Integer deviceID, Integer blackListID, Integer state,
			Integer operatorID) {
		// TODO Auto-generated method stub
		return RecordsDao.InsertRecords(cardID,deviceID,blackListID,state,operatorID);
	}

	@Override
	public Integer deleteBlackListByCardID(String cardID) {
		// TODO Auto-generated method stub
		return RecordsDao.deleteBlackListByCardID(cardID);
	}

	@Override
	public Integer delAuthPassRecordByCardID(String cardID) {
		// TODO Auto-generated method stub
		return RecordsDao.delAuthPassRecordByCardID(cardID);
	}

	@Override
	public Integer delAuthPassRecordByDeviceID(Integer deviceID) {
		// TODO Auto-generated method stub
		return RecordsDao.delAuthPassRecordByDeviceID(deviceID);
	}

	@Override
	public Integer delRecordsByDeviceID(Integer deviceID) {
		// TODO Auto-generated method stub
		return RecordsDao.delRecordsByDeviceID(deviceID);
	}

	@Override
	public Integer delRecordsByCardID(String cardID) {
		// TODO Auto-generated method stub
		return RecordsDao.delRecordsByCardID(cardID);
	}
}
