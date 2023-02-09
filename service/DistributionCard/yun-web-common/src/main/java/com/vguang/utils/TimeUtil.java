package com.vguang.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间格式转换、计算
 * @author wang
 * date 2017-04-11
 */
public class TimeUtil {
	//private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);
	private static SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat  sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat  sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat  sdf4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/********************************************获取*****************************************/
	/**
	 * 获取当前系统时间
	 * 类型 timestamp
	 * @return
	 */
	public static Timestamp getCurTime(){
		long times = System.currentTimeMillis();
		Timestamp ts = long2Timestamp(times);
		
		return ts;
	}
	
	/**
	 * 获取当月1号日期
	 * @return
	 */
	public static String getCurMonthFirstDay(){
		Calendar a = Calendar.getInstance();  
	    a.set(Calendar.DATE, 1);	//把日期设置为当月第一天  
		
		return sdf2.format(a.getTime());
	}
	/**
	 * 获取上一月份天数
	 * @return
	 */
	public static int getCurMonthDays(){
		Calendar a = Calendar.getInstance();  
		a.add(Calendar.MONTH, -1);
	    a.set(Calendar.DATE, 1);	//把日期设置为当月第一天  
	    a.roll(Calendar.DATE, -1);	//日期回滚一天，也就是最后一天  
	    int maxDate = a.get(Calendar.DATE);  
	    return maxDate;  
	}
	
	/**
	 * 获得当前月份
	 * @return
	 */
	public static int getCurMonth(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		
        return cal.get(Calendar.MONTH);
	}
	
	/**
	 * 获取日期当天星期几,Calendar的星期从sunday起始为1
	 * @param ts
	 * @return
	 */
	public static int getDayOfWeek(Timestamp ts){
		if(ts == null){
			
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(ts.getTime()));
			
			return cal.get(Calendar.DAY_OF_WEEK) - 1;
		}
		
