package com.vguang.utils.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;

/**
 * 对字符进行AES解密
 * 
 * @author wang date 2017-03-17 
 * modify 2017-03-23 改用AES加密
 * 2017-04-14 枚举重构
 */
public class AESUtil {
	private static final Logger log = LoggerFactory.getLogger(AESUtil.class);
	private static final String WAYS = "AES";
	private static final String FORMAT = "utf-8";
	// AES-128
	private static final String PKCS5 = "AES/CBC/PKCS5Padding";   //"PKCS5Padding";
	private static final String PKCS7 = "AES/CBC/PKCS7Padding";

	public static boolean initialized = false;

	/**
	 * AES-128-CBC解密:数据、密钥、向量原始数据全部使用了Base64
	 * @param data
	 * @param key
	 * @param iv
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static String decryptWithBase64(String data, String key, String iv) throws InvalidAlgorithmParameterException {
		initialize();

		byte[] databytes = B64Util.decode(data),
				keybytes = B64Util.decode(key),
				ivbytes = B64Util.decode(iv);

		return decryptNoBase64(databytes, keybytes, ivbytes);
	}
	
	/**
	 * AES-128-CBC解密:数据、密钥、向量原始数据全部未使用Base64
	 * @param data
	 * @param key
	 * @param iv
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static String decryptNoBase64(byte[] databytes, byte[] keybytes, byte[] ivbytes) throws InvalidAlgorithmParameterException {
		initialize();

		try {
			Cipher cipher = Cipher.getInstance(PKCS5);
			Key sKeySpec = new SecretKeySpec(keybytes, WAYS);
			// 初始化解码类Cipher
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivbytes));
			byte[] resultByte = cipher.doFinal(databytes);

			String info = null;
			if (null != resultByte && resultByte.length > 0) {
				// 删除解密后字节resultByte的补位字符
				info = new String(WxEncoder.decode(resultByte));
			}
			
			return info;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void initialize() {
		if (initialized)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialized = true;
	}

	// 生成iv
	private static AlgorithmParameters generateIV(byte[] iv) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance(WAYS);
		params.init(new IvParameterSpec(iv));
		return params;
	}
	
	/**********************************************加密二维码内容****************************************************/

	/**
	 * 判断加密密钥、向量合法性
	 * @param key
	 * @return
	 */
	private static boolean isValid(String key){
		if(null != key && key.length() == 16){
			return true;
		}
		
		return false;
	}
	
	/**
	 * AES-128-CBC加密
	 * @param sSrc
	 * @return Base64加密后的字符串
	 * @throws Exception
	 */
	public static String cbcEncrypt(String sSrc) {
		SystemConfigs sys = (SystemConfigs) SpringContextUtil.getBean("sysConfig");
		String key = sys.getValue(SystemConfigs.AES_KEY);
		String iv = sys.getValue(SystemConfigs.AES_IV);
		
		return cbcEncryptToStr(sSrc, key, iv);
	}
	
	/**
	 * AES-128-CBC加密:返回加密后的字符串
	 * @param sSrc 原始字符串,未使用Base64加密
	 * @param key 原始字符串,未使用Base64加密
	 * @param iv 原始字符串,未使用Base64加密
	 * @return Base64加密后的字符串
	 */
	public static String cbcEncryptToStr(String sSrc,String key,String iv) {
		if(isValid(key) && isValid(iv)){
			byte[] encryptbytes = cbcEncryptToBytes(sSrc, key, iv);
			
			if(null != encryptbytes){
				return B64Util.encode(encryptbytes);
			}
		}else{
			log.info("----公司密钥不合法----");
		}
		return null;
	}

	/**
	 * AES-128-CBC加密:返回加密后的二进制字节数组
	 * @param sSrc 原始字符串,未使用Base64加密
	 * @param key 原始字符串,未使用Base64加密
	 * @param iv 原始字符串,未使用Base64加密
	 * @return
	 */
	public static byte[] cbcEncryptToBytes(String sSrc,String key,String iv) {
		log.info("加密前：{}", sSrc);
		// 模式：例如AES/ECB/PKCS5Padding AES/CBC/PKCS7Padding
		try {
			Cipher cipher = Cipher.getInstance(PKCS5);
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), WAYS);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, generateIV(iv.getBytes()));
			byte[] encrypted = cipher.doFinal(sSrc.getBytes(FORMAT));

			if (null != encrypted) {
				return encrypted;
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	
}
