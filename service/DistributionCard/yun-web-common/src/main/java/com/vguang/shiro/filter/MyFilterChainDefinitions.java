package com.vguang.shiro.filter;

import java.util.Map;

public class MyFilterChainDefinitions extends AbstractFilterChainDefinitionsService {

	@Override
	public Map<String, String> initOtherPermission(Map<String, String> map) {
		return map;
	}

}
