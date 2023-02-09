package com.vguang.dao;

import org.springframework.stereotype.Repository;
@Repository
public interface ILoginSessionDao {

	Integer queryAccountIDByAccount(String accountname, String accountpassword);

	Integer queryAccountStateByID(Integer accountID);

	Integer queryCardIDByAccount(String accountname, String accountpassword);

	Integer updateFirstState(Integer firstState, Integer accountID);

	Integer insertAccount(String PersonID, String accountName, String accountPassword, Integer firstState,
			Integer accountState);

	Integer updateAccountPasswordByID(String accountID,String AccountName, String accountPassword);

	Integer queryCountAccountByAccountName(String accountName);

	Integer delAccountByCardID(String cardID);

	Integer queryPersonIDByAccount(String accountName, String accountPassword);

}