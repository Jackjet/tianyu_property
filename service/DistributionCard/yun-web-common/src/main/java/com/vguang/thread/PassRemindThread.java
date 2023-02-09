/**
 * 
 */
package com.vguang.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vguang.service.IDeviceService;
import com.vguang.service.IPersonService;
import com.vguang.service.impl.SyncService;
import com.vguang.system.SpringContextUtil;

/**
 * @author Dingjz
 *
 * @date 2018年7月25日
 */
public class PassRemindThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(PassRemindThread.class);

	private String personId;
	private String deviceId;
	private long passTime;
	private boolean passRst;
	private String url;
	private IDeviceService deviceService = (IDeviceService) SpringContextUtil.getBean("deviceService");
	private IPersonService personService = (IPersonService) SpringContextUtil.getBean("personService");
	private SyncService syncService = (SyncService) SpringContextUtil.getBean("syncService");
	/**
	 * @param personId
	 * @param deviceId
	 * @param passTime
	 * @param passRst
	 */
	public PassRemindThread(String personId, String deviceId, long passTime, boolean passRst,String url) {
		super();
		this.personId = personId;
		this.deviceId = deviceId;
		this.passTime = passTime;
		this.passRst = passRst;
		this.url = url;
	}

	@Override
	public void run() {
		log.info("启动线程提醒客户通行信息：personID：{},deviceId：{},passTime:{},passResult:{}", personId, deviceId, passTime,
				passRst);
		
		notifyReport2CallBack(personId, deviceId, passTime, passRst,url);
	}
	/**
	 * 发送给客户
	 * @param personId
	 * @param deviceId
	 * @param l_passTime
	 * @param passRst
	 */
	private void notifyReport2CallBack(String personId, String deviceId, long l_passTime, boolean passRst,String url) {}
	private String getCallBackParams(String orgUserId, String orgDeviceId, String passTime, boolean passRst) {
		StringBuilder builder = new StringBuilder();
		builder.append("UserId=").append(orgUserId).append("&").append("DeviceId=").append(orgDeviceId).append("&")
				.append("PassTime=").append(passTime).append("&").append("PassResult=").append(passRst);
		return builder.toString();
	}
}
