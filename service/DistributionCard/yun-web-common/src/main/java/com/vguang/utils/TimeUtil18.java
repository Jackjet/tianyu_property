package com.vguang.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wangsir
 *
 * 2018年1月5日
 */
public class TimeUtil18 {
	/**
	 * 对日期添加月数 
	 * @param time
	 * @param count
	 * @return
	 */
	public static String hmscalTime(Date time, int count){
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.MONTH, count);
		
		return sdf.format(cal.getTime());
	}
}
