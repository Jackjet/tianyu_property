package com.vguang.utils;

/**
 * 所有常量使用枚举
 * @author wang
 * date 2017-04-12
 */
public enum TimeEnum {

	//基数值
	DAY_TIME(86400),
	WEEK_TIME(604800),
	MONTH_TIME(2592000);
	
	private final int value;

	private TimeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
