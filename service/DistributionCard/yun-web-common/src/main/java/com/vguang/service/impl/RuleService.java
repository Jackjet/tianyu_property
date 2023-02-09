package com.vguang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vguang.dao.IRuleDao;
import com.vguang.service.IDeviceService;
import com.vguang.service.IRuleService;
import com.vguang.utils.ByteUtil;

@Service("RuleService")
public class RuleService implements IRuleService {
	private Logger log = LoggerFactory.getLogger(RuleService.class);
	@Resource
	private IRuleDao RuleDao;
	@Autowired
	private IDeviceService deviceservice;
	@Autowired
	private IRuleService RuleService;
	@Override
	public Integer queryCountRuleByOldRuleID(String oldRuleID) {
		// TODO Auto-generated method stub
		return RuleDao.queryCountRuleByOldRuleID(oldRuleID);
	}


	@Override
	public Integer queryCountRules(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RuleDao.queryCountRules(params);
	}

	@Override
	public List<Map<String, Object>> queryRules(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return RuleDao.queryRules(params);
	}

	@Override
	public List<Map<String, Object>> queryDeviceNameAndLiftRuleByPersonID(Integer personID) {
		return RuleDao.queryDeviceNameAndLiftRuleByPersonID(personID);
	}

	@Override
	public List<Map<String, Object>> querypersonrule(Integer PersonID,Integer deviceType) {
		// TODO Auto-generated method stub
		return RuleDao.querypersonrule(PersonID,deviceType);
	}

	@Override
	public String queryDeviceidByRuleid(String powers_ruleid) {
		// TODO Auto-generated method stub
		return RuleDao.queryDeviceidByRuleid(powers_ruleid);
	}

	@Override
	public String queryLiftRuleByRuleid(String ruleid) {
		// TODO Auto-generated method stub
		return RuleDao.queryLiftRuleByRuleid(ruleid);
	}

	@Override
	public byte[] Calculation_tk_power(byte[] tk_power, List<String> powers_rule_list) {
		// 获取权限中的电梯号
		for (int i = 0; i < 10; i++) {
			byte[] tk_power_son = new byte[8];
			byte[] tk_power_deviceid = new byte[1];
			byte[] tk_power_liftrule = new byte[7];
			if (powers_rule_list.size() <= i) {

			} else {
				String ruleid = powers_rule_list.get(i);
				if (!"".equals(ruleid)) {
					// 根据权限id获取,获取相应的楼层号和设备号
					String Str_deviceid = RuleService.queryDeviceidByRuleid(ruleid);
					int deviceid = Integer.valueOf(Str_deviceid);
					log.info("deviceid-------->" + deviceid);
					// 获取设备偏执
					Integer FloorDifference = deviceservice.queryFloorDifferenceByDeviceID(deviceid);
					if (null == FloorDifference) {
						log.info("偏执为空,赋值为0");
						FloorDifference = 0;
					} 
//					else {
//						if (FloorDifference < 0) {
//							log.info("偏值数为负数,需要转为对数");
//							FloorDifference = Math.abs(FloorDifference);
//						}
//					}
					log.info("FloorDifference----"+FloorDifference);
					byte r = (byte) deviceid;
					tk_power_deviceid[0] = r;
					String LiftRule = RuleService.queryLiftRuleByRuleid(ruleid);
					log.info("LiftRule-----"+LiftRule);
					String[] powers_arr = LiftRule.split(",");
					for (int k = 0; k < powers_arr.length; k++) {
						log.info("powers_arr----"+powers_arr);
						String Str_power = powers_arr[k];
						Integer power = Integer.valueOf(Str_power);
						// 楼层权限加偏值
						if (power < 0) {
							power = power + FloorDifference + 1;
							log.info("power----"+power);
						} else {
							power = power + FloorDifference;
						}
						if (power <= 0 || power > 56) {
							log.info("跳过===power---->" + power);
							continue;
						}
						log.info("power========="+power);
						int index = 6 - (((power % 8) == 0) ? (power / 8 - 1) : (power / 8));
						int off = (((power % 8) == 0) ? 8 : (power % 8)) - 1;
						tk_power_liftrule[index] = (byte) (tk_power_liftrule[index] | (1 << off));
					}
					tk_power_son = ByteUtil.concatBytes(tk_power_deviceid, tk_power_liftrule);// 合并单个设备的楼层权限
				}

			}

			// 将单个电梯的楼层权限赋值到总权限中
			for (int j = 0; j < 8; j++) {
				int tk_power_son_len = (i * 8) + j;
				System.out.println("------" + tk_power_son_len);
				tk_power[tk_power_son_len] = tk_power_son[j];
			}
		}

		return tk_power;
	}

	@Override
	public Integer delRuleByPersonID(Integer PersonID) {
		// TODO Auto-generated method stub
		return RuleDao.delRuleByPersonID(PersonID);
	}

	@Override
	public Integer delRuleByDeviceID(Integer deviceID) {
		// TODO Auto-generated method stub
		return RuleDao.delRuleByDeviceID(deviceID);
	}




	@Override
	public Integer queryRuleidByDidAndPid(Integer personID, String DeviceID) {
		// TODO Auto-generated method stub
		return RuleDao.queryRuleidByDidAndPid(personID,DeviceID);
	}


	@Override
	public Integer updateRule(Integer ruleid, Integer personID, String DeviceID, String startTime, String endTime,
			String liftRules) {
		// TODO Auto-generated method stub
		return RuleDao.updateRule( ruleid,  personID,  DeviceID,  startTime,  endTime,liftRules);
	}


	@Override
	public Integer addRule(Integer personID, Integer DeviceID, String startTime, String endTime, String liftRule) {
		// TODO Auto-generated method stub
		return RuleDao.addRule( personID,  DeviceID,  startTime,  endTime,  liftRule);
	}


	@Override
	public List<Map<String, Object>> queryRuleByPersonID(Integer personID) {
		// TODO Auto-generated method stub
		return RuleDao.queryRuleByPersonID(personID);
	}

	@Override
	public List<Map<String, Object>> queryListByPersonID(Integer personID) {
		return RuleDao.queryListByPersonID(personID);
	}

	@Override
	public List<Map<String, Object>> queryRulesIDByPersonID(Integer personID) {
		return RuleDao.queryRulesIDByPersonID(personID);
	}


	@Override
	public String queryLiftRuleByDevice(Integer deviceID,Integer PersonID) {
		// TODO Auto-generated method stub
		return RuleDao.queryLiftRuleByDevice(deviceID,PersonID);
	}

	@Override
	public List<Map<String, Object>> queryFamilyGroupRule(Map<String, Object> params) {
		return RuleDao.queryFamilyGroupRule(params);
	}

	@Override
	public Integer queryCountFamilyGroupRule(Map<String, Object> params) {
		return RuleDao.queryCountFamilyGroupRule(params);
	}

	@Override
	public  List<Map<String, Object>> queryPersonIDByGroupID(Integer GroupID) {
		return RuleDao.queryPersonIDByGroupID(GroupID);
	}

	@Override
	public String[]  queryVisitorEndTimeByVisitorApplyID(Integer VisitorApplyID) {
		return RuleDao.queryVisitorEndTimeByVisitorApplyID(VisitorApplyID);
	}

	@Override
	public String[]  queryVisitorBeginTimeByVisitorApplyID(Integer VisitorApplyID) {
		return RuleDao.queryVisitorBeginTimeByVisitorApplyID(VisitorApplyID);
	}

}
