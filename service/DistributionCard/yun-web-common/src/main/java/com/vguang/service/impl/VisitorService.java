package com.vguang.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vguang.dao.IVisitorDao;
import com.vguang.eneity.Visitor;
import com.vguang.service.ISyncService;
import com.vguang.service.IVisitorService;
import com.vguang.utils.HttpUtil;
@Service("VisitorService")
public class VisitorService implements IVisitorService {
	@Resource
	private IVisitorDao VisitorDao;
	@Autowired
	private ISyncService sysService;
	private Logger log = LoggerFactory.getLogger(VisitorService.class);
	@Override
	public Integer queryCountVisitor(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return VisitorDao.queryCountVisitor(params);
	}

	@Override
	public Integer queryCountHistoryVisitor(Map<String, Object> params) {
		return VisitorDao.queryCountHistoryVisitor(params);
	}

	@Override
	public List<Map<String, Object>> queryVisitor(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return VisitorDao.queryVisitor(params);
	}

	@Override
	public List<Map<String, Object>> queryHistoryVisitor(Map<String, Object> params) {
		return VisitorDao.queryHistoryVisitor(params);

	}

	@Override
	public HashMap<String, Object> queryVisitorapplyByVid(Integer visitorApplyID) {
		// TODO Auto-generated method stub
		return VisitorDao.queryVisitorapplyByVid(visitorApplyID);
	}
	@Override
	public List<Map<String, Object>> queryVisitorApplyRuleByVisitorApplyID(Integer visitorApplyID) {
		// TODO Auto-generated method stub
		return VisitorDao.queryVisitorApplyRuleByVisitorApplyID(visitorApplyID);
	}
	@Override
	public Map<String, Object> invitevisitorcode(Integer PersonID, Integer Flag) {
		Map<String, Object> map = new HashMap<>();
		Integer result = -1;
		String msg = "";
		// 生成二维码图片
		JSONObject params = new JSONObject();
		OutputStream fout = null;
		try {
			if (Flag == 1) {
				params.put("path", "pages/index/index?VisitorType=1");
			} else {
				params.put("path", "pages/index/index?VisitorType=1&PersonID=" + PersonID);
			}

			params.put("width", 300);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String wAppid = sysService.queryValueByKey("appid");
		//wAppid=wxa5c3e69894041e1f
		String wSecret = sysService.queryValueByKey("secret");
		//wSecret=14028c2bb5f5fa9136c291d79c170c14
		log.info("页面图片");
		String access_token = HttpUtil.getWxAccessToken(wAppid, wSecret, null);
		InputStream in = HttpUtil.getWxQrCode(access_token, params);
		File file = new File("/usr/local/nginx/html/temp2/visitor/");
		int bytes;
		byte[] buffer = new byte[1024];
		String filePath = null;
		// 图片请求路径
		if (!file.exists() && !file.isDirectory()) {
			log.info("//不存在");
			file.mkdir();
		} else {
			log.info("//目录存在");
		}
		String OrgidName = "invitevisitorcode.png";
		filePath = sysService.queryValueByKey("invitesev");
		String inviteurl = sysService.queryValueByKey("inviteurl");
		filePath = filePath + OrgidName;
		String imgurl = inviteurl + OrgidName;
		String Url = HttpUtil.writeQrcode(in, filePath);
		map.put("imgurl", imgurl);
		result = 0;
		map.put("result", result);
		map.put("msg", msg);

		return map;
	}
	@Override
	public Integer insertvisitor(Visitor visitor) {
		// TODO Auto-generated method stub
		return VisitorDao.insertvisitor(visitor);
	}
	@Override
	public Integer insertVisitorApplyRule(Integer deviceID, Integer visitorApplyID, String liftRule,
			String visitorBeginTime, String visitorEndTime) {
		// TODO Auto-generated method stub
		return VisitorDao.insertVisitorApplyRule(deviceID,visitorApplyID,liftRule,visitorBeginTime,visitorEndTime);
	}
	@Override
	public Integer updatevisitor(Integer visitorApplyID, String visitorApplyName, String visitorBeginTime,
			String visitorEndTime, Integer visitorStatus, String visitorApplyPhone) {
		// TODO Auto-generated method stub
		return VisitorDao.updatevisitor(visitorApplyID,visitorApplyName,visitorBeginTime,visitorEndTime,visitorStatus,visitorApplyPhone);
	}
	@Override
	public Integer updateVisitorTimeByVisitorApplyID(Integer visitorApplyID, String visitorBeginTime,
			String visitorEndTime) {
		// TODO Auto-generated method stub
		return VisitorDao.updateVisitorTimeByVisitorApplyID(visitorApplyID,visitorBeginTime,visitorEndTime);
	}
	@Override
	public List<HashMap<String, Object>> getvisitorrule(Integer visitorApplyID) {
		// TODO Auto-generated method stub
		return VisitorDao.getvisitorrule(visitorApplyID);
	}
	@Override
	public Integer delvisitorrule(Integer VisitorApplyRuleID) {
		// TODO Auto-generated method stub
		return VisitorDao.delvisitorrule(VisitorApplyRuleID);
	}
	@Override
	public Integer updateVisitorRuleByID(Integer visitorApplyRuleID, Integer deviceID, Integer visitorApplyID,
			String liftRule, String visitorBeginTime, String visitorEndTime) {
		// TODO Auto-generated method stub
		return VisitorDao.updateVisitorRuleByID(visitorApplyRuleID, deviceID, visitorApplyID, liftRule,
				visitorBeginTime, visitorEndTime);
	}

	@Override
	public Integer deleteVisitorRecordById(Integer VisitorApplyID) {
		return VisitorDao.deleteVisitorRecordById(VisitorApplyID);
	}

	@Override
	public Integer deleteVisitorAuthorityById(Integer VisitorApplyID) {
		return VisitorDao.deleteVisitorAuthorityById(VisitorApplyID);
	}


}
