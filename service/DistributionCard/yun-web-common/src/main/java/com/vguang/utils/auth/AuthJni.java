/**
 * 
 */
package com.vguang.utils.auth;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Dingjz
 *
 * @date 2018年7月10日
 */
@Component
public class AuthJni {

	//beginTime  longlongUTC 起始时间
	//bs 16BYTE
	//endTime longlong 失效时间
	//orgKey org密钥
	private static final Logger log = LoggerFactory.getLogger(AuthJni.class);

	public interface AuthLib extends Library {
		int get_next_key(long beginTime, byte[] bs, byte[] endTime, int index, byte[] orgKey);

		int get_current_key(long beginTime, byte[] bs, byte[] endTime, int index, byte[] orgKey);

		int test_so(int type, long arg);
		
		String path = "/usr/local/lib/libchannel_linux_x86_64.so";
		AuthLib INSTANCE = (AuthLib) Native.loadLibrary(path, AuthLib.class);
		
	}

	public int get_next_key(long beginTime, byte[] auth, byte[] endTime, byte[] orgKey) {
		return AuthLib.INSTANCE.get_next_key(beginTime, auth, endTime, -1, orgKey);
	}

	public int get_current_key(long beginTime, byte[] auth, byte[] endTime, byte[] orgKey) {
		return AuthLib.INSTANCE.get_current_key(beginTime, auth, endTime, -1, orgKey);
	}

	public int test_so(int type, long arg) {
		// TODO Auto-generated method stub
		return AuthLib.INSTANCE.test_so(type, arg);
	}
}
