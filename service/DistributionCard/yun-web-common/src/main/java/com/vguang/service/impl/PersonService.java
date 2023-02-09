package com.vguang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vguang.dao.IPersonDao;
import com.vguang.eneity.Person;
import com.vguang.eneity.WxUserInfo;
import com.vguang.service.IPersonService;

@Service("personService")
public class PersonService implements IPersonService {
	@Resource
	private IPersonDao PersonDao;

	@Override
	public Integer[] queryAllPersonidByGroupID(Integer groupID) {
		// TODO Auto-generated method stub
		return PersonDao.queryAllPersonidByGroupID(groupID);
	}

	@Override
	public Integer queryCountPerson(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return PersonDao.queryCountPerson(params);
	}

	@Override
	public List<Map<String, Object>> queryPerson(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return PersonDao.queryPerson(params);
	}

	@Override
	public String queryPhoneByPersonID(int personID) {
		// TODO Auto-generated method stub
		return PersonDao.queryPhoneByPersonID(personID);
	}

	@Override
	public Integer updatePersonStatusByPersonID(int personStatus, int personID) {
		// TODO Auto-generated method stub
		return PersonDao.updatePersonStatusByPersonID(personStatus,personID);
	}

	@Override
	public Integer queryAccountIDByPersonID(int personID) {
		// TODO Auto-generated method stub
		return PersonDao.queryAccountIDByPersonID(personID);
	}

	@Override
	public Integer delUserGroupByPersonID(int personID) {
		// TODO Auto-generated method stub
		return PersonDao.delUserGroupByPersonID(personID);
	}

	@Override
	public Integer delPersonByPersonID(int personID) {
		// TODO Auto-generated method stub
		return PersonDao.delPersonByPersonID(personID);
	}

	@Override
	public Integer updatePersonByPersonID(String fullName, String phoneNum, String email, int personStatus,
			int personID) {
		// TODO Auto-generated method stub
		return PersonDao.updatePersonByPersonID(fullName,phoneNum,email, personStatus,personID);
	}

	@Override
	public Map<String, Object> querypersondetails(Integer personID) {
		// TODO Auto-generated method stub
		return PersonDao.querypersondetails(personID);
	}

	@Override
	public Integer delUserGroupByUserGroupID(Integer userGroupID) {
		// TODO Auto-generated method stub
		return PersonDao.delUserGroupByUserGroupID(userGroupID);
	}

	@Override
	public Integer delUserGroup(Integer groupID) {
		// TODO Auto-generated method stub
		return PersonDao.delUserGroup(groupID);
	}

	@Override
	public Integer updateUserGroupByGroupID(Integer groupID, String userGroupName, String userGroupDesc) {
		// TODO Auto-generated method stub
		return PersonDao.updateUserGroupByGroupID(groupID,  userGroupName,  userGroupDesc);
	}

	@Override
	public Integer queryCountGroup(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return PersonDao.queryCountGroup(params);
	}

	@Override
	public List<Map<String, Object>> queryGroup(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return PersonDao.queryGroup(params);
	}

	@Override
	public Integer queryPersonIDByWxUnionID(String wxUnionID) {
		// TODO Auto-generated method stub
		return PersonDao.queryPersonIDByWxUnionID(wxUnionID);
	}

	@Override
	public Integer addStrangerPerson(Person person) {
		// TODO Auto-generated method stub
		return PersonDao.addStrangerPerson(person);
	}

	@Override
	public Integer queryPersonIDByFullNameAndPhoneNum(String FullName, String PhoneNum) {
		return PersonDao.queryPersonIDByFullNameAndPhoneNum(FullName,PhoneNum);
	}

	@Override
	public Integer queryCountByFullNameAndPhoneNum(String FullName, String PhoneNum) {
		return PersonDao.queryCountByFullNameAndPhoneNum(FullName,PhoneNum);
	}

	@Override
	public Integer addWxUser(WxUserInfo wxuserinfo) {
		// TODO Auto-generated method stub
		return PersonDao.addWxUser(wxuserinfo);
	}

	@Override
	public Integer addCount(Person person) {
		return PersonDao.addCount(person);
	}

	@Override
	public Integer modWxUser(WxUserInfo wxuserinfo) {
		// TODO Auto-generated method stub
		return PersonDao.modWxUser(wxuserinfo);
	}

	@Override
	public Integer queryPersonStatusByPersonID(Integer personid) {
		// TODO Auto-generated method stub
		return PersonDao.queryPersonStatusByPersonID(personid);
	}

	@Override
	public String queryFullNameByPersonID(Integer personid) {
		// TODO Auto-generated method stub
		return PersonDao.queryFullNameByPersonID(personid);
	}

	@Override
	public List<Map<String, Object>> queryUserGroupByPersonID(Integer personid) {
		// TODO Auto-generated method stub
		return PersonDao.queryUserGroupByPersonID(personid);
	}

	@Override
	public Integer queryMaxGroupID() {
		// TODO Auto-generated method stub
		return PersonDao.queryMaxGroupID();
	}

	@Override
	public Integer insertGroup(Integer personID, Integer parentUserGroupID, String userGroupName,String UserGroupDesc, Integer groupID,
			Integer groupPower, Integer status) {
		// TODO Auto-generated method stub
		return PersonDao.insertGroup(personID,  parentUserGroupID,  userGroupName,UserGroupDesc,  groupID, groupPower,  status);
	}

	@Override
	public Integer queryGroupIDByUserGroupName(String userGroupName) {
		// TODO Auto-generated method stub
		return PersonDao.queryGroupIDByUserGroupName(userGroupName);
	}

	@Override
	public HashMap<String, Object> queryPersonByPersonID(Integer personID) {
		// TODO Auto-generated method stub
		return PersonDao.queryPersonByPersonID(personID);
	}

	@Override
	public List<HashMap<String, Object>>queryAll(Integer CardType) {
		return PersonDao.queryAll( CardType);
	}

	@Override
	public HashMap<String, Object> queryPersonDetailedByPersonID(Integer personID) {
		// TODO Auto-generated method stub
		return PersonDao.queryPersonDetailedByPersonID(personID);
	}

	@Override
	public Integer updateGroupStatusByUserGroupID(Integer userGroupID, Integer status) {
		// TODO Auto-generated method stub
		return PersonDao.updateGroupStatusByUserGroupID(userGroupID,status);
	}

	@Override
	public Integer updateUserGroupNameByGroupID(Integer GroupID,String UserGroupName) {
		return PersonDao.updateUserGroupNameByGroupID(GroupID,UserGroupName);
	}

	@Override
	public Integer queryUserGroupStatusByPersonID(Integer personID) {
		// TODO Auto-generated method stub
		return PersonDao.queryUserGroupStatusByPersonID(personID);
	}

	@Override
	public Integer queryhouseholderByGroupidAndGroupPower(Integer GroupID, Integer groupPower) {
		// TODO Auto-generated method stub
		return PersonDao.queryhouseholderByGroupidAndGroupPower(GroupID,groupPower);
	}

	@Override
	public String queryUserGroupNameByUserGroupID(Integer userGroupID) {
		// TODO Auto-generated method stub
		return PersonDao.queryUserGroupNameByUserGroupID(userGroupID);
	}

	@Override
	public String queryEmailByPersonID(Integer PersonID) {
		return PersonDao.queryEmailByPersonID(PersonID);
	}

	@Override
	public Integer update(Integer personID, String FullName, String PhoneNum, String InputTime, Integer PersonStatus) {
		return PersonDao.update(personID,FullName,PhoneNum,InputTime,PersonStatus);
	}

}
