package com.vguang.utils.encrypt;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author wangsir
 *
 *         2017年9月21日
 */
public class HMACUtil {
	private static final String HMACMD5 = "HmacMD5";
	private static final String HMACSHA1 = "HmacSHA1";
	private static final String CHARSET_UTF8 = "UTF-8";
	/** 密钥 **/
	private static final String MAC_KEY = "abcdef";

	/**
	 * 计算待签名字符串的 HMAC-MD5 值
	 * @param bytes
	 * @param key
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] encrypt(byte[] bytes, String key) throws Exception {
		if (isEmpty(key)) {
			throw new IOException("secret can not be empty");
		}
		
		SecretKey skey = new SecretKeySpec(key.getBytes("UTF-8"), HMACMD5);
		Mac mac = Mac.getInstance(HMACMD5);
		mac.init(skey);
		mac.update(bytes);

		return mac.doFinal();
	}

	/**
	 * 计算待签名字符串的 HMAC-SHA1 值
	 */
	public static byte[] hmacSHA1Signature(String key, String baseString) throws Exception {
		if (isEmpty(key)) {
			throw new IOException("secret can not be empty");
		}
		if (isEmpty(baseString)) {
			return null;
		}
		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_UTF8), HMACSHA1);
		mac.init(keySpec);
		
		return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
	}

	private static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	
	/**
	 * 计算待签名字符串的 HMAC-MD5 值（Key是16进制字符串时使用）
	 * @param bytes
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt1(byte[] bytes, String key) throws Exception {
		if (isEmpty(key)) {
			throw new IOException("secret can not be empty");
		}
		 int m = 0, n = 0;
		int l = key.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + key.substring(i * 2, m) + key.substring(m, n));
		}
		SecretKey skey = new SecretKeySpec(ret, HMACMD5);
		Mac mac = Mac.getInstance(HMACMD5);
		mac.init(skey);
		mac.update(bytes);

		return mac.doFinal();
	}
//	 private static int toByte(char c) {
//		    byte b = (byte) "0123456789ABCDEF".indexOf(c);
//		    return b;
//		 }
}
