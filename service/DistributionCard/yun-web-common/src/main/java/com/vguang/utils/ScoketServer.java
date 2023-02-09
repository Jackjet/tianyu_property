package com.vguang.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vguang.service.IDeviceService;
import com.vguang.service.ISyncService;
import com.vguang.service.impl.SyncService;
import com.vguang.system.SpringContextUtil;



@Service
public class ScoketServer {
	private static final Logger log = LoggerFactory.getLogger(ScoketServer.class);
	public static Socket connection  =null;
	public static HashMap<String, Object> SockethashMap =new HashMap<>();
	public static String Key  =null;//暂时写在这,以后要放到数据库中,在添加电梯的时候填写,key=电梯版的唯一标识
	// 服务器IP
	public static final String SERVER_IP = "127.0.0.1";

	// 服务器端口号
	public static final int SERVER_PORT = 8686;

	// 请求终结字符串
	public static final char REQUEST_END_CHAR = '#';

	@Autowired
	private JedisManager jedisManager;
//	@Autowired
//	private static ISyncService sysService;
	/***
	 * 启动服务器
	 *
	 * @param 服务器监听的端口号
	 *            ，服务器ip无需指定，系统自动分配
	 */
	public void startServer(String serverIP, int serverPort) {
		log.info("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		// 创建服务器地址对象
		InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIP);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		log.info("scoket服务器开始运行");
		// Java提供了ServerSocket作为服务器
		// 这里使用了Java的自动关闭的语法，很好用
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
			ExecutorService executor = Executors.newFixedThreadPool(100);
			// serverSocket.setSoTimeout(timeout);
			while (true) {
				log.info("scoket服务器等待连接");
				try {
					// 有客户端向服务器发起tcp连接时，accept会返回一个Socket
					// 该Socket的対端就是客户端的Socket
					// 具体过程可以查看TCP三次握手过程

					 connection = serverSocket.accept();
					log.info("scoket处理客户端所发信息>>>>>>>>>>>>>"+connection);
					InetAddress getInetAddress = connection.getInetAddress();
					log.info("getInetAddress=============="+getInetAddress);
					Integer getLocalPort = connection.getLocalPort();
					log.info("getLocalPort===="+getLocalPort);
					Key = 	Key = getInetAddress+"--"+getLocalPort;
					SockethashMap.put(Key, connection);
					 Object a = SockethashMap.get(Key);
					 log.info("a======================="+a);
					// 利用线程池，启动线程

					executor.execute(new Runnable() {
						@Override
						public void run() {
							// 使用局部引用，防止connection被回收
							Socket conn = connection;
							String result = null;
							try {
								int q = 1;
								while (q == 1) {
									log.info("q====" + q);

									InputStream in = conn.getInputStream();
									int s;
									final StringBuilder recvStrBuilder = new StringBuilder();
									log.info("============================");
									// 读取客户端的请求包包头，包头固定长度32
									byte[] b = new byte[1024];

									while (true) {
										if((s = in.read(b)) == -1) {
											log.info("断开连接====");
											q = 2;
											conn.close();
											log.info("-------------");
											return;
										}
//										InetAddress getInetAddress = connection.getInetAddress();
//										log.info("getInetAddress=============="+getInetAddress);
//										int getLocalPort = connection.getLocalPort();
//										log.info("getLocalPort===="+getLocalPort);
//										Key = "55AA";//key
//										SockethashMap.put(Key, connection);
//										 Object a = SockethashMap.get(Key);
//										 log.info("a======================="+a);
										log.info("b==========" + b);
										String bb = ByteUtil.byte2Hex(b);
										log.info("bb===============" + bb);
										String Title = bb.substring(0, 4);
										log.info("Title====" + Title);
										if (Title.equals("55AA") || Title.equals("55aa")) {
											String Str_command = bb.substring(4, 6);
											Integer command = Integer.valueOf(Str_command);
											log.info("command====" + command+"--");
											if (command ==49) {
												log.info("------------------------------设备心跳指令回复指令------------------");
												log.info("设备心跳指令========" + bb);
												//开始回复
												String text ="55AA49010000B7";
												byte[] bytes = ByteUtil.Str16toBytes(text);
												OutputStream out = conn.getOutputStream();
												out.write(bytes);
												log.info("回复完成");
											} else if (command ==50) {
												log.info("------------------------------握手指令回复指令------------------");
												String jsonstr = bb.substring(6, 12);
												log.info("jsonstr========" + jsonstr);
												if (jsonstr.equals("010000")) {
													log.info("开启成功,加入缓存,供页面调用");
													String key = "handshake";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 15);
												}

											} else if (command ==51) {
												log.info("----------------------开启发卡器回复指令----------------------------");
												String jsonstr = bb.substring(6, 12);
												log.info("jsonstr========" + jsonstr);
												if (jsonstr.equals("010000")) {
													log.info("开启成功,加入缓存,供页面调用");
													String key = "open_distributioncard_return";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 30);
												} else  {
													log.info("开启失败,加入缓存,供页面调用");
													String key = "handshake";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 30);
												}
											} else if (command ==52) {
												log.info("----------------------发卡器上报读卡数据----------------------------");
												String len = bb.substring(6, 8);
												log.info("len========" + len);
												// 转义为10进制
												int len_int = ByteUtil.Str16toint10(len);
												log.info("数据长度为len_int==========" + len_int);
												// 获取数据
												String text = bb.substring(10, 10 + len_int * 2);
												// 获取校验码
												String check = bb.substring(10 + len_int * 2, 10 + len_int * 2 + 2);
												text = "55aa52" + len + "00" + text + check;
												log.info("正文数据为text....." + text);
												// 加入缓存中
												String key = "returncard";
												jedisManager.delValueByStr(0, key);
												jedisManager.setValueByStr(0, key, text, 30);
												 byte[] resu = new byte[7];
												  resu[0]=(byte)0x55;resu[1]=(byte) 0xAA;resu[2]=(byte)0x52;resu[3]=(byte)0x01;resu[4]=(byte)0x00;resu[5]=(byte)0x00;resu[6]=(byte) 0xAC;
													// 向客户端写出处理后的字符串
													OutputStream out = conn.getOutputStream();
													out.write(resu);
											} else if (command ==53) {
												log.info(
														"----------------------下发卡数据设备返回值----------------------------");
												String Card_result = bb.substring(6, 12);
												log.info("Card_result========" + Card_result);
												// 加入缓存中
												String key = "Card_result";
												jedisManager.delValueByStr(0, key);
												jedisManager.setValueByStr(0, key, Card_result, 30);
											} else if (command ==55) {
												log.info("----------------------修改扇区返回值----------------------------");
												String jsonstr = bb.substring(6, 12);
												log.info("jsonstr========" + jsonstr);
												if (jsonstr.equals("010000")) {
													log.info("修改成功,加入缓存,供页面调用");
													String key = "setUpSectorAndSecret_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												} else  {
													log.info("修改失败,加入缓存,供页面调用");
													String key = "setUpSectorAndSecret_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												}
											} else if (command ==56) {
												log.info("----------------------下发身份id返回值----------------------------");
												String jsonstr = bb.substring(6, 12);
												log.info("jsonstr========" + jsonstr);
												if (jsonstr.equals("010000")) {
													log.info("修改成功,加入缓存,供页面调用");
													String key = "distributionidentity_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												} else  {
													log.info("修改失败,加入缓存,供页面调用");
													String key = "distributionidentity_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												}
											} else if (command ==57) {
												log.info("----------------------下发配置卡返回值----------------------------");
												String jsonstr = bb.substring(6, 12);
												log.info("jsonstr========" + jsonstr);
												if (jsonstr.equals("010000")) {
													log.info("修改成功,加入缓存,供页面调用");
													String key = "deviceconfigcard_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												} else  {
													log.info("修改失败,加入缓存,供页面调用");
													String key = "deviceconfigcard_result";
													jedisManager.delValueByStr(0, key);
													jedisManager.setValueByStr(0, key, jsonstr, 11);
												}
											} else {
												log.info("命令字不对,直接过滤掉");
//												byte[] resu = new byte[7];
//												resu[0] = (byte) 0x55;
//												resu[1] = (byte) 0xAA;
//												resu[2] = (byte) 0x40;
//												resu[3] = (byte) 0x01;
//												resu[4] = (byte) 0x00;
//												resu[5] = (byte) 0x00;
//												resu[6] = (byte) 0xBE;
//												// 向客户端写出处理后的字符串
//												OutputStream out = conn.getOutputStream();
//												out.write(resu);
											}
										} else {
											log.info("协议头不对,直接过滤掉");
										}

									}

									// for (int i=0; (s=in.read())!=-1&&i<268; i++) {
									// log.info("s==========="+s);
									// recvStrBuilder.append((char)s);
									// }
									// log.info("s===="+s);
									//
									// log.info("s===="+in.read());

								}
							} catch (IOException e) {
								e.printStackTrace();
							} finally {


							}
						}
					});

				} catch (IOException e) {
					log.info("===================================================================3");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			log.info("===================================================================4");
			e.printStackTrace();
		}
	}




