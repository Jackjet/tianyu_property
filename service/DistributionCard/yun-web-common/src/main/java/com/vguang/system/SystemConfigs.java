package com.vguang.system;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vguang.service.ISyncService;
/**
 * 重构标志位
 * @author wang
 * date 2017-04-14
 * 
 * 腾讯云 123.207.152.144/10.141.189.9
 * 智能门禁
 * AppId  "wx5fb76a552ce9eacf"
 * WXSECRET  "9129d6fae0518cd566493ae0609dc232"
 * 2017-08-03误操作更新密钥"136ed0e816ec12cac262a00bd42d6527"
 * 叮叮开门
   SpAppId = "wx066e6b6611e05b68";
   wxspSecret = "f678cdedd9e757eb8880530561738eb7";
   grant_type = "authorization_code";
   
   10.102.106.169
 */
/**
 * @author wangsir
 *
 * 2017年9月25日
 */
public class SystemConfigs {
	private static final Logger log = LoggerFactory.getLogger(SystemConfigs.class);
	public static final String AppId = "appid";
	public static final String SECRET = "secret";
	public static final String GRANTTYPE = "granttype";
	
	public static final String INHOST = "inhost";
	public static final String HTTPS_HOST = "httpshost";
	public static final String HTTPS_PORT = "httpsport";
	public static final String SOCKETIO_PORT = "socketioport";
	
	public static final String MDOMAIN = "domain";
	public static final String MQ_HOST = "mqhost";
	public static final String MQ_PORT = "mqport";
	public static final String MQ_NAME = "mquname";
	public static final String MQ_PWD = "mqpwd";
	public static final String MQ_BROKER = "mqbroker";
	
	public static final String TENCENT_PARTNER_ID = "partnerid";
	public static final String TENCENT_KEY = "tencentkey";
	public static final String TENCENT_URL = "tencenturl";
	
	public static final String AES_KEY = "aeskey";
	public static final String AES_IV = "aesiv";
	
	public static final String LOGIN_PIC_URL = "loginpicurl";
	public static final String LOGIN_PIC_SEV = "loginpicsev";
	public static final String DEV_PIC_URL = "devpicurl";
	public static final String DEV_PIC_SEV = "devpicsev";
	
	public static final String SSL_KEY = "sslkey";
	public static final String SSL_CERT_URL = "sslcerturl";
	
	public static final String OrgConfigType ="2";
	
	public static final String VIS_PIC_URL = "visitorurl";
	public static final String VIS_PIC_SEV = "visitorsev";
	
	public static final String FAC_PIC_URL = "facilitatorpersonurl";
	public static final String FAC_PIC_SEV = "facilitatorpersonsev";
	
	public static final String TemPlate_ID = "template_id";
	public static final String EXECL_CARD = "execlcard";
	
	
	public static final String YBC_APPID = "ybc_appid";
	public static final String YBC_PRIVATEKEY = "ybc_privatekey";
	public static final String YBC_PUBLICKEY = "ybc_publickey";
	
	
	public static final String ORG_PIC_URL = "orgpersonurl";
	public static final String ORG_PIC_SEV = "orgpersonsev";
	
	//20118年9月22日
	public static final String MONITORINTERVALTIME = "monitorintervaltime";
	public static final String MONITORTIMEOUT = "monitortimeout";
	public static final String DEVICELISTENER = "devicelistener";
	public static final String DEVICELISTENERTIME = "devicelistenertime";
	
	//2019年1月11日
	public static final String WXTRAFFICADMIN = "wxtrafficadmin";
	public static final String WXITTENCEADMIN = "wxittenceadmin";
	public static final String ORGADMIN = "orgadmin";
	
	//2019年2月27日
	public static final String ALIAPPID = "aliappid";
	public static final String ALIPRIVATEKEY = "aliprivatekey";
	public static final String ALIPUBLICKEY = "alipublickey";
	
	//2019年3月8日
	public static final String ALI_VISITORSEV = "alivisitorsev";
	public static final String ALI_VISITORURL = "alivisitorurl";
	
