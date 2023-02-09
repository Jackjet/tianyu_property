package com.vguang.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.vguang.service.impl.SyncService;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;  
/**
 * 向微信服务器发送请求，获取数据
 * 
 * @author wang date 2017-02-28
 */
public class HttpUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
//	@Autowired
	private static JedisPool jedisPool = (JedisPool) SpringContextUtil.getBean("jedisPool");
	private static SyncService syncService = (SyncService) SpringContextUtil.getBean("syncService");
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static synchronized String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			log.info("urlNameString=="+urlNameString);
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();

			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送GET请求出现异常：" + e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static synchronized String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			log.info("realUrl:{}",realUrl);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流,发送请求参数
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();

			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			for (String line; (line = in.readLine()) != null;) {
				result.append(line);
			}

		} catch (Exception e) {
			log.error("发送 POST请求出现异常:{}", e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null){
					in.close();
				}
				if (out != null){
					out.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.toString();
	}
	/**
	 * 返回access_token,先判断缓存是否存在，如果存在则直接取出，否则重新获取
	 * access_token有效时间为2小时
	 * @return
	 */
	public static String getWxAccessToken(String wAppid, String wSecret, String gtype){

		log.info("======开始获取微信access_token======");
		Jedis jis = jedisPool.getResource();
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String access_token = null;
		
		Map<String, String> configs = new SystemConfigs().getConfigsMap();
			if(null == gtype){
				gtype = "client_credential";
				gtype = gtype.trim();
				log.info("gtype============="+gtype);
			}
		
		//请求参数
        String params = "appid=" + wAppid + "&secret=" + wSecret + "&grant_type=" + gtype;
        log.info("params=++++++++++++++"+params);
        if(jis.exists(wAppid)){
        	access_token = jis.get(wAppid);
        	log.info("access_token=++++++++++++++"+access_token);
        }else{
        	String sr = HttpUtil.sendGet(url, params);
            log.info("后台token请求结果:{}" ,sr);
            JSONObject skey = JsonUtil.str2json(sr);
            log.info("skey=++++++++++++++"+skey);
    		try {
    			access_token = skey.getString("access_token");
    			log.info("access_token****************8"+access_token);
    			jis.setex(wAppid, 3600, access_token);
    		} catch (JSONException e1) {
    			e1.printStackTrace();
    		}
        }
		
        if(null != jis){
        	jis.close();
        }
		return access_token;
	
	}
	
	/**
	 * 根据access_token、params获取二维码图片
	 * 
	 */
	public static InputStream getWxQrCode(String access_token, JSONObject params){
		log.info("======开始获取微信二维码图片======");
		String url = "https://api.weixin.qq.com/wxa/getwxacode?access_token=" + access_token;
		
		PrintWriter out = null;
		InputStream in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			// 获取URLConnection对象对应的输出流,发送请求参数
			out = new PrintWriter(conn.getOutputStream());
			out.println(params);
			out.flush();
						
			// 读取URL的响应
			in = conn.getInputStream();
		} catch(Exception e){
			e.printStackTrace();
		} finally {
//			closeIPStream(in, out);
			if(null != out){
				out.close();
			}
		}
		
		return in;
	}
	
	/**
	 * 把二进制图片流写入到本地
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String writeQrcode(InputStream in, String filePath) {
		log.info("======开始二进制图片流写入到本地======");
		File file = new File(filePath);
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file);
			int len = 0;
			byte[] bytes = new byte[1024];
			while ((len = in.read(bytes)) != -1) {
				fout.write(bytes, 0, len);
			}
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeIOStream(in, fout);
		}
		return "Success";
	}
	
	private static void closeIPStream(InputStream in, PrintWriter out) {
		try {
			if (null != in) {
				in.close();
			}
			if(null != out){
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void closeIOStream(InputStream in, OutputStream out) {
		try {
			if (null != in) {
				in.close();
			}
			if(null != out){
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getIP(String remoteAddr, String forwarded, String realIp){
		String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if(forwarded != null){
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
        return ip;
	}
	/**
	 * 根据access_token、params获取二维码图片
	 * @throws AlipayApiException 
	 * 
	 */
	
	
	/**
	 * 根据access_token、params获取二维码图片
	 * @throws AlipayApiException 
	 * @throws IOException 
	 * 
	 */
	

	public static String querywAppid(String wAppid, Integer orgid) {	return wAppid;
	}
	
	/**
	 * post 的from表单提交
	 * @param url
	 * @param paramMap
	 * @return
	 */
	 public static String httpfrom(String url, Map<String, Object> paramMap) {
			
		  CloseableHttpClient httpClient = null;
	        CloseableHttpResponse httpResponse = null;
	        String result = "";
	        // 创建httpClient实例
	        httpClient = HttpClients.createDefault();
	        // 创建httpPost远程连接实例
	        HttpPost httpPost = new HttpPost("http://www.dingdingkaimen.cn/imgdetect/imgdetection");
	        // 配置请求参数实例
	        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
	                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
	                .setSocketTimeout(60000)// 设置读取数据连接超时时间
	                .build();
	        // 为httpPost实例设置配置
	        httpPost.setConfig(requestConfig);
	        // 设置请求头
	        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
	        // 封装post请求参数
	        if (null != paramMap && paramMap.size() > 0) {
	            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	            // 通过map集成entrySet方法获取entity
	            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
	            // 循环遍历，获取迭代器
	            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
	            while (iterator.hasNext()) {
	                Entry<String, Object> mapEntry = iterator.next();
	                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
	            }

	            // 为httpPost设置封装好的请求参数
	            try {
	                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }
	        }
	        try {
	            // httpClient对象执行post请求,并返回响应参数对象
	            httpResponse = httpClient.execute(httpPost);
	            // 从响应对象中获取响应内容
	            HttpEntity entity = httpResponse.getEntity();
	            result = EntityUtils.toString(entity);
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            // 关闭资源
	            if (null != httpResponse) {
	                try {
	                    httpResponse.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (null != httpClient) {
	                try {
	                    httpClient.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	       System.out.println(result);
		return result;
	 }  
	 
	 
	 /**
		 * post 的from表单提交
		 * @param url
		 * @param paramMap
		 * @return
		 */
		 public static String httpfrom1(String url, Map<String, Object> paramMap) {
				
			  CloseableHttpClient httpClient = null;
		        CloseableHttpResponse httpResponse = null;
		        String result = "";
		        // 创建httpClient实例
		        httpClient = HttpClients.createDefault();
		        // 创建httpPost远程连接实例
		        log.info("url======"+url);
		        HttpPost httpPost = new HttpPost(url);
		        log.info("1111111111======");
		        // 配置请求参数实例
		        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
		                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
		                .setSocketTimeout(60000)// 设置读取数据连接超时时间
		                .build();
		        // 为httpPost实例设置配置
		        httpPost.setConfig(requestConfig);
		        // 设置请求头
		        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		        // 封装post请求参数
		        if (null != paramMap && paramMap.size() > 0) {
		            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		            // 通过map集成entrySet方法获取entity
		            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
		            log.info("entrySet=========="+entrySet);
		            // 循环遍历，获取迭代器
		            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
		            log.info("iterator=========="+iterator);
		            while (iterator.hasNext()) {
		                Entry<String, Object> mapEntry = iterator.next();
		                log.info("key======="+mapEntry.getKey());
		                log.info("value======="+ mapEntry.getValue());
		                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
		            }

		            // 为httpPost设置封装好的请求参数
		            try {
		                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		            } catch (UnsupportedEncodingException e) {
		                e.printStackTrace();
		            }
		        }
		        try {
		            // httpClient对象执行post请求,并返回响应参数对象
		            httpResponse = httpClient.execute(httpPost);
		            // 从响应对象中获取响应内容
		            HttpEntity entity = httpResponse.getEntity();
		            result = EntityUtils.toString(entity);
		        } catch (ClientProtocolException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            // 关闭资源
		            if (null != httpResponse) {
		                try {
		                    httpResponse.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		            if (null != httpClient) {
		                try {
		                    httpClient.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		        log.info(result);
			return result;
		 }  
	
	
}
