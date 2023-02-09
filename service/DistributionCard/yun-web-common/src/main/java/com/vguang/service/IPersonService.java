package com.vguang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vguang.eneity.Person;
import com.vguang.eneity.WxUserInfo;

public interface IPersonService {

	Integer[] queryAllPersonidByGroupID(Integer groupID);

	Integer queryCountPerson(Map<String, Object> params);

	List<Map<String, Object>> queryPerson(Map<String, Object> params);

	String queryPhoneByPersonID(int personID);

	Integer updatePersonStatusByPersonID(int personStatus, int personID);

	Integer queryAccountIDByPersonID(int personID);

	Integer delUserGroupByPersonID(int personID);

	Integer delPersonByPersonID(int personID);

	Integer updatePersonByPersonID(String fullName, String phoneNum, String email, int personStatus, int personID);

	Map<String, Object> querypersondetails(Integer personID);

	Integer delUserGroupByUserGroupID(Integer userGroupID);

	Integer delUserGroup(Integer groupID);

	Integer updateUserGroupByGroupID(Integer groupID, String userGroupName, String userGroupDesc);

	Integer queryCountGroup(Map<String, Object> params);

	List<Map<String, Object>> queryGroup(Map<String, Object> params);

	Integer queryPersonIDByWxUnionID(String wxUnionID);

	Integer addStrangerPerson(Person person);
	Integer queryPersonIDByFullNameAndPhoneNum(String FullName,String PhoneNum);
	Integer queryCountByFullNameAndPhoneNum(String FullName,String PhoneNum);

	Integer addWxUser(WxUserInfo wxuserinfo);
	//添加计次人员信息
	Integer addCount(Person person);
	Integer modWxUser(WxUserInfo wxuserinfo);

	Integer queryPersonStatusByPersonID(Integer personid);

	String queryFullNameByPersonID(Integer personid);

	List<Map<String, Object>> queryUserGroupByPersonID(Integer personid);

	Integer queryMaxGroupID();

	Integer insertGroup(Integer personID, Integer parentUserGroupID, String userGroupName,String UserGroupDesc, Integer groupID,
			Integer groupPower, Integer status);

	Integer queryGroupIDByUserGroupName(String userGroupName);

	HashMap<String, Object> queryPersonByPersonID(Integer personID);
	List<HashMap<String, Object>> queryAll(Integer CardType);

	HashMap<String, Object> queryPersonDetailedByPersonID(Integer personID);

	Integer updateGroupStatusByUserGroupID(Integer userGroupID, Integer status);
	Integer updateUserGroupNameByGroupID(Integer GroupID,String UserGroupName);

	Integer queryUserGroupStatusByPersonID(Integer personID);

	Integer queryhouseholderByGroupidAndGroupPower(Integer GroupID, Integer groupPower);

	String queryUserGroupNameByUserGroupID(Integer userGroupID);
	String queryEmailByPersonID(Integer PersonID );
	Integer update(Integer personID,String FullName,String PhoneNum,String InputTime,Integer PersonStatus);
}
