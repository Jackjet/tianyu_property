package com.vguang.service;

public interface ILoginService {

	Integer queryAccountIDByAccount(String accountname, String accountpassword);

	Integer queryAccountStateByID(Integer accountID);

	Integer queryCardIDByAccount(String accountname, String accountpassword);

	Integer updateFirstState(Integer firstState, Integer accountID);

	Integer insertAccount(String PersonID, String accountName, String accountPassword, Integer firstState,
			Integer accountState);

	Integer updateAccountPasswordByID(String accountID, String AccountName,String accountPassword);

	Integer queryCountAccountByAccountName(String accountName);

	Integer delAccountByCardID(String cardID);

	Integer queryPersonIDByAccount(String accountName, String accountPassword);}
