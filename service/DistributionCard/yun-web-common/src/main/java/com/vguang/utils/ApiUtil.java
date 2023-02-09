package com.vguang.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vguang.utils.encrypt.B64Util;
import com.vguang.utils.encrypt.HMACUtil;

/**
 * @author wangsir
 *
 *         2017年11月17日
 */
public class ApiUtil {
	private static final Logger log = LoggerFactory.getLogger(ApiUtil.class);
	private static final String CHARSET_UTF8 = "UTF-8";
	private static final String SEPARATOR = "&";

	/** 构造规范化的请求字符串（排序及URL编码） **/
	public static Map<String, String> splitQueryString(String url)
			throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(url);
		String query = uri.getQuery();
		final String[] pairs = query.split("&");
		TreeMap<String, String> queryMap = new TreeMap<String, String>();
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? pair.substring(0, idx) : pair;
			if (!queryMap.containsKey(key)) {
				queryMap.put(key, URLDecoder.decode(pair.substring(idx + 1), CHARSET_UTF8));
			}
		}
		return queryMap;
	}

	/**
	 * 拼接参数字符串
	 * 对字符串进行hmac签名
	 * 对GET/POST不同方式请求进行不同编码
	 * @throws Exception
	 */
	public static String generate(String method, TreeMap<String, String> parameter, String accessKeySecret)
			throws Exception {
		// 拼接生成待签名的字符串
		String signString = generateSignString(method, parameter);
		System.out.println("signString:" + signString);
		// 对字符串进行hmac签名
		byte[] signBytes = HMACUtil.hmacSHA1Signature(accessKeySecret + "&", signString);
		String signature = B64Util.encode(signBytes);
		System.out.println("signature:" + signature);
		
//		if ("POST".equals(method))
//			return signature;
//		URLEncoder.encode(signature, "UTF-8")
		
		return signature;
	}

	/**
	 * 拼接GET/POST格式,同时进行URLEncode编码
	 * @param httpMethod
	 * @param parameter
	 * @return
	 * @throws IOException
	 */
	public static String generateSignString(String httpMethod, Map<String, String> parameter) throws IOException {
		TreeMap<String, String> sortParameter = new TreeMap<String, String>();
		sortParameter.putAll(parameter);
		//构建请求参数字符串
		String canonicalizedQueryString = generateQueryString(sortParameter);
		
		if (null == httpMethod) {
			throw new RuntimeException("httpMethod can not be empty");
		}
		//构造待签名的字符串 GET&/&
		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(httpMethod)
			.append(SEPARATOR)
			.append(percentEncode("/"))
			.append(SEPARATOR)
			.append(percentEncode(canonicalizedQueryString));
		
		return stringToSign.toString();
	}
	
	/**
	 * 只对参数拼接，不进行URLEncode编码
	 * @param queries
	 * @return
	 */
	public static String generateQueryString(TreeMap<String, String> queries) {
		Set<String> names = queries.keySet();
		Iterator<String> iter = names.iterator();
		
		StringBuilder canonicalizedQueryString = new StringBuilder();
		while(iter.hasNext()){
			String key = iter.next();
			canonicalizedQueryString.append("&")
				.append(percentEncode(key))
				.append("=")
				.append(percentEncode((String) queries.get(key)));
		}
		
		return canonicalizedQueryString.toString().substring(1);
	}

	public static String percentEncode(String value) {
		try {
			return value == null ? null
					: URLEncoder.encode(value, CHARSET_UTF8).replace("+", "%20").replace("*", "%2A").replace("%7E","~");
		} catch (Exception e) {
		}
		return "";
	}

}
