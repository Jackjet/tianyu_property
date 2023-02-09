//package com.vguang.system;
//
//import java.util.Date;
//import java.util.List;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.google.gson.Gson;
//import com.vguang.entity.tencent.TenReport;
//import com.vguang.entity.tencent.TenResponse;
//import com.vguang.service.ITencentService;
//import com.vguang.utils.HttpUtil;
//import com.vguang.utils.encrypt.MD5Util;
//
///**
// * @author wangsir
// *
// * 2017年9月21日
// */
//@Component
//public class SyncReportService {
//	private static final Logger log = LoggerFactory.getLogger(SyncReportService.class);
////	private static final String TEN_PARTNER_ID = SysEnum.TENCENT_PARTNER_ID.getValue();
////	private static final String TEN_KEY = SysEnum.TENCENT_KEY.getValue();
////	private static final String TEN_URL = SysEnum.TENCENT_URL.getValue();
//	@Autowired
//	private ITencentService tenService;
//	
//	/**
//	 * 每天凌晨2点定时将通行记录提交到腾讯
//	 */
//	@Scheduled(cron = "0 0/1 * * * ?")
//	public void syncReport(){
//		log.info("腾讯同步记录当前时间:{}", new Date());
//		//1、查询腾讯的所有小区
//		List<String> comms = tenService.queryCommunitys();
//		
////		Map<String, String> configs = new SystemConfigs().getConfigsMap();
//		SystemConfigs sys = (SystemConfigs) SpringContextUtil.getBean("sysConfigs");
//		String TEN_PARTNER_ID = sys.getValue(SystemConfigs.TENCENT_PARTNER_ID)
//				,TEN_KEY = sys.getValue(SystemConfigs.TENCENT_KEY)
//				,TEN_URL = sys.getValue(SystemConfigs.TENCENT_URL);
//		for(int i=0; i<comms.size(); i++){
//			//2、查询相应小区所有的通行记录
//			TenReport map = tenService.queryReport(comms.get(i));
//			if(null == map){
//				log.info("该小区通行记录为空:communityid:{}", comms.get(i));
//			}else{
//				log.info("community_id:{}", comms.get(i));
//				
//				//3、进行数据签名
//				JSONObject json = new JSONObject(map);
//				String content = json + "&partner_id=" + TEN_PARTNER_ID + "&key=" + TEN_KEY;
//				String sign = MD5Util.encode2hex(content).toUpperCase();
//				
//				//4、向腾讯传输数据
//				String content2 = "data=" + json + "&partner_id=" + TEN_PARTNER_ID +"&sign=" + sign;
//				log.info("上传通行记录:{}", content2);
//				
//				String result = HttpUtil.sendPost(TEN_URL, content2);
//				//5、接收腾讯响应数据, code 0:成功;1:失败
//				Gson gson = new Gson();
//				TenResponse response = gson.fromJson(result, TenResponse.class);
//				log.info("响应code:{}", response.getCode());
//				
//				if(response.getCode() == 0){
//					//6、如果小区通行记录传输成功，更新该小区通行记录的标志
//					tenService.markAuthPass(comms.get(i));
//				}else{
//					log.info("同步腾讯记录失败");
//				}//if response code end
//			}//if map end
//		}
//	}
//	
//	
//	
//	
//}
