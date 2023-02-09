package com.vguang.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Dingjz
 *
 * @date 2018年6月14日
 */
public class GsonUtil {
	private static Gson gson;
	private static Gson gson2;

	public static Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().enableComplexMapKeySerialization().create();
		}
		return gson;
	}

	public static Gson getComplexGson() {
		if (gson2 == null) {
			gson2 = new GsonBuilder().enableComplexMapKeySerialization().create();
		}
		return gson2;
	}

	public static String toJson(Object obj) {

		return getGson().toJson(obj);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return getGson().fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		return getGson().fromJson(json, typeOfT);
	}
}
