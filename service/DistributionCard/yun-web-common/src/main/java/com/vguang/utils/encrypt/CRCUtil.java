package com.vguang.utils.encrypt;

import java.util.zip.CRC32;

/**
 * @author wangsir
 *
 * 2017年9月21日
 */
public class CRCUtil {
	
	public static long encrypt(byte[] bytes){
		CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		
		return crc32.getValue();
	}
}