	/**
	 * 握手
	 * @param DeviceID
	 * @param flag
	 * @return
	 * @throws IOException
	 */
	public static int handshake() throws IOException {
		log.info("---------------------------握手--------------------------");
		SyncService sysService = (SyncService) SpringContextUtil.getBean("syncService");
		String text ="55AA500000AF";
		byte[] bytes = ByteUtil.Str16toBytes(text);
		System.out.println("最后的的16进制为：：" + ByteUtil.byte2Hex(bytes));
		// 向客户端写出处理后的字符串
		//String Key =Key;
		connection = (Socket) SockethashMap.get(Key);
		log.info("connection====" + connection);
		if(null== connection ||"".equals(connection)) {
		log.info("发卡设备未连接或无发卡设备");
		return -10;
		}
		Socket conn = connection;
		OutputStream out = conn.getOutputStream();
		out.write(bytes);
		return 0;
	}
	/**
	 * 开启发卡指令
	 * @param DeviceID
	 * @param flag
	 * @return
	 * @throws IOException
	 */
	public static int open_distributioncard(String DeviceID, Integer flag,String  SecretType ) throws IOException {
		log.info("---------------------------发送发卡开启指令--------------------------");
		SyncService sysService = (SyncService) SpringContextUtil.getBean("syncService");



		// 标题头
		byte[] herdbyte = new byte[6];
		herdbyte[0] = (byte) 0x55;
		herdbyte[1] = (byte) 0xAA;
		herdbyte[2] = (byte) 0x51;
		herdbyte[3] = (byte) 0x21;
		herdbyte[4] = (byte) 0x00;
		if (flag == 1) {
			herdbyte[5] = (byte) 0x01;
		} else if(flag ==2) {
			herdbyte[5] = (byte) 0x02;
		}else if(flag ==3) {
			herdbyte[5] = (byte) 0x03;
		}else if(flag ==4) {
			herdbyte[5] = (byte) 0x04;
		}
		log.info("----------------------------------权限部分----------------------------------");
		String Sector =null;
		String Sector_SecretKey =null;
		if(flag ==1) {
			log.info("门禁");
			// 获取当前门禁扇区
			String Key4 = "M_Power_Sector";
			 Sector = sysService.getValueByKey(Key4);
			// 获取当前门禁权限扇区秘钥
			String Key = "M_Power_Sector_Secret";
			 Sector_SecretKey = sysService.getValueByKey(Key);

		}else if(flag ==2) {
			log.info("梯控");
			// 获取当前梯控权限扇区
			String Key6 = "T_Power_Sector";
			 Sector = sysService.getValueByKey(Key6);
			// 获取当前梯控权限扇区秘钥
			String Key2 = "T_Power_Sector_Secret";
			 Sector_SecretKey = sysService.getValueByKey(Key2);
		}else if(flag ==3) {
			log.info("身份权限");
			// 获取当前身份权限扇区
			String Key6 = "P_Power_Sector";
			 Sector = sysService.getValueByKey(Key6);
			// 获取当前身份权限扇区秘钥
			String Key2 = "P_Power_Sector_Secret";
			 Sector_SecretKey = sysService.getValueByKey(Key2);
		}else if(flag ==4) {
			log.info("配置卡");
			// 获取当前配置卡权限扇区
			String Key6 = "D_Power_Sector";
			 Sector = sysService.getValueByKey(Key6);
			// 获取当前配置卡权限扇区秘钥
			String Key2 = "D_Power_Sector_Secret";
			 Sector_SecretKey = sysService.getValueByKey(Key2);
		}
		log.info("Sector_SecretKey========"+Sector_SecretKey);
		log.info("获取秘钥");
		if(SecretType.equals("A")) {
			log.info("A秘钥,获取秘钥前12位");
			Sector = Sector+"60";
			 Sector_SecretKey = Sector_SecretKey.substring(0, 12);

		}else if(SecretType.equals("B")) {
			log.info("B秘钥");
			Sector = Sector+"61";
			 Sector_SecretKey = Sector_SecretKey.substring(20, 32);
		}

		byte[]  bytes_1 = ByteUtil.Str16toBytes(Sector);
		// +原秘钥
		byte[] bytes_2 = ByteUtil.Str16toBytes(Sector_SecretKey);
		// 合并数组
		byte[] power_oldKey = ByteUtil.concatBytes(bytes_1, bytes_2);
		// 新扇区+新秘钥类型+新秘钥(其实和老的是一样的)
		byte[] power_newKey = power_oldKey;
		byte[] power = ByteUtil.concatBytes(power_newKey, power_oldKey);
		log.info("-------------------------------黑名单部分-------------------------------------");
		String Blacklist_Sector= null;
		String Blacklist_Sector_SecretKey= null;
		if (flag == 1) {
			log.info("门禁");
			// 获取当前门禁黑名单扇区
			String Key5 = "M_Blacklist_Sector";
			Blacklist_Sector = sysService.getValueByKey(Key5);
			// 获取当前门禁黑名单扇区秘钥
			String Key1 = "M_Blacklist_Sector_Secret";
			Blacklist_Sector_SecretKey = sysService.getValueByKey(Key1);
		} else if(flag ==2){
			log.info("梯控");
			// 获取当前梯控黑名单扇区
			String Key7 = "T_Blacklist_Sector";
			Blacklist_Sector = sysService.getValueByKey(Key7);
			// 获取当前梯控黑名单扇区秘钥
			String Key3 = "T_Blacklist_Sector_Secret";
			Blacklist_Sector_SecretKey = sysService.getValueByKey(Key3);

		}else if(flag ==3) {
			log.info("身份权限");
			// 获取当前身份权限黑名单扇区
			String Key6 = "P_Blacklist_Sector";
			Blacklist_Sector = sysService.getValueByKey(Key6);
			// 获取当前身份权限黑名单扇区秘钥
			String Key2 = "P_Blacklist_Sector_Secret";
			Blacklist_Sector_SecretKey = sysService.getValueByKey(Key2);
		}else if(flag ==4) {
			log.info("配置卡");
			// 获取当前配置卡黑名单扇区
			String Key6 = "D_Blacklist_Sector";
			Blacklist_Sector = sysService.getValueByKey(Key6);
			// 获取当前配置卡黑名单扇区秘钥
			String Key2 = "D_Blacklist_Sector_Secret";
			Blacklist_Sector_SecretKey = sysService.getValueByKey(Key2);
		}
		log.info("Blacklist_Sector_SecretKey========"+Blacklist_Sector_SecretKey);
		log.info("Blacklist_Sector========"+Blacklist_Sector);
		if(SecretType.equals("A")) {
			log.info("A秘钥,获取秘钥前12位");
			Blacklist_Sector = Blacklist_Sector+"60";
			Blacklist_Sector_SecretKey = Blacklist_Sector_SecretKey.substring(0, 12);

		}else if(SecretType.equals("B")) {
			log.info("B秘钥");
			Blacklist_Sector = Blacklist_Sector+"61";
			Blacklist_Sector_SecretKey = Blacklist_Sector_SecretKey.substring(20, 32);
		}
		byte[] bytes_3 = ByteUtil.Str16toBytes(Blacklist_Sector);
		// +原秘钥
		byte[] bytes_4 = ByteUtil.Str16toBytes(Blacklist_Sector_SecretKey);
		// 合并数组
		byte[] blacklist_oldKey = ByteUtil.concatBytes(bytes_3, bytes_4);
		byte[] blacklist_newKey = blacklist_oldKey;
		byte[] blacklist = ByteUtil.concatBytes(blacklist_oldKey, blacklist_newKey);
		log.info("合并为最终的开启刷卡之类");
		byte[] crc = new byte[1];
		byte[] bytes = ByteUtil.concatBytes(herdbyte, power, blacklist);
		crc[0] = bytes[0];
		for (int i = 1; i < bytes.length; i++) {
			crc[0] ^= bytes[i];

		}
		System.out.println("最后的的16进制为：：" + ByteUtil.byte2Hex(crc));
		bytes = ByteUtil.concatBytes(bytes,crc);
		System.out.println(bytes);
		System.out.println("最后的的16进制为：：" + ByteUtil.byte2Hex(bytes));
		// 向客户端写出处理后的字符串
	//String Key ="55AA";
		connection = (Socket) SockethashMap.get(Key);
		log.info("connection====" + connection);
		if(null== connection ||"".equals(connection)) {
			log.info("发卡设备未连接或无发卡设备");
			return -10;
			}
		Socket conn = connection;
		OutputStream out = conn.getOutputStream();
		out.write(bytes);
		return 0;
	}

