package com.vguang.utils;

import java.awt.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




public class DateTimeUtil {
	private static SimpleDateFormat  sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static Map<String, Object> getTwoDay(String sj1, String sj2) throws ParseException {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		java.util.Date endDate = myFormatter.parse(sj1);
		java.util.Date nowDate = myFormatter.parse(sj2);
		/// 获得两个时间的毫秒时间差异
	    long diff = endDate.getTime() - nowDate.getTime();
	    System.out.println(diff);
	    // 计算差多少天
	    long day = diff /1000/60/60/24;
	    // 计算差多少小时
	    long hour = diff /1000/60/60;
	    // 计算差多少分钟
	    long min = diff /1000/60;
	    // 计算差多少秒
	    long sec =diff/1000;
	     System.out.println(day+"--"+hour+"--"+min+"--"+sec);
	 	Map<String, Object> map = new HashMap<>();
	 	map.put("day", day);
	 	map.put("hour", hour);
	 	map.put("min", min);
	 	map.put("sec", sec);
		return map;
		}
	
	/**
	 * 获取前N个月的1号日期
	 * @param int1
	 * @return
	 */
	public static String getMonth(int int1) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		 Calendar c = Calendar.getInstance();
		 c.setTime(new Date());
		 c.add(Calendar.MONTH, int1);
		 Date m = c.getTime();
		 String mon = format.format(m);
		 String FirstMonth = mon+"-01";
		 return FirstMonth;
	}
	
	/**
	 * yyyy-MM-dd HH:mm 字符串时间格式转换为秒 
	 * @param str
	 * @return
	 */
	public static long str2second(String str){
		Calendar cal = Calendar.getInstance();
		if(str != null){
			try {
				cal.setTime(sdf3.parse(str));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return cal.getTimeInMillis()/1000;
	}
	
	/**
	 * 获取当前日期
	 * @return
	 */
	public static String nowDay(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String day = dateFormat.format(date);
		
		return day;
	}
	/**
	 * 
	 * @return
	 */
	public static Date getDay(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		
		return date;
	}
	
	
	/**
	 * 获取当前日期yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String nowDayhms(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String day = dateFormat.format(date);
		
		return day;
	}
	/**
	 * 获取当前时间的下一天的0点
	 * @param NowTime
	 * @return
	 */
	public static String GetStartTimeOfTheNextDay(String NowTime){
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		 Date d = TimeUtil.str2date(NowTime);
		calendar.setTime(d);
		calendar.add(Calendar.DATE, 1);
		 Date date = calendar.getTime();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				0, 0, 0);
		Date beginOfDate = calendar.getTime();
		String time = dateformat1.format(beginOfDate);
		
		return time;
	}
	/**
	 * 获取当前时间的0点
	 * @param NowTime
	 * @return
	 */
	public static String GetStartTimeOfTheDay(String NowTime){
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		 Date d = TimeUtil.str2date(NowTime);
		calendar.setTime(d);
		 Date date = calendar.getTime();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				0, 0, 0);
		Date beginOfDate = calendar.getTime();
		String time = dateformat1.format(beginOfDate);
		
		return time;
	}
	/**
	 * 判断两个时间是否为同一天的
	 * @param Time
	 * @param Time2
	 * @return
	 */
	public static boolean JudgeWhetherSameDay(String Time, String Time2) {
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		Date time1 = TimeUtil.str2date(Time);
		Date time2 = TimeUtil.str2date(Time2);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(time1);
		c2.setTime(time2);
		boolean Result = (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
		return Result;

	}
	/**
	 * 获取两个时间直接的所有天数
	 * @param StartTime
	 * @param EndTime
	 * @return
	 * @throws ParseException
	 */
	public static ArrayList DisplayDateEveryday(String StartTime, String EndTime) throws ParseException {
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         Date dateOne = dateFormat.parse(StartTime);
         Date dateTwo = dateFormat.parse(EndTime);
         Calendar calendar = Calendar.getInstance();
          ArrayList list = new ArrayList<>();
         calendar.setTime(dateOne);
         int i = 0;
         while(calendar.getTime().before(dateTwo)){
        	  String Day = dateFormat.format(calendar.getTime());
             //System.out.println(dateFormat.format(calendar.getTime()));
             list.add(i, Day);
             calendar.add(Calendar.DAY_OF_MONTH, 1); 
             i = i+1;
         }
         String[] strArray = EndTime.split("-");
			String year = strArray[0];
			String month = strArray[1];
			String days = strArray[2];
			String[] strdayArray = days.split(" ");
			String day = strdayArray[0];
			String Day = year+"-"+month+"-"+day;
			 list.add(i, Day);
         System.out.println(list);
		return list;

	}
	
	/**
	 * 获取当月第一天
	 * @return
	 * @throws ParseException
	 */
	public static String getSameMonthFirstDay() throws ParseException {
		Calendar cale = null;
	    cale = Calendar.getInstance();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    String firstday, lastday;
	    // 获取本月的第一天
	    cale = Calendar.getInstance();
	    cale.add(Calendar.MONTH, 0);
	    cale.set(Calendar.DAY_OF_MONTH, 1);
	    firstday = format.format(cale.getTime());
		return firstday;

	}
	
	/**
	 * 获取当月最后一天
	 * @return
	 * @throws ParseException
	 */
	public static String getSameMonthLastDay() throws ParseException {
		Calendar cale = null;
	    cale = Calendar.getInstance();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
	    String firstday, lastday;
	    // 获取本月的最后一天
	    cale = Calendar.getInstance();
	    cale.add(Calendar.MONTH, 1);
	    cale.set(Calendar.DAY_OF_MONTH, 0);
	    lastday = format.format(cale.getTime());
		return lastday;

	}
	/**
	 * 时间戳转日期
	 * @param seconds
	 * @param format
	 * @return
	 */
	public static String timeStamp2Date(String seconds,String format) {  
		         if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
		             return "";  
		         }  
		         if(format == null || format.isEmpty()){
		             format = "yyyy-MM-dd HH:mm:ss";
		         }   
		         SimpleDateFormat sdf = new SimpleDateFormat(format);  
		         return sdf.format(new Date(Long.valueOf(seconds+"000")));  
		     } 
	
	public static Integer intervalMonth(String startTime, String endTime) {
		Date s = TimeUtil.str2date(startTime);
		Date e = TimeUtil.str2date(endTime);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(s);
		c2.setTime(e);
		int year1 = c1.get(Calendar.YEAR);
		int year2 = c2.get(Calendar.YEAR);
		int month1 = c1.get(Calendar.MONTH);
		int month2 = c2.get(Calendar.MONTH);
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH);
		System.out.println(year1);
		System.out.println(year2);
		System.out.println(month1);
		System.out.println(month2);
		int year = year2 - year1;
		// int month = month2 -month1;
		int month = 0;
		if (month1 == month2) {
			month = 0;
		} else if (month1 > month2) {
			month = month1 - month2;
		} else if (month2 > month1) {
			month = month2 - month1;
		}

		int intervalMonth = year * 12 + month;

		return intervalMonth;
	}
	
	
}
