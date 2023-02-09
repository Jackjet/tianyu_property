package com.vguang.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 企业密码hash转换
 * 
 * @author wang date 2017-04-11
 */
public class SHAUtil {

	/**
	 * 
	 * @param strText
	 *            hash前字符串
	 * @param strType
	 *            hash类型：SHA-256/SHA-512
	 * @return
	 */
	public static String SHA(final String strText, final String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 类型结果
				byte byteBuffer[] = messageDigest.digest();

				// 將 byte转换为string
				StringBuffer strHexString = new StringBuffer();
				// 遍历 byte buffer
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}
}