	//2019年3月19日
	public static final String DEVICEPICTURE = "devicepicture";
	public static final String DEVICEPICTUREURL = "devicepictureurl";
	
	public static final String DEVICEVOICE = "devicevoice";
	public static final String DEVICEVOICEURL = "devicevoicegurl";
	
	//2019年5月17日 总后台统计数据
	public static final String allOrgs  = "allOrgs";//总组织
	public static final String OfficeOrgs   = "OfficeOrgs";//办公组织
	public static final String ResidentialOrgs   = "ResidentialOrgs";//小区
	public static final String SchoolOrgs    = "SchoolOrgs";//学校
	public static final String allperson     = "allperson";//总人数
	public static final String addperson     = "addperson";//每天新增加人数
	public static final String onLinePerson     = "onLinePerson";//在线人数
	public static final String allDeviceNum     = "allDeviceNum";//总设备数
	public static final String addDeviceNum     = "addDeviceNum";//每日新增设备
	public static final String onLineDevice      = "onLineDevice"; //在线设备
	public static final String allCurrentNum       = "allCurrentNum";//总通行次数
	public static final String addCurrentNum       = "addCurrentNum"; //新增通行次数
	
	//2019 7-25  智慧园区一码通小程序id和秘钥
	public static final String FacAppID   = "facappid";
	public static final String FacSecret   = "facsecret";
	
	
	//2019 8-09
	public static final String VIS_NOTE_TEM = "notetemplate"; //访客短信模板id
	//2019 8-12
	public static final String AUTHPASSRECORDCOUNT = "authpassrecordcount"; //通行记录数量
	public static final String COEFFICIENT = "coefficient"; //首页统计数据系数
	
	//2019 8-26
	public static final String VIS_CONNECT = "vis_connect"; //用于拼接访客生码连接
	
//	public static final String DEVICEVOICEMIDDLE = "devicevoicemiddle";
//	public static final String DEVICEVOICEURLMIDDLE = "devicevoicegmiddleurl";
	
	//2019 9-05
	public static final String VisAppID   = "visappid";//访客默认appid
	
//	@Autowired
//	private ISyncService syncService;
	public static final String TASK_CRON = "synctime";

//  2019 11-21
	public static final String ATTEND_TIMING = "attend_timing";
	
	//2019 12-16
	public static final String NoVisitors_Orgs = "novisitorsorgs"; //无被访人企业
	
	//2020 2-16
	public static final String Antiepidemic_Default_Appid = "Antiepidemic_Default_Appid"; //防疫缺省小程序appid
	
	//2020 2-22
	public static final String Ladder_control_Default_Appid = "Ladder_control_Default_Appid"; //梯控缺省小程序appid
	
	//public static final String HeartSyncTime = "HeartSyncTime"; //梯控缺省小程序appid
		
	private ISyncService syncService = (ISyncService) SpringContextUtil.getBean("syncService");
	
	public Map<String, String> configs = new HashedMap<>();
	
	public SystemConfigs() {
		log.info("===初始化系统参数===");
		configs.put(GRANTTYPE, "authorization_code");
		configs.put(AES_KEY, "Nm-#km=IlXER=soP");
		configs.put(AES_IV, "oBLEW#UkvmP=WXC=");
		
		//初始化配置项
		init(configs);
		
	}

	private void init(Map<String, String> configs2) {
		log.info("syncService:{}", null==syncService);
		//1、从Sysconfig数据表获取
		List<Map<String, String>> sysconfigs = syncService.querySysConfigs();
		
		
		if(null != sysconfigs){
			for(int i=0; i<sysconfigs.size(); i++){
				Map<String, String> map = sysconfigs.get(i);
				String key = map.get("sysconfigkey");
				String value = map.get("sysconfigvalue");
				configs2.put(key, value);
			}
		}
	}

	public Map<String, String> getConfigsMap(){
		return this.configs;
	}
	
	public String getValue(String key){
		return this.configs.get(key);
	}

	public void setValue(String key, String value) {
		this.configs.put(key, value);
	}
	
	
}
