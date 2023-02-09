package com.vguang.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.rmi.runtime.Log;

/**
 * 工具类：拼接字符串、打包字符串
 * 
 * @author wang date 2017-03-20 modify 2017-04-17
 */
public class StringUtil {
	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
	//随机码基数
	private static final String strs = "abcdefghijkmnpqrstuvwxyABCDEFGHJKLMNPQRSTUVWXY3456789";
	/**
	 * 拼接二维码字符串
	 * @param params
	 * @return
	 */
	public static String concatStr(String symbol, String... params) {
		StringBuilder content = new StringBuilder();
		// //1、拼接员工信息
		// if(sym == 1){
		// content.append(MJ).append(VERSION);
		// }else{
		// content.append("$dev");
		// }

		for (String str : params) {
			content.append(str).append("@");
		}

		return content.append("6").toString();
	}
	public static String concatVg(String symbol, String... params) {
		StringBuilder content = new StringBuilder(symbol);
		for (String str : params) {
			content.append("&").append(str);
		}

		return content.toString();
	}

	/**
	 * 求两个Integer数组的差集
	 * 20170603 求两个String数组的差集
	 * @param codes1
	 * @param codes
	 * @return
	 */
	public static String[] minus(String[] codes1, String[] codes) {
		LinkedList<String> list = new LinkedList<String>();
		LinkedList<String> history = new LinkedList<String>();
		String[] longerArr = codes1;
		String[] shorterArr = codes;
		// 找出较长的数组来减较短的数组
		if (codes.length > codes1.length) {
			longerArr = codes;
			shorterArr = codes1;
		}
		for (String str : longerArr) {
			if (!list.contains(str)) {
				list.add(str);
			}
		}
		for (String str : shorterArr) {
			if (list.contains(str)) {
				history.add(str);
				list.remove(str);
			} else {
				if (!history.contains(str)) {
					list.add(str);
				}
			}
		}
		String[] result = {};
		return list.toArray(result);
	}
	
	/**
	 * 求两个数组的交集   
	 * @param arr1
	 * @param arr2
	 * @return
	 */
    public static String[] intersect(String[] arr1, String[] arr2) {   
        Map<String, Boolean> map = new HashMap<String, Boolean>();   
        LinkedList<String> list = new LinkedList<String>();   
        for (String str : arr1) {   
            if (!map.containsKey(str)) {   
                map.put(str, Boolean.FALSE);   
            }   
        }   
        for (String str : arr2) {   
            if (map.containsKey(str)) {   
                map.put(str, Boolean.TRUE);   
            }   
        }   
  
        for (Entry<String, Boolean> e : map.entrySet()) {   
            if (e.getValue().equals(Boolean.TRUE)) {   
                list.add(e.getKey());   
            }   
        }   
  
        String[] result = {};   
        return list.toArray(result);   
    }   
	
	/**
	 * 生成随机字符串，排除l/I/oO/0/1/2/zZ
	 * @param count
	 * @return
	 */
	public static String randStrs(int count){
		String str = null;
		if(count > 0){
			str = RandomStringUtils.random(count, strs);
		}
		
		return str;
	}
	
	/**
	 * 生成UUID
	 */
	public static String getUniqueNonce() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	/**
	 * 判空条件：str==null
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		if(null == str){
			return true;
		}
		return false;
	}
	
	/**
	 * 判空条件：null == str || "".equals(str) 
	 * @param str
	 * @return
	 */
	public static boolean isNullAndEmpty(String str){
		if(null == str || "".equals(str)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判空条件：null == obj || "".equals(obj) 
	 * @param str
	 * @return
	 */
	public static boolean isNullAndEmpty_Obj(Object obj){
		if(null == obj || "".equals(obj)){
			return true;
		}
		return false;
	}
	
	public static String logStr(String str) {
		return "==============" + str;
	}
	public static String getHhmlPath(String path) {
		return path.substring(path.indexOf("html")+5);
	}
	/**
	 * 密钥 十进制转十六进制
	 * 
	 * @param value
	 * @return
	 */
	public static String dec2Hex(byte[] dec) {
		StringBuffer sbu = new StringBuffer();
		for (int i = 0; i < dec.length; i++) {
			String str = String.format("%2s", Integer.toHexString(dec[i])).replace(' ', '0');
			sbu.append(str);
		}
		return sbu.toString();
	}
	/**
	 * 密钥 byte数组转long
	 * 
	 * @param bs
	 * @return
	 */
	public static long byte2Long(byte[] bs) {
		long s = 0;
		long s0 = bs[0] & 0xff;// 最低位
		long s1 = bs[1] & 0xff;
		long s2 = bs[2] & 0xff;
		long s3 = bs[3] & 0xff;
		long s4 = bs[4] & 0xff;// 最低位
		long s5 = bs[5] & 0xff;
		long s6 = bs[6] & 0xff;
		long s7 = bs[7] & 0xff;

		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s7 | s6 | s5 | s4 | s3 | s2 | s1 | s0;
		return s;
	}
	
	/**
	 * 获取4位随机数
	 * @return
	 */
	public static int random() {
		int b= (int) ((Math.random()*9+1)*1000);
		
		return  b;
	}

	/**
	 * 验证字符串是否为16进制
	 * @param str
	 * @return
	 */
	public static boolean isHexNumber(String str) {
		boolean flag = true;
		for (int i = 0; i < str.length(); i++) {
			String cc = str.substring(i, i + 1);
			if (!cc.equals("0") && !cc.equals("1") && !cc.equals("2") && !cc.equals("3") && !cc.equals("4")
					&& !cc.equals("5") && !cc.equals("6") && !cc.equals("7") && !cc.equals("8") && !cc.equals("9")
					&& !cc.equals("a") && !cc.equals("b") && !cc.equals("c") && !cc.equals("d") && !cc.equals("e")
					&& !cc.equals("f") && !cc.equals("A") && !cc.equals("B") && !cc.equals("C") && !cc.equals("D")
					&& !cc.equals("E") && !cc.equals("F")) {
				flag = false;
				return flag;
			}
		}
		return flag;
	}
	
	public static String objToString(Object obj) {
		String str = null;
		
		if (null != obj && !"".equals(obj)) {
			str = obj.toString();
		}
		return str;
	}
	
	public static Integer ObjToInteger(Object obj) {
		Integer i = null;
		if (null != obj && !"".equals(obj)) {
			i = Integer.valueOf(obj.toString());
		}
		return i;
	}
	
	/**
	 * 判断是否为整数
	 * @param str
	 * @return
	 */
	 public static boolean isInteger(String str) {    
		    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
		    return pattern.matcher(str).matches();    
		  }
}

