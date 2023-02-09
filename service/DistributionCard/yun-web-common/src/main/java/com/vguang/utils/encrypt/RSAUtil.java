package com.vguang.utils.encrypt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 对扫码字符串进行RSA加密
 * 
 * @author wang date 2017-03-17 
 * modify 2017-03-20
 */
@SuppressWarnings("unused")
public class RSAUtil {

	// 加密算法RSA
	public static final String KEY_ALGORITHM = "RSA";
	// 签名算法
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	// // 获取公钥的key
	// private static final String PUBLIC_KEY = "RSAPublicKey";
	// // 获取私钥的key
	// private static final String PRIVATE_KEY = "RSAPrivateKey";
	// RSA最大加密明文大小
	private static final int MAX_ENCRYPT_BLOCK = 117;
	// RSA最大解密密文大小
	private static final int MAX_DECRYPT_BLOCK = 128;

	private static RSAPublicKey rsaPubKey = null;
	//密钥存放路径
	private static String filePath = "/etc/qcloud/keymall/";

	/**
	 * 生成密钥对(公钥和私钥) 并存储在本地
	 */
	public static void genKeyPair(String filePath, String keyName) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 将密钥对写入到文件
		String pubKey = B64Util.encode(publicKey.getEncoded());
		String priKey = B64Util.encode(privateKey.getEncoded());
		FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
		FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
		BufferedWriter pubbw = new BufferedWriter(pubfw);
		BufferedWriter pribw = new BufferedWriter(prifw);
		pubbw.write(pubKey);
		pribw.write(priKey);
		pubbw.flush();
		pubbw.close();
		pubfw.close();
		pribw.flush();
		pribw.close();
		prifw.close();
	}
	public static void genKeyPair(String keyName) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 将密钥对写入到文件
		String pubKey = B64Util.encode(publicKey.getEncoded());
		String priKey = B64Util.encode(privateKey.getEncoded());
		FileWriter pubfw = new FileWriter(filePath + keyName + "pub.keystore");
		FileWriter prifw = new FileWriter(filePath + keyName + "pri.keystore");
		BufferedWriter pubbw = new BufferedWriter(pubfw);
		BufferedWriter pribw = new BufferedWriter(prifw);
		pubbw.write(pubKey);
		pribw.write(priKey);
		pubbw.flush();
		pubbw.close();
		pubfw.close();
		pribw.flush();
		pribw.close();
		prifw.close();
	}

	private static String loadPublicKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "/publicKey.keystore"));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}

	public static void loadPublicKeyByStr(String path) throws Exception {
		try {
			String storePubKey = loadPublicKeyByFile(path);
			System.out.println("公钥" + storePubKey);
			byte[] buffer = B64Util.decode(storePubKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			rsaPubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}
	
	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data 已加密数据
	 * @param privateKey 私钥(BASE64编码)
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = B64Util.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return B64Util.encode(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data 已加密数据
	 * @param publicKey 公钥(BASE64编码)
	 * @param sign 数字签名
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = B64Util.decode(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(B64Util.decode(sign));
	}
	
	/**
	 * 公钥加密
	 * @param data 源数据
	 * @param publicKey 公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = B64Util.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * v0.4对内容进行加密,返回字符串
	 */
	public static String enData(String content) {
		// RSAPublicKey pubKey = null;
		byte[] cipherData = null;
		String bconstr = null;
		try {
			cipherData = RSAUtil.encryptByPublicKey(content.getBytes(), B64Util.encode(rsaPubKey.getEncoded()));
			bconstr = B64Util.encode(cipherData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String constr = bconstr;

		return constr;
	}
	//RSA加密生成二进制数组
	public static byte[] enDataToBytes(String content) {
		byte[] cipherData = null;
		try {
			cipherData = RSAUtil.encryptByPublicKey(content.getBytes(), B64Util.encode(rsaPubKey.getEncoded()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cipherData;
	}

	/***************************************************************************************************************
    
    //加载私钥
	public static String loadPrivateKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "/privateKey.keystore"));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
		try {
			byte[] buffer = B64Util.decode(privateKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	//私钥解密
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
		byte[] keyBytes = B64Util.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	// 公钥解密
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
		byte[] keyBytes = B64Util.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}
	
	// 私钥加密
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = B64Util.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}
	
	***********************************************************************************************************/

}
