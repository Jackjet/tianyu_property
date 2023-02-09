package com.vguang.utils;

import java.sql.Timestamp;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * @author wangsir
 *
 * 2017年11月1日
 */
public class TimeRangeUtil {
	private static final Logger log = LoggerFactory.getLogger(TimeRangeUtil.class);
	
	static Gson gson = new Gson();
	
	/**
	 * 不重复
	 * @param begintime
	 * @param endtime
	 * @param cur
	 * @return
	 */
	public static boolean checkNoRepeate(Timestamp begin, Timestamp end, Timestamp cur) {
		if(cur.after(begin) && cur.before(end)){
			log.info("===判断时分秒===");
			return true;
		}else{
			log.info("重复失效");
		}
		return false;
	}
	
	/**
	 * 检查日重复:不判断年月日,只判断时分秒
	 * 检查周重复
	 * 检查月重复
	 * @return
	 */
	public static boolean checkRepeate(Timestamp begin, Timestamp end, Timestamp cur){
		log.info("===checkRepeate===");
		//先比较日期(年月日)
		log.info("begin:{}, cur:{}, end:{}", gson.toJson(begin), gson.toJson(cur), gson.toJson(end));
		if(cur.after(begin) && cur.before(end)){
			log.info("===判断时分秒===");
			//再比较时间(时分秒)
			if(TimeUtil.compareTo(begin, cur) >= 0 && TimeUtil.compareTo(cur, end) >= 0){
				return true;
			}
		}else{
			log.info("重复失效");
		}
		
		return false;
	}
	
	/**
	 * 检查工作日重复
	 * @return
	 */
	public static boolean checkWorkdayRepeate(Timestamp begin, Timestamp end, Timestamp cur){
		log.info("===checkWorkdayRepeate===");
		//先比较日期(年月日)
		if(cur.after(begin) && cur.before(end)){
			Integer wday = TimeUtil.getDayOfWeek(cur);
			//判断是否在周一--周五之间
			if(wday > 1 && wday < 5){
				//再比较时间(时分秒)
				if(TimeUtil.compareTo(begin, cur) >= 0 && TimeUtil.compareTo(cur, end) >= 0){
					return true;
				}
			}
		}else{
			log.info("工作日重复失效");
		}
		
		return false;
	}
	
	/**
	 * 检查自定义日重复
	 * @return
	 */
	public static boolean checkCustDayRepeate(Timestamp begin, Timestamp end, Timestamp cur, int space){
		log.info("===checkCustDayRepeate===");
		//先比较日期(年月日)
		if(cur.after(begin) && cur.before(end)){
			//判断是否在间隔日
			if((TimeUtil.getDaySpace(begin, cur) % space) == 0){
				//再比较时间(时分秒)
				if(TimeUtil.compareTo(begin, cur) >= 0 && TimeUtil.compareTo(cur, end) >= 0){
					return true;
				}
			}
		}else{
			log.info("自定义日重复失效");
		}
		
		return false;
	}
	
	/**
	 * 检查自定义周重复
	 * @return
	 */
	public static boolean checkCustWeekRepeate(Timestamp begin, Timestamp end, Timestamp cur, int space, Set<Integer> repeat){
		log.info("===checkCustWeekRepeate===");
		//先比较日期(年月日)
		if(cur.after(begin) && cur.before(end)){
			//判断是否在间隔周
			if((TimeUtil.getWeekSpace(begin, cur) % space) == 0){
				//是否在该周选择日内
				if(repeat.contains(TimeUtil.getDayOfWeek(cur))){
					//再比较时间(时分秒)
					if(TimeUtil.compareTo(begin, cur) >= 0 && TimeUtil.compareTo(cur, end) >= 0){
						return true;
					}
				}
			}
		}else{
			log.info("自定义周重复失效");
		}
		
		return false;
	}
	
	/**
	 * 检查自定义月重复
	 * @return
	 */
	public static boolean checkCustMonthRepeate(Timestamp begin, Timestamp end, Timestamp cur, int space, Set<Integer> repeat){
		log.info("===checkCustMonthRepeate===");
		//先比较日期(年月日)
		if(cur.after(begin) && cur.before(end)){
			//判断是否在间隔周
			if((TimeUtil.getMonthSpace(begin, cur) % space) == 0){
				//是否在该周选择日内
				if(repeat.contains(TimeUtil.getDayOfMonth(cur))){
					//再比较时间(时分秒)
					if(TimeUtil.compareTo(begin, cur) >= 0 && TimeUtil.compareTo(cur, end) >= 0){
						return true;
					}
				}
			}
		}else{
			log.info("自定义月重复失效");
		}
		
		return true;
	}

	
	
	
}