		return 0;
	}
	
	/**
	 * 当月的第几天,Calendar的天数起始为1
	 * @param ts
	 * @return
	 */
	public static int getDayOfMonth(Timestamp ts) {
		if(null == ts){
			
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(ts.getTime()));
			
			return cal.get(Calendar.DAY_OF_MONTH);
		}
		
		return 0;
	}
	
	/**
	 * 获取当天的开始时间
	 * @return
	 */
	public static Timestamp getBeginDayOfCur() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return new Timestamp(cal.getTime().getTime());
	}
	/**
	 * 获取明天的开始时间
	 * @return
	 */
	public static Timestamp getBeginDayOfTomorrow() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getBeginDayOfCur());
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return new Timestamp(cal.getTime().getTime());
	}
	 

	/******************************************转换*******************************************/
	/**
	 * 毫秒转Timestamp
	 * 类型 timestamp
	 * @return
	 */
	public static Timestamp long2Timestamp(Long times){
		Timestamp ts = new Timestamp(times);
		
		return ts;
	}
	
	/**
	 * 字符串(yyyy-MM-dd HH:mm:ss)转时间戳
	 * @param str
	 * @return
	 */
	public static Timestamp str2timestamp(String str){
		Timestamp time = null;
		if(null == str){
			return null;
		}else{
			Date date = null;
			try {
				date = sdf.parse(str);
				time = new Timestamp(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return time;
	}
	
	/**
	 * 日期转字符串(yyyy-MM-dd HH:mm:ss)
	 * @param time
	 * @return
	 */
	public static String createTime(Date time){
		if(time != null){
			return sdf.format(time);
		}
		return null;
	}
	
	/**
	 * 字符串(yyyy-MM-dd HH:mm:ss)转时间Date
	 */
	public static Date str2date(String str){
		if(str != null){
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 秒转换为日期格式(yyyy-MM-dd HH:mm:ss)
	 */
	public static String second2str(long time){
		if(time > 0){
			return sdf.format(time*1000);
		}
		return null;
	}
	
	/**
	 * 毫秒转换为日期格式
	 */
	public static String msecond2str(long time){
		if(time > 0){
			return sdf.format(time);
		}
		return null;
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss 字符串时间格式转换为秒   
	 * @throws ParseException 
	 */
	public static String str2str(String str){
		Calendar cal = Calendar.getInstance();
		long time = 0L;
		if(str != null){
			try {
				cal.setTime(sdf.parse(str));
				time= cal.getTimeInMillis()/1000;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf(time);
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
	 * 时间戳转为标准UTC时间字符串(yyyy-MM-dd'T'HH:mm:ss'Z')
	 */
	public static String getISO8601Time(Timestamp ts) {
//		Date nowDate = new Date();
		sdf4.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return sdf4.format(ts);
	}
	
	
	/**
	 * 时间戳转为标准UTC时间字符串(yyyy-MM-dd'T'HH:mm:ss'Z')
	 */
	public static String getISO8601TimeBeiJing(Timestamp ts) {
//		Date nowDate = new Date();
		sdf4.setTimeZone(TimeZone.getTimeZone("GMT+08"));
		return sdf4.format(ts);
	}
	
	/**
	 * 标准UTC时间字符串(yyyy-MM-dd'T'HH:mm:ss'Z')转为时间戳
	 * @param ts
	 * @return
	 */
	public static Timestamp strISO2Time(String str) {
		Timestamp time = null;
		Date date = new Date();
		sdf4.setTimeZone(new SimpleTimeZone(0, "GMT"));
		if(null != str){
			try {
				date = sdf4.parse(str);
				time = new Timestamp(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return time;
	}
	
	/**
	 * 标准UTC时间字符串(yyyy-MM-dd'T'HH:mm:ss'Z')转为时间戳,抛出异常版本
	 * @param ts
	 * @return
	 * @throws ParseException 
	 */
	public static Timestamp strISO2TimeException(String str) throws ParseException {
		Timestamp time = null;
		Date date = new Date();
		sdf4.setTimeZone(new SimpleTimeZone(0, "GMT"));
		if(null != str){
				date = sdf4.parse(str);
				time = new Timestamp(date.getTime());
		}
		
		return time;
	}
	
	/**
	 * 标准UTC时间字符串(yyyy-MM-dd'T'HH:mm:ss'Z')转为毫秒
	 * @param ts
	 * @return
	 */
	public static long strISO2Second(String str) {
		Timestamp time = strISO2Time(str);
		
		return time.getTime();
	}
	
	/**
	 * UTC--毫秒，北京时间
	 * 
	 * @param str
	 * @return
	 */
	public static long strISO2BeijingSecond(String str) {
		return strISO2Second(str) - 8 * 60 * 60 * 1000;

	}
	
	
	/********************************************增减比较****************************************/
	/**
	 * 对日期添加月数
	 * @param time
	 * @param count
	 * @return
	 */
	public static String calTime(Date time, int count){
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.MONTH, count);
		
		return sdf2.format(cal.getTime());
	}
	
	/**
	 * 比较两个时间的大小
	 * @throws ParseException 
	 */
	public static boolean compareDate(String date1,String date2){
		if(date1 != null && date2 != null){
			Date d1 = null,d2 = null;
			try {
				d1 = sdf2.parse(date1);
				d2 = sdf2.parse(date2);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(d1.getTime() > d2.getTime()){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 比较时间戳时分秒
	 * begin = end : 0
	 * begin < end : -1
	 * begin > end : 1
	 */
	public static int compareTo(Timestamp begin, Timestamp end){
		if(begin == null || end == null){
			
		}else{
			Calendar cal = Calendar.getInstance();
			cal.set(0, 0, 0, begin.getHours(), begin.getMinutes(), begin.getSeconds());
			
			Calendar cal2 = Calendar.getInstance();
			cal2.set(0, 0, 0, end.getHours(), end.getMinutes(), end.getSeconds());
			
			//log.info("时分秒比较:{}", cal2.compareTo(cal));
			return cal2.compareTo(cal);
		}
		
		return 0;
	}
	
	/**
	 * 两个日期相隔的天数
	 * @param begin
	 * @param end
	 * @return
	 */
	public static long getDaySpace(Timestamp begin, Timestamp end){
		if(begin == null || end == null){
			
		}else{
			
			return (end.getTime() - begin.getTime())/(24*60*60*1000);
		}
		
		return 0;
	}
	
	/**
	 * 两个日期相隔的周数
	 * @param begin
	 * @param cur
	 * @return
	 */
	public static long getWeekSpace(Timestamp begin, Timestamp end) {
		if(begin == null || end == null){
			
		}else{
			
			return (end.getTime() - begin.getTime())/(7*24*60*60*1000);
		}
		
		return 0;
	}
	
	/**
	 * 两个日期相隔的月数
	 * @param ts
	 * @return
	 */
	public static int getMonthSpace(Timestamp begin, Timestamp end) {
		if(begin == null || end == null){
			
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(begin.getTime()));
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(new Date(end.getTime()));
			
			int months = (cal2.get(Calendar.YEAR) - cal.get(Calendar.YEAR))*12 + (cal2.get(Calendar.MONTH) - cal.get(Calendar.MONTH));
					
			return months;
		}
		
		return 0;
	}
	
	/**
	 * 获取当月的所有周六  周日
	 * @param year
	 * @param month
	 * @return
	 */
	public static Map getWeekendInMonth(int year, int month) {
		Map<String, Object> map = new HashMap<String, Object>();
		List SaturdayDatelist = new ArrayList();
		 List SundayDatelist = new ArrayList();
		 List Saturdayslist = new ArrayList();
		 List Sundayslist = new ArrayList();
		    Calendar calendar = Calendar.getInstance();
		    calendar.set(Calendar.YEAR, year);// 不设置的话默认为当年
		    calendar.set(Calendar.MONTH, month - 1);// 设置月份
		    calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为当月第一天
		    int daySize = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);// 当月最大天数
		    for (int i = 0; i < daySize-1; i++) {
		        calendar.add(Calendar.DATE, 1);//在第一天的基础上加1
		        int week = calendar.get(Calendar.DAY_OF_WEEK);
		        if(week == Calendar.SATURDAY) {
		        	System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
		        	SaturdayDatelist.add(year+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH));
		        	Saturdayslist.add(calendar.get(Calendar.DAY_OF_MONTH));
		        }
		        if(week == Calendar.SUNDAY) {
		        	System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
		        	SundayDatelist.add(year+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH));
		        	Sundayslist.add(calendar.get(Calendar.DAY_OF_MONTH));
		        }
		     }
		map.put("SaturdayDatelist", SaturdayDatelist);
		map.put("SundayDatelist", SundayDatelist);
		map.put("Saturdayslist", Saturdayslist);
		map.put("Sundayslist", Sundayslist);
		return map;
	}
	
	/**
	 * 获取当月天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static Integer getDaysByYearMonth(int year, int month) {
	 Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		System.out.println(maxDate);
		return maxDate;
	}
	
	/**
	 * 获取俩个日期的年月日
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }
	/**
	 * 验证时间格式
	 * @param timeStr
	 * @return
	 */
	public static boolean valiDateTimeWithLongFormat(String timeStr) {
		String format = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
				+ "([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(timeStr);
		if (matcher.matches()) {
			pattern = Pattern.compile("(\\d{4})-(\\d+)-(\\d+).*");
			matcher = pattern.matcher(timeStr);
			if (matcher.matches()) {
				int y = Integer.valueOf(matcher.group(1));
				int m = Integer.valueOf(matcher.group(2));
				int d = Integer.valueOf(matcher.group(3));
				if (d > 28) {
					Calendar c = Calendar.getInstance();
					c.set(y, m-1, 1);
					int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
					return (lastDay >= d);
				}
			}
			return true;
		}
		return false;
	}

}
