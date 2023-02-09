package com.vguang.utils.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util {
	private static final Logger log = LoggerFactory.getLogger(MD5Util.class);
	
	/**
	 * 按照format编码格式将字符串转为字节数组,默认为UTF-8编码格式
	 * @param source
	 * @return
	 */
	public static byte[] str2bytes(String source, String format){
		byte[] bytes = null;
		if(null != source){
			if(null == format){
				try {
					bytes = source.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				try {
					bytes = source.getBytes(format);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		return bytes;
	}
	/**
	 * 将源字节数组使用MD5加密为字节数组
	 * 
	 * @param source
	 * @return
	 */
	public static byte[] encode2bytes(byte[] sbytes) {
		byte[] result = null;
		
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(sbytes);
			result = md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 字节数组转为十六进制字符串
	 */
	public static String byte2hex(byte[] bytes){
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xff & bytes[i]);

			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
	/**
	 * 将源字符串使用MD5加密为32位16进制数
	 * @param source
	 * @return
	 */
	public static String encode2hex(String source) {
		//MD5加密
		byte[] data = encode2bytes(str2bytes(source, null));

		return byte2hex(data);
	}
	
	/**
	 * 将源字节流使用MD5加密为32位16进制数
	 * @param source
	 * @return
	 */
	public static String encode2hex2(byte[] bytes) {
		//MD5加密
		byte[] data = encode2bytes(bytes);

		return byte2hex(data);
	}

	/**
	 * 验证字符串是否匹配
	 * 
	 * @param unknown
	 *            待验证的字符串
	 * @param okHex
	 *            使用MD5加密过的16进制字符串
	 * @return 匹配返回true，不匹配返回false
	 */
	public static boolean validate(String unknown, String okHex) {
		return okHex.equals(encode2hex(unknown));
	}
}
