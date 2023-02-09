package com.vguang.system;

import java.util.ArrayList;
import java.util.List;

import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuaUtil {
	private static final Logger log = LoggerFactory.getLogger(LuaUtil.class);
	public static List<String> PUBLIC_LUA_FILES = new ArrayList<String>();

	public static void executeWithNoParam(String command){
		LuaState L = LuaStateFactory.newLuaState();
		// 加载lua标准库,否则一些lua基本函数无法使用
		L.openLibs();
		// 加载公共函数文件
		if (!PUBLIC_LUA_FILES.isEmpty()) {
			for (int i = 0; i < PUBLIC_LUA_FILES.size(); i++) {
				L.LdoFile(PUBLIC_LUA_FILES.get(i));
			}
		}
		// 加载函数串
		L.LdoString(command);
		
		L.close();
	}
	/**
	 * 直接传输命令(可以是函数),参数
	 * @param command
	 * @param params
	 */
	public static void executeString(String method, String methodName, String... params) {
		LuaState L = LuaStateFactory.newLuaState();
		// 加载lua标准库,否则一些lua基本函数无法使用
		L.openLibs();
		// 加载公共函数文件
		if (!PUBLIC_LUA_FILES.isEmpty()) {
			for (int i = 0; i < PUBLIC_LUA_FILES.size(); i++) {
				L.LdoFile(PUBLIC_LUA_FILES.get(i));
			}
		}

		// 加载函数串
		L.LdoString(method);
		// 调用函数名
		L.getField(LuaState.LUA_GLOBALSINDEX, methodName);
		// 设置参数
		int len = params.length;
		if (len > 0) {
			for (String param : params) {
				L.pushString(param);
			}
		}

		// 调用
		L.call(len, 1);

		// 设置返回对象
		L.setField(LuaState.LUA_GLOBALSINDEX, "RESULT");
		LuaObject lobj = L.getLuaObject("RESULT");
		// 获取返回值
		String res = lobj.getString();
		log.info("Lua执行结果：{}", res);
		L.close();
	}

	/**
	 * 从服务器读取.lua脚本文件，再执行
	 * @param filePath
	 * @param method
	 * @param params
	 */
	public static void executeFile(String filePath, String methodName, String... params) {
		// 1、执行脚本文件
		LuaState L = LuaStateFactory.newLuaState();
		L.openLibs();

		// 读入Lua脚本
		int error = L.LdoFile(filePath);
		if (error != 0) {
			System.out.println("parse error");
			return;
		}

		// 找到函数test
		L.getField(LuaState.LUA_GLOBALSINDEX, methodName);
		// 设置参数
		int len = params.length;
		if (len > 0) {
			for (String param : params) {
				L.pushString(param);
			}
		}
		
		// 调用!! 一共两个参数, 1个返回值
		L.call(len, 1);
		
		// 保存返回值, 到RESULT中
		L.setField(LuaState.LUA_GLOBALSINDEX, "RESULT");
		// 读入RESULT
		LuaObject l = L.getLuaObject("RESULT");
		// 打印结果.
		log.info("返回结果：{}", l.getString());
		L.close();
	}
}
