package com.vguang.shiro;

public class MyShiroManager {

	
	// 注意/r/n前不能有空格
	private static final String CRLF = "\r\n";

	public String loadFilterChainDefinitions() {
		StringBuffer sb = new StringBuffer();
		//从数据库读取权限
		sb.append(getFixedAuthRule());
		return sb.toString();
	}
	
	private String getFixedAuthRule(){
		
		return null;
	}
}
