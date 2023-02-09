package com.vguang.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 把字符串、结果集转换成JSON格式
 * @author wang
 * date 2017-03-08
 * modify 2017-03-20
 */
public class JsonUtil {
	
	/**
	 * 把结果集转为数组
	 * @param <T>
	 * @return
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	public static <T> JSONArray list2json(List<T> list) throws SQLException, JSONException{
		JSONArray arr = new JSONArray(list);
		
		return arr;
	}
	/**
	 * 把结果集转为数组
	 * @return
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	public JSONArray rset2json(ResultSet rs) throws SQLException, JSONException{
		JSONArray arr = new JSONArray();
		
		ResultSetMetaData md = rs.getMetaData();
		int col = md.getColumnCount();
		
		while(rs.next()){
			JSONObject json = new JSONObject();
			for(int i=1; i<=col; i++){
				String colname = md.getColumnLabel(i);
				String val = rs.getString(colname);
				
				json.put(colname, val);
			}
			arr.put(json);
		}
		
		return arr;
	}
	
	/**
	 * 把字符串转为JSON格式
	 * @param str
	 * @return
	 */
	public static JSONObject str2json(String str){
		if(str == null) return null;
		
		JSONObject json = null;
		try {
			json = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 把返回给前端的数据拼接成JSON格式
	 * @param params
	 * @return
	 * @throws JSONException 
	 */
	public static JSONObject createJson(String ...params) throws JSONException{
		JSONObject json = new JSONObject();
		for(String str : params){
			json.put(str, str);
		}
		
		return json;
	}
	
	public static JSONObject ToJson(String payload) throws JSONException{
		JSONObject jsonObject1 = new JSONObject(payload);
		
		return jsonObject1;
	}
	
}
