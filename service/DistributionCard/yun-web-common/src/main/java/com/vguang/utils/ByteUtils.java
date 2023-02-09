package com.vguang.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wangsir
 *
 *         2017年9月21日
 */
public class ByteUtils {
	public static byte[] hexDecode(String hexStr) {
		if (hexStr == null || "".equals(hexStr)) {
			return null;
		}
		try {
			char[] cs = hexStr.toCharArray();
			return Hex.decodeHex(cs);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String int2HexStr(Integer n) {

		String str = Integer.toHexString(n);
		while (str.length()<18){
			str = "0"+str;
		}
		return str.toUpperCase();
	}

	/**
	 * 将int转为高端字节序排列的byte数组（Java内存存放顺序）
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] int2ByteArray(int n) {
		byte[] byteArray = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			dataOut.writeInt(n);
			byteArray = byteOut.toByteArray();
			Arrays.toString(byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}

	/**
	 * 将int转为高字节在前，低字节在后的byte数组
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] int2Hbytes(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);

		return b;
	}
	
	/**
	 * 将int转为高字节在前，低字节在后的byte数组,一字节
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] int2Hbytes1byte(int n) {
		byte[] b = new byte[1];
		b[0] = (byte) (n & 0xff);
		return b;
	}

	/**
	 * 将short转为高字节在前，低字节在后的byte数组
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] short2Hbytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * 以下 是整型数 和 网络字节序的 byte[] 数组之间的转换
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] long2Hbytes(long n) {
		byte[] b = new byte[8];
		b[7] = (byte) (n & 0xff);
		b[6] = (byte) (n >> 8 & 0xff);
		b[5] = (byte) (n >> 16 & 0xff);
		b[4] = (byte) (n >> 24 & 0xff);
		b[3] = (byte) (n >> 32 & 0xff);
		b[2] = (byte) (n >> 40 & 0xff);
		b[1] = (byte) (n >> 48 & 0xff);
		b[0] = (byte) (n >> 56 & 0xff);
		return b;
	}
	public static byte[] long2H6bytes(long n) {
		byte[] b = new byte[6];
		b[5] = (byte) (n & 0xff);
		b[4] = (byte) (n >> 8 & 0xff);
		b[3] = (byte) (n >> 16 & 0xff);
		b[2] = (byte) (n >> 24 & 0xff);
		b[1] = (byte) (n >> 32 & 0xff);
		b[0] = (byte) (n >> 40 & 0xff);
		return b;
	}
	public static byte[] long2H4bytes(long n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}
	
	public static byte[] unlong2H4bytes(long n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	public static byte[] unlong2H2bytes(long n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * 合并数组
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatBytes(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			if(null != array){
				totalLength += array.length;
			}
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			if(null != array){
				System.arraycopy(array, 0, result, offset, array.length);
				offset += array.length;
			}
		}
		return result;
	}
	
	/**
	 * byte数组转为十六进制字符串
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(byte[] bytes){
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
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hex2Byte(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	/** 
	 * Convert char to byte 
	 * @param c char 
	 * @return byte 
	 */  
	 private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}
	 
	/**
	 * 输入流转为字节数组
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    while (-1 != (n = input.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    return output.toByteArray();
	}
	
	
	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
		}
		return ret;
	}
	
	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            s为待转换的ASCII字符串
	 *
	 */
	public static String str2HexStr(String s) {
		
		String str = "";
	    for (int i = 0; i < s.length(); i++) {
	        int ch = (int) s.charAt(i);
	        String s4 = Integer.toHexString(ch);
	        str = str + s4;
	    }
	    return str;
	}
	
	/**
	 * 16进制的字符串转byte数组
	 * @param src
	 * @return
	 */
	public static byte[] Str16toBytes(String src) {
		int w = 0;
		byte[] bytes_2 = new byte[src.length()/2];
		for (int i = 0; i < src.length(); i++) {
			String zz = src.substring(i, i + 2);
//			System.out.println(zz);
			byte aaa = (byte) Integer.parseInt(zz, 16);
//			System.out.println(w);
			bytes_2[w] = aaa;
//			System.out.println(ByteUtil.byte2Hex(bytes_2));
			i++;

			w = w + 1;
		}

		return bytes_2;
	}
	
	/**
	 * 16进制转10进制数字
	 * @param src
	 * @return
	 */
	public static int Str16toint10(String src) {
		int h = Integer.parseInt(src, 16);
		return h;
	}
	/**
	 * 从16进制字符串中获取2进制为1的序列号
	 * @param src
	 * @return
	 */
	public static ArrayList Str16Get1(String src) {
		byte[] b_p = ByteUtils.Str16toBytes(src);
		ArrayList list = new ArrayList<>();
				int w = 0;
		for (int i = 0; i < src.length()/2; i++) {
			for (int j = 0; j < 8; j++) {
				if ((b_p[src.length()/2-1 - i] & (1 << j)) != 0) {
					int pp = i * 8 + j + 1;
					System.out.println("pppp=======" + pp);
					list.add(w, pp);
					w=w+1;
				} else {
					
				}
			}

		}
		return list;
	}
	
	/**
	 * 异或校验
	 * @param bytes
	 * @return
	 */
	public static byte[] bytesXorCrc(byte[] bytes) {
		byte[] crc = new byte[1];// 异或校验
		crc[0] = bytes[0];
		for (int i = 1; i < bytes.length; i++) {
			crc[0] ^= bytes[i];
		}
		return crc;
	}

	public static String convertStringToHex(String str){
		char[] chars = str.toCharArray();
		StringBuffer hex = new StringBuffer();
		for(int i = 0; i < chars.length; i++){
			hex.append(Integer.toHexString((int)chars[i]));
		}
		return hex.toString();
	}

	public static String convertHexToString(String hex){

		hex = hex.replace(" ","");
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		//49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for( int i=0; i<hex.length()-1; i+=2 ){

			//grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			//convert the decimal to character
			sb.append((char)decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}
	public static byte[] hexStringToByteArray(String s) {
		//十六进制转byte数组
		int len = s.length();
		byte[] bs = new byte[len/2];
		for(int i = 0;i < len;i+=2) {
			bs[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}
		return bs;
	}
	public static String decimalToBinary(int num, int size) {
		if (size <(Integer.SIZE - Integer.numberOfLeadingZeros(num))) {
			throw  new RuntimeException("传入size小于num二进制位数");
		}
		StringBuilder binStr = new StringBuilder();
		for(int i = size-1;i >= 0; i--){
			binStr.append(num >>> i & 1);
		}
		return binStr.toString();
	}
	public static long binary2To10(String binary){
		//先补足64位进行计算
		StringBuffer sb = new StringBuffer(binary);
		for(int i = 0; i < (64 - binary.length());i++){
			sb.insert(0, "0");
		}
		char[] in = sb.toString().toCharArray();
		char[] usea = new char[in.length];
		//首位等于1则为负数,需要取反
		if(in[0] == '1'){
			for (int i=0; i<in.length; i++){
				if(in[i] == '1'){
					usea[i] = '0';
				}else{
					usea[i] = '1';
				}
			}
		}else{
			usea = in;
		}
		long count = 0;
		for (int i=0; i<usea.length; i++){
			count += (long) ((int)((int) usea[i] - (int) ('0')) * Math.pow(2, (usea.length - 1) - i));
		}
		//首位等于1则为负数, 需要加1再取负数
		if(in[0] == '1'){
			count = -(count+1);
		}
		return count;
	}
	public static byte[] long2Hbytes1byte(long n) {
		byte[] b = new byte[1];
		b[0] = (byte) (n & 0xff);
		return b;
	}
}