	/**
	 * 下发卡信息
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static int sendOutCard(byte[] bytes) throws IOException {
		log.info("---------------------------发送TCP指令--------------------------");
		System.out.println("最后的的16进制为：：" + ByteUtil.byte2Hex(bytes));
		Integer res = -1;
		// 向客户端写出处理后的字符串
	//	Key = "55AA";
		connection = (Socket) SockethashMap.get(Key);
		log.info("connection====" + connection);
		if(null== connection ||"".equals(connection)) {
			log.info("发卡设备未连接或无发卡设备");
			return -10;
			}
		Socket conn = connection;
		OutputStream out = conn.getOutputStream();
		out.write(bytes);
		res = 0;

		return res;
	}

	public static int setUpSectorAndSecret(int Sector, String Sector_type, String Ord_Secret, String new_Secret)
			throws IOException {
		log.info("---------------------------设置扇区秘钥--------------------------");
		// System.out.println("最后的的16进制为：：" + ByteUtil.byte2Hex(bytes));
		// 标题头以及长度
		byte[] herdbyte = new byte[6];
		herdbyte[0] = (byte) 0x55;
		herdbyte[1] = (byte) 0xAA;
		herdbyte[2] = (byte) 0x55;
		herdbyte[3] = (byte) 0x18;
		herdbyte[4] = (byte) 0x00;
		// 扇区
		byte r = (byte) Sector;
		byte[] bytes_Sector = new byte[1];
		bytes_Sector[0] = r;
		// 秘钥类型
		byte[] byte_Sector_type = ByteUtil.Str16toBytes(Sector_type);
		// 老秘钥
		byte[] byte_Ord_Secret = ByteUtil.Str16toBytes(Ord_Secret);
		// 新秘钥
		byte[] byte_new_Secret = ByteUtil.Str16toBytes(new_Secret);
		// 合并数组
		byte[] texts = ByteUtil.concatBytes(herdbyte, bytes_Sector, byte_Sector_type, byte_Ord_Secret, byte_new_Secret);
		//计算异或
		byte[] crc = ByteUtil.bytesXorCrc(texts);
		// 最终数据
		byte[] bytes = ByteUtil.concatBytes(texts, crc);
		Integer res = -1;
		// 向客户端写出处理后的字符串
	//	Key = "55AA";
		connection = (Socket) SockethashMap.get(Key);
		log.info("connection====" + connection);
		if(null== connection ||"".equals(connection)) {
			log.info("发卡设备未连接或无发卡设备");
			return -10;
			}
		Socket conn = connection;
		OutputStream out = conn.getOutputStream();
		out.write(bytes);
		res = 0;
		return res;
	}

	/**
	 * 测试发送指令
	 * @param abcd
	 * @return
	 * @throws IOException
	 */
	public static int test(String abcd) throws IOException {
		log.info("---------------------------测试发送指令--------------------------");
		 byte[] resu = new byte[7];
		  resu[0]=(byte)0x55;resu[1]=(byte) 0xAA;resu[2]=(byte)0x40;resu[3]=(byte)0x01;resu[4]=(byte)0x00;resu[5]=(byte)0x00;resu[6]=(byte) 0xBE;
			// 向客户端写出处理后的字符串
		  connection = (Socket) SockethashMap.get(Key);
		  Socket conn = connection;
			OutputStream out = conn.getOutputStream();
			out.write(resu);

		return 12;
	}


}

