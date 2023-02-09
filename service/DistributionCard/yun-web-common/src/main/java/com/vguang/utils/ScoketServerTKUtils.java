package com.vguang.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.vguang.dao.ICardDao;
import com.vguang.dao.IDeviceDao;
import com.vguang.dao.IPersonDao;
import com.vguang.dao.IRuleDao;
import com.vguang.eneity.Card;
import com.vguang.eneity.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 思路
 * 开启线程池
 * <p>
 * 1.建立连接
 * 2.在线程池中开启线程池
 * 线程
 * 1).获取设备ID
 * 发送获取ID指令
 * 读等待回应
 * a. 得到设备ID
 * 判断设备ID是否在已在线设备中
 * 在，记录冲突，(不做退出线程，如果同一设备，原来线程会超时)
 * 不在
 * 设置线程对应的设备ID，记录在线设备的ID
 * b. 超时  退出线程
 * c. 其他  退出线程
 * 2).循环
 * {
 * 判断是否超时（线程超时时间）
 * 是，记录，退出线程（同时从在线设备列表中删除设备ID）
 * <p>
 * 读等待(阻塞，超时时间设置为线程超时时间/4)
 * 有数据：
 * 判断指令头* 					 {
 * 心跳：处理心跳
 * 键值上报：
 * 验证身份：
 * 无身份，记录，返回失败（亮红灯）
 * 有身份，验证权限，
 * 有权限，记录，返回成功（亮绿灯，控制开门）
 * 无权限，记录，返回失败（亮红灯）
 * ：记录
 * }
 * }
 * <p>
 * 异常：
 * 记录，退出线程（同时从在线设备列表中删除设备ID）
 */
@Service
public class ScoketServerTKUtils {
    @Autowired
    private JedisManager jedisManager;
    private Logger log = LoggerFactory.getLogger(ScoketServerTKUtils.class);
    public static HashMap<String, Object> SockethashMap =new HashMap<>();
    // 服务器端口号
    public static final int SERVER_PORT = 60219;

    /**
     * 以下是线程池参数
     **/
    //核心线程数
    final static int CORE_POOL_SIZE = 600;
    //超过核心线程数量的线程最大空闲时间,单位秒
    final static int KEEP_ALIVE_TIME = 5;
    //以秒为时间单位
    final TimeUnit unit = TimeUnit.SECONDS;
    //创建工作队列，用于存放提交的等待执行任务
    final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);

    //监控线程间隔,单位毫秒
    final static int MONITOR_THREAD_INTERVAL = 2 * 60 * 1000;

    //心跳间隔时间，单位秒
    final private static int HEART_BEAT_INTERVAL = 5;
    //获取设备ID响应超时时间，单位秒
    final private static int QUERY_DEVICE_ID_TIMEOUT = 30;

    /***
     * 启动服务器
     *
     */
    public void startServer() {
        log.info("scoket服务器开始运行");
        // Java提供了ServerSocket作为服务器
        // 这里使用了Java的自动关闭的语法，很好用
        try {
            //读取服务器端口号
            int serverPort = getSystemConfigValue(SERVER_PORT, "TcpServerPort");
            log.info("服务器TCP服务端口:" + serverPort);

            //读取支持最大设备数
            int maxDeviceCount = getSystemConfigValue(CORE_POOL_SIZE, "MaxDeviceCount");
            log.info("支持最大设备数:" + maxDeviceCount);
            int maxPoolSize = maxDeviceCount * 2;

            //读取超过核心线程数量的线程最大空闲时间,单位秒
            int keepAliveTime = getSystemConfigValue(KEEP_ALIVE_TIME, "KeepAliveTime");
            log.info("超过核心线程数量的线程最大空闲时间,单位秒:" + keepAliveTime);

            //读取监控线程间隔,单位毫秒
            int monitorThreadInterval = getSystemConfigValue(MONITOR_THREAD_INTERVAL, "MonitorThreadInterval");
            log.info("监控线程间隔,单位毫秒:" + monitorThreadInterval);
            final int finalMonitorThreadInterval = monitorThreadInterval;

            //读取心跳间隔时间，单位秒
            int heartBeatInterval = getSystemConfigValue(HEART_BEAT_INTERVAL, "HeartBeatInterval");
            log.info("心跳间隔时间，单位秒:" + heartBeatInterval);
            //线程处理响应超时时间，单位秒
            int finalThreadResponseDealTimeout = heartBeatInterval * 3;

            //读取获取设备ID响应超时时间，单位秒
            int queryDeviceAddrTimeout = getSystemConfigValue(QUERY_DEVICE_ID_TIMEOUT, "QueryDeviceAddrTimeout");
            log.info("获取设备ID响应超时时间，单位秒:" + queryDeviceAddrTimeout);
            int finalQueryDeviceAddrTimeout = queryDeviceAddrTimeout;

            ServerSocket serverSocket = new ServerSocket(serverPort);
            //创建线程池
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maxDeviceCount,
                    maxPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue,
                    new ThreadPoolExecutor.AbortPolicy());

            //线程池监控
            Thread monitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        //休眠
                        try {
                            Thread.sleep(finalMonitorThreadInterval);
                        } catch (InterruptedException e) {
                            log.info("monitorThread线程休眠异常", e);
                        }
                        log.info("线程池已执行和未执行的任务总数：" + threadPoolExecutor.getTaskCount());
                        log.info("线程池已完成的任务数量：" + threadPoolExecutor.getCompletedTaskCount());
                        log.info("线程池当前线程数量：" + threadPoolExecutor.getPoolSize());
                        log.info("线程池中正在执行任务的线程数量：" + threadPoolExecutor.getActiveCount());
                        log.info("线程池当前排队线程数：" + threadPoolExecutor.getQueue().size());
                        log.info("线程池设置的核心线程数量:" + threadPoolExecutor.getCorePoolSize());
                        log.info("线程池设置的最大的线程数" + threadPoolExecutor.getMaximumPoolSize());
                        ScoketServerTKUtils.logThreadPool();
                    }
                }
            });
            monitorThread.start();

            while (true) {
                log.info("scoket服务器等待连接");

                try {
                    // 有客户端向服务器发起tcp连接时，accept会返回一个Socket
                    // 该Socket的対端就是客户端的Socket
                    // 具体过程可以查看TCP三次握手过程
                    Socket socket = serverSocket.accept();
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(finalThreadResponseDealTimeout*1000);//设置超时属性
                    //log.info("scoket处理客户端所发信息>>>>>>>>>>>>>" + socket);
                    InetAddress getInetAddress = socket.getInetAddress();
                    // log.info("getInetAddress==============" + getInetAddress);
                    Integer getLocalPort = socket.getLocalPort();
                    //log.info("getLocalPort====" + getLocalPort);


                    // 利用线程池，启动线程
                    ((ExecutorService) threadPoolExecutor).execute(new Runnable() {
                        @Override
                        public void run() {
                            long threadId = Thread.currentThread().getId();
                            log.info("当前线程ID:" + threadId);
                            // 使用局部引用，防止connection被回收
                            Socket conn = socket;

                            try{
                                SocketHelp socketHelp = new SocketHelp(conn, threadId,
                                        System.currentTimeMillis() / 1000,
                                        finalThreadResponseDealTimeout,
                                        finalQueryDeviceAddrTimeout);
                                //pc向发卡器发送请求卡号命令
                                //读设备ID
                                socketHelp.readDeviceAddr();

                                //循环读命令处理
                                socketHelp.dealCommand();
                            }catch (Exception e){
                                log.info("线程ID:" + threadId + "异常", e);
                            }finally {
                                log.info("线程ID:" + threadId + " 退出关闭");
                                try {
                                    conn.close();
                                } catch (IOException e1) {
                                    log.info("线程ID:" + threadId + " 关闭socket异常", e1);
                                }
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
     * 获取线程池配置
     * @param defaultValue 默认配置
     * @param systemConfigKey 数据库字段
     * @return
     */
    private int getSystemConfigValue(int defaultValue, String systemConfigKey) {
        int systemConfigValue = defaultValue;
//        String sSystemConfigValue = sysService.queryValueByKey(systemConfigKey);
//        if (sSystemConfigValue != null) {
//            try {
//                systemConfigValue = Integer.valueOf(sSystemConfigValue);
//            } catch (NumberFormatException e) {
//                log.info("读取系统配置[" + systemConfigKey + "]错误：" + sSystemConfigValue
//                        + " 使用缺省配置:" + defaultValue);
//            }
//        }
        return systemConfigValue;
    }

    /**
     * byte[]数据结果
     */
    class ByteDataResult {
        private byte[] data;
        private int length;

        ByteDataResult(byte[] data, int length) {
            this.data = data;
            this.length = length;
        }

        public byte[] getData() {
            return data;
        }

        public int getLength() {
            return length;
        }

    }

    /**
     * 命令字类型枚举
     * OTHER-其他
     * HAND_SHAKE-握手
     * SYNCHRONIZATION_OF_THE_SECRET_KEY-同步密钥
     * QUERY_KEY-查询密钥
     * SYNCHRONIZE_WHITELIST-同步白名单
     * QUERY_WHITELIST-查询白名单
     * REPORT_TRAFFIC_RECORD-上报通行记录
     * ONLINE_VERIFICATION-在线验证
     * CONTROL_DEVICE-控制设备
     * QUERY_DEVICE_STATUS-查询设备状态
     * SET_DEVICE_STATUS-设置设备状态
     * BACKGROUND_SYNCHRONIZATION_CLOCK-后台同步时钟
     * DEVICE_SYNCHRONIZATION_CLOCK-设备同步时钟
     * UPDATE_FIRMWARE-更新固件
     */

    public enum CommandWordType {
        OTHER,
        REPORT,
        ISSUED,
        HAND_SHAKE,

    }


    /**
     * 发送指令
     * @return 成功与否
     */

    public static boolean sendCommand(String id,String interpret,String json) throws IOException {
        System.out.println("id="+id+",interpret="+interpret);
        System.out.println(SockethashMap.get("195"));

        Socket connection = (Socket) SockethashMap.get(id);
        System.out.println("connection="+connection);
        if (connection!=null) {
            System.out.println("设备[" + id + "] sendCommand "+"发送一次" + interpret + "的命令");
            // 向客户端写出处理后的字符串
            OutputStream out = connection.getOutputStream();
            System.out.println("发送的数据位----="+getPublicCommand(id,interpret,json));
            out.write(ByteUtils.hex2Byte(getPublicCommand(id,interpret,json)));
            out.flush();
            return true;
        }else {
            return false;
        }
    }
///**
// * 生成握手指定固定
// *
// */
//    public static String shakeHands(){
//        return "55 AA 80 00 00 7F";
//    }
    /**
     * 生成指令
     *
     */
    public static String getPublicCommand(String id,String interpret, String json){
        String command ="";
        JSONObject object = new JSONObject();
        String length;

        switch (interpret) {
            case "HAND_SHAKE":
//
                break;
           case "REPORT":
               command = "00";
               length = ByteUtils.int2HexStr(ByteUtils.hexDecode(command).length).substring(18-4);
               length = length.substring(2,4) +length.substring(0,2);
               command = "81" + length+command;
                 break;
            case "ISSUED":
                command = json;
                length = ByteUtils.int2HexStr(ByteUtils.hexDecode(command).length).substring(18-4);
                length = length.substring(2,4) +length.substring(0,2);
                command = "82"+length+command;
                break;
            default:
                return "";
        }
        System.out.println("设备[" + id + "] getPublicCommand Json组装内容："
                + object.toJSONString());
        //		命令头+ 命令字 + 标识字 + 长度字+ 数据域+ 校验字
        return "55AA"+command + ByteUtils.byte2Hex(ByteUtils.bytesXorCrc(ByteUtils.hexDecode("55AA"+command)));
    }


    /**
     * Socket操作辅助工具类
     */
    class SocketHelp {
        @Autowired
        private ICardDao cardDao;
        @Autowired
        private IDeviceDao deviceDao;
        @Autowired
        private IPersonDao personDao;
        @Autowired
        private IRuleDao ruleDao;


        //命令头长度，2字节 0x55AA
        final private static int DATA_HEAD_LENGTH = 2;
        //读数据超时时间,单位毫秒
        final private static int READ_DATA_TIMEOUT = 5000;
        //连续读数据超时时间,单位毫秒
        final private static int SEQUENCE_READ_DATA_TIMEOUT = 200;

        //线程处理响应超时时间，单位秒
        private int threadResponseDealTimeout;
        //获取设备ID响应超时时间，单位秒
        private int queryDeviceAddrTimeout;




        private Socket socket;
        private long threadId = 0;
        private String DeviceAddr = "";//此业务逻辑中为设备号
        private long createTime = 0;
        //最后响应时间
        private long lastResponseTime = 0;

        public SocketHelp(Socket socket, long threadId, long createTime,
                          int threadResponseDealTimeout, int queryDeviceAddrTimeout) {
            this.socket = socket;
            this.threadId = threadId;
            this.createTime = createTime;
            this.lastResponseTime = createTime;
            this.threadResponseDealTimeout = threadResponseDealTimeout;
            this.queryDeviceAddrTimeout = queryDeviceAddrTimeout;
            this.cardDao=BeanContext.getApplicationContext().getBean(ICardDao.class);
            this.deviceDao=BeanContext.getApplicationContext().getBean(IDeviceDao.class);
            this.personDao=BeanContext.getApplicationContext().getBean(IPersonDao.class);
            this.ruleDao=BeanContext.getApplicationContext().getBean(IRuleDao.class);

        }

        /**
         * 读设备ID
         */
        public void readDeviceAddr() throws IOException {
            OutputStream out = socket.getOutputStream();
            out.write(ByteUtils.hex2Byte("55AA8000007F"));
            out.flush();
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.info("线程[" + threadId + "] readDeviceID() 线程休眠异常", e);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.info("线程[" + threadId + "] readDeviceID() 线程休眠异常", e);
                }

                //读到命令回复
                int result = readDataHead();
                if (result == 0) {
                    //log.info("线程[" + threadId + "] readDeviceID() 读命令回复超时");
                    continue;
                }
                if (result < 0) {
                    log.info("线程[" + threadId + "] readDeviceID() 读命令回复错误");
                    break;
                }
                if (result == 2) {
                    //log.info("线程[" + threadId + "] readDeviceID() 是心跳信息，忽略");
                    continue;
                }
                if (result != 1) {
                    //log.info("线程[" + threadId + "] readDeviceID() 读命令头不是命令");
                    continue;
                }
                //读命令回复类型
                CommandWordType commandWordType = readCommandWord();

                int length = readCommandDataLength();

                ByteDataResult deviceIdByteDataResult = readCommandResultData(length);

                //读取Bcc
                byte bcc = readCommandBcc();

                //不是读设备ID回复，忽略
                if (commandWordType != CommandWordType.HAND_SHAKE) {
                    log.info("线程[" + threadId + "] readDeviceID() 不是读设备ID回复，忽略");
                    continue;
                }

                if (length < 1) {
                    log.info("线程[" + threadId + "] readDeviceID() 读设备ID命令结果长度小于1，失败");
                    continue;
                }

                if (deviceIdByteDataResult == null) {
                    log.info("线程[" + threadId + "] readDeviceID() 读设备ID命令结果数据失败");
                    continue;
                }

                //BCC校验
                byte[] composeData = composeDataByte(commandWordType, deviceIdByteDataResult.getData(), length);

                if (!validBcc(composeData, composeData.length, bcc)) {
                    log.info("线程[" + threadId + "] readDeviceID() 读设备ID命令BCC校验失败");
                    continue;
                }

                //获取卡号
                //log.info("读取到的卡号二进制="+Arrays.toString(deviceIdByteDataResult.getData()));
                log.info("读取到的卡号转为16="+ByteUtils.byte2Hex(deviceIdByteDataResult.getData()));
//                int s=deviceIdByteDataResult.getData()[0] | (deviceIdByteDataResult.getData()[1] << 8)|
//                        (deviceIdByteDataResult.getData()[2] << 16) | (deviceIdByteDataResult.getData()[3] << 24);
                Integer s=0;
                for (int i = 0; i < 4; i++) {
                    int x = deviceIdByteDataResult.getData()[i];
                    System.out.println(x);
                    if (x < 0) {
                        x += 256;
                    }
                    s |= (x << (i * 8));
                }

                //Integer x=s;
                DeviceAddr = s.toString();
                log.info("设备号---"+s);
//                JSONObject object = JSONObject.parseObject(json);
//                DeviceAddr = (String) object.get("DeviceAddr");
//                sn = (Integer) object.get("SerialNo");

            } while (DeviceAddr.equals("") && !threadLinkTimeOut());

            if (!DeviceAddr.equals("")) {
                log.info("线程[" + threadId + "] readDeviceID() 读到设备ID：" + DeviceAddr);

//                判断设备ID是否在系统中

                log.info("读取到的卡号为="+DeviceAddr);

                Device device = deviceDao.queryDeviceByDeviceIdentification(DeviceAddr);
//                 判断当前id是否存在
                if (device == null) {
                    log.info("线程[" + threadId + "] id不存在系统中,不做处理");
                } else {
                    //DeviceAddr = device.getDeviceAddress();
                    log.info("线程[" + threadId + "] readDeviceID() 设备连接服务器，记录在列表中");
//                    设备记录日志发
                    DeviceThreadInfo deviceThreadInfo =
                            new DeviceThreadInfo(threadId, DeviceAddr, createTime);
                    DeviceListUtil.getInstance().add(deviceThreadInfo);
                    SockethashMap.put(DeviceAddr, socket);
                    //sendCommand(DeviceAddr,"hand",null,sn,null,null,null,null,null,null);
                }
            } else {
                log.info("线程[" + threadId + "] 读ID超时");

            }
        }

        /**
         * 循环读命令处理
         * @throws IOException
         * @throws FileNotFoundException
         * @throws InterruptedException
         */
        public void dealCommand() throws FileNotFoundException, IOException, InterruptedException {
            if (DeviceAddr == null || DeviceAddr.equals("")) {
                log.info("线程[" + threadId + "] dealCommand() 设备ID没有获取");
                return;
            }
            log.info("循环读命令处理	");
            while (!threadDealTimeOut()) {
                // 休眠
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "]  dealCommand() 线程休眠异常", e);
                }

                // 读到命令回复
                int result = readDataHead();
                if (result < 0) {
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 读命令回复错误");
                    break;
                }

                if (result == 0) {
                     log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 读命令回复超时");
                    continue;
                }

                // 更新最后处理时间
                lastResponseTime = (System.currentTimeMillis() / 1000);

                if (result == 2) {
                     log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 是心跳信息，忽略");
                    continue;
                }

                if (result != 1) {
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 不是命令信息，忽略");
                    continue;
                }

                // 读命令回复类型
                CommandWordType commandWordType = readCommandWord();

                int length = readCommandDataLength();
                ByteDataResult reportByteDataResult = readCommandResultData(length);

                // 读取Bcc
                byte bcc = readCommandBcc();
                //System.out.println("-------------------------------------"+DeviceAddr);

                if (length < 1) {
                    //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 键值上报结果长度小于1，失败");
                    continue;
                }

                if (reportByteDataResult == null) {
                    //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 键值上报命令数据失败");
                    continue;
                }
                byte[] composeData = composeDataByte(commandWordType, reportByteDataResult.getData(), length);
                if (!validBcc(composeData, composeData.length, bcc)) {
                    //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 读设备ID命令BCC校验失败");
                    continue;
                }
                // 处理数据
                String json = ByteUtils.convertHexToString(ByteUtils.byte2Hex(reportByteDataResult.getData()));
//                JSONObject object = JSONObject.parseObject(json);
//                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand Json内容解析："
//                        + object.toJSONString());
//                sn = (Integer) object.get("SerialNo");

                switch (commandWordType){
                    case HAND_SHAKE:
                        break;
                    case ISSUED:
                        String key2="sentSuccessfully";
                        String S="55AA"+"82"+ByteUtils.byte2Hex(ByteUtils.unlong2H2bytes(length))+ByteUtils.byte2Hex(reportByteDataResult.getData())+bcc;
                        System.out.println("读取到的数据进行拼接==="+S);
                        jedisManager.setValueByStr(0, key2, S, 15);
                        break;
                    case REPORT:
                        System.out.println("读取到的数据头"+result);
                        System.out.println("字节数"+length);
                        System.out.println("数据"+ByteUtils.byte2Hex(reportByteDataResult.getData()));
                        System.out.println("校验字"+bcc);
                        String reportData="55AA"+"81"+ByteUtils.byte2Hex(ByteUtils.unlong2H2bytes(length))+ByteUtils.byte2Hex(reportByteDataResult.getData())+bcc;
                        System.out.println("读取到的数据进行拼接==="+reportData);
                        String key=DeviceAddr;
                        jedisManager.setValueByStr(0, key, reportData, 15);
                        sendCommand(DeviceAddr,"REPORT",json);
                        break;
                    default:
                }
            }
            //设备离线更新数据库；
            //deviceMapper.updateOl(1,DeviceAddr);



            //清理，退出线程
            DeviceListUtil.getInstance().del(threadId);
            log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] dealCommand() 清理线程ID");
            log.info("记录设备掉线清除:"+DeviceAddr);
        }


        /**
         *
         * @param commandWordType 组装指令
         * @param data 数据域
         * @param dataLength 数据域长度
         * @return 组装指令
         */
        private byte[] composeDataByte(CommandWordType commandWordType, byte[] data, int dataLength) {
            byte[] resultData = new byte[6 + dataLength];
            resultData[0] = (byte) 0x55;
            resultData[1] = (byte) 0xAA;
            if (CommandWordType.HAND_SHAKE == commandWordType) {
                resultData[2] = (byte) 0x80;
            } else if (CommandWordType.REPORT == commandWordType) {
                resultData[2] = (byte) 0x81;
            } else if(CommandWordType.ISSUED == commandWordType){
                resultData[2] = (byte) 0x82;
            }
            resultData[3] = (byte) (dataLength % 256);
            resultData[4] = (byte) (dataLength / 256);

            for (int i = 0; i < dataLength; i++) {
                resultData[5 + i] = data[i];
            }

            return resultData;
        }

        /**
         * 校验BCC
         *
         * @param data
         * @param length
         * @param bcc
         * @return
         */
        private boolean validBcc(byte[] data, int length, byte bcc) {
            byte[] result = ByteUtils.bytesXorCrc(data);
            //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] validBcc() 校验BCC，result:" + result[0] + " bcc:" + bcc);
            return result[0] == bcc;
        }

        /**
         * 判断线程是否超时
         *
         * @return 是否超时
         */
        private boolean threadLinkTimeOut() {
            return ((System.currentTimeMillis() / 1000) - lastResponseTime > queryDeviceAddrTimeout);
        }

        /**
         * 判断线程是否超时
         *
         * @return
         */
        private boolean threadDealTimeOut() {
            return ((System.currentTimeMillis() / 1000) - lastResponseTime > threadResponseDealTimeout);
        }


        /**
         * 读数据
         *
         * @param length  int 预期读取数据长度
         * @param timeout int 读取超时时间，单位毫秒
         * @return
         */
        private ByteDataResult readData(int length, int timeout) {
            try {
                socket.setSoTimeout(timeout);
                InputStream in = socket.getInputStream();
                byte[] data = new byte[length];
                int readLength = in.read(data);

                ByteDataResult byteDataResult = new ByteDataResult(data, readLength);

                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readData 读取数据，预期长度：" + length + " 实际数据长度：" + byteDataResult.length);
                if (byteDataResult.length > 0) {
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readData 读取数据，数据内容：" + ByteUtils.byte2Hex(byteDataResult.getData()));
                }

                return byteDataResult;
            } catch (SocketTimeoutException e) {
                try {
                    socket.sendUrgentData(0xFF); // 发送心跳包
                    // 更新最后处理时间
                    System.out.println("----------------------刷新超时时间");
                    lastResponseTime = (System.currentTimeMillis() / 1000);
                } catch (IOException ex) {
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readData(" + length + "," + timeout + ") 读取数据SocketTimeoutException异常：");
                }
                return new ByteDataResult(null, 0);
            } catch (SocketException e) {
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readData(" + length + "," + timeout + ") 读取数据SocketException异常：", e);
                return new ByteDataResult(null, -1);
            } catch (IOException e) {
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readData(" + length + "," + timeout + ") 读取数据IOException异常：", e);
                return new ByteDataResult(null, -2);
            }

        }

        /**
         * 读取数据头
         *
         * @return int <0,失败; 0-超时;  >0 有数据， 1-命令， 2-心跳， 3-其它
         */
        private int readDataHead() {
            ByteDataResult headByteDataResult = readData(DATA_HEAD_LENGTH, READ_DATA_TIMEOUT);
            //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据，数据长度：" + headByteDataResult.length);
            if (headByteDataResult.length > 0) {
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据，数据内容：" + ByteUtils.byte2Hex(headByteDataResult.getData()));
            }

            if (headByteDataResult.length < 0) {//错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据头错误");
                return -1;
            } else if (headByteDataResult.length == 0) {//超时
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据头超时");
                return 0;
            } else if (headByteDataResult.length == DATA_HEAD_LENGTH) {
                if (headByteDataResult.getData()[0] == (byte) 0x55 && headByteDataResult.getData()[1] == (byte) 0xAA) { //是数据头
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据头成功，是数据头");
                    return 1;
                } else if (headByteDataResult.getData()[1] == (byte) 0x55) {  //可能是数据头，再读一位
                    do { //连续读取，直到失败
                        ByteDataResult oneByteDataResult = readData(1, SEQUENCE_READ_DATA_TIMEOUT);
                        log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取，数据长度：" + oneByteDataResult.length);
                        if (oneByteDataResult.length > 0) {
                            log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取，数据内容：" + ByteUtils.byte2Hex(oneByteDataResult.getData()));
                        }
                        if (oneByteDataResult.length < 0) { //连续读取错误
                            log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取数据错误");
                            return -1;
                        } else if (oneByteDataResult.length == 0) { //连续读取超时
                            log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取数据超时");
                            return 3;
                        } else if (oneByteDataResult.length == 1) { //连续读取成功
                            if (headByteDataResult.getData()[0] == (byte) 0xAA) {
                                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取数据成功，是数据头");
                                return 1;
                            } else if (headByteDataResult.getData()[0] == (byte) 0x55) { //下一位可能是数据头，继续读
                                continue;
                            } else { //不是数据头
                                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取数据成功，但不是数据头");
                                return 3;
                            }
                        } else { //错误
                            log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 连续读取数据错误");
                            return 3;
                        }
                    } while (true);
                } else { //不是数据头
                    log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据成功，但不是数据头");
                    return 3;
                }
            } else { //错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取数据错误");
                return 3;
            }

        }

        /**
         * 读取命令字
         *
         * @return CommandWordType；
         * OTHER-其他
         * HAND_SHAKE-握手
         * SYNCHRONIZATION_OF_THE_SECRET_KEY-同步密钥
         * QUERY_KEY-查询密钥
         * SYNCHRONIZE_WHITELIST-同步白名单
         * QUERY_WHITELIST-查询白名单
         * REPORT_TRAFFIC_RECORD-上报通行记录
         * ONLINE_VERIFICATION-在线验证
         * CONTROL_DEVICE-控制设备
         * QUERY_DEVICE_STATUS-查询设备状态
         * SET_DEVICE_STATUS-设置设备状态
         * BACKGROUND_SYNCHRONIZATION_CLOCK-后台同步时钟
         * DEVICE_SYNCHRONIZATION_CLOCK-设备同步时钟
         * UPDATE_FIRMWARE-更新固件
         */
        private CommandWordType readCommandWord() {
            ByteDataResult commandWordByteDataResult = readData(1, SEQUENCE_READ_DATA_TIMEOUT);
            //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandWord 读取数据，数据长度：" + commandWordByteDataResult.length);
            if (commandWordByteDataResult.length > 0) {
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandWord 读取数据，数据内容：" + ByteUtils.byte2Hex(commandWordByteDataResult.getData()));
            }

            if (commandWordByteDataResult.length < 1) {//长度不够或超时，错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandWord 读取命令字失败");
                return CommandWordType.OTHER;
            } else if (commandWordByteDataResult.length == 1) {  //判断类型
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 读取命令字成功");
                CommandWordType commandWordType = CommandWordType.OTHER;
                byte topic = commandWordByteDataResult.getData()[0];
                switch (commandWordByteDataResult.getData()[0]) {
                    case (byte) 0x80:
                        //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 是握手请求");
                        commandWordType = CommandWordType.HAND_SHAKE;
                        break;
                    case (byte) 0x81:
                        //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 是握手请求");
                        commandWordType = CommandWordType.REPORT;
                        break;
                    case (byte) 0x82:
                        //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 是握手请求");
                        commandWordType = CommandWordType.ISSUED;
                        break;
                    default:
                        //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readDataHead 是其它命令");
                        commandWordType = CommandWordType.OTHER;
                        break;

                }
                return commandWordType;
            } else { //错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandWord 读取命令字错误");
                return CommandWordType.OTHER;
            }

        }

        /**
         * 读取命令bcc
         *
         * @return int <0 错误；>=0 长度
         */
        private byte readCommandBcc() {
            ByteDataResult commandBccByteDataResult = readData(1, SEQUENCE_READ_DATA_TIMEOUT);
            // log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandBcc 读取数据，数据长度：" + commandBccByteDataResult.length);
            if (commandBccByteDataResult.length > 0) {
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandBcc 读取数据，数据内容：" + ByteUtils.byte2Hex(commandBccByteDataResult.getData()));
            }

            if (commandBccByteDataResult.length < 1) {//长度不够或超时，错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandBcc 读取命令bcc失败");
                return 0;
            } else if (commandBccByteDataResult.length == 1) {  //判断成功失败
                return commandBccByteDataResult.getData()[0];
            } else { //错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandBcc 读取命令bcc错误");
                return -1;
            }

        }

        /**
         * 读取命令数据长度
         *
         * @return int <0 错误；>=0 长度
         */
        private int readCommandDataLength() {
            ByteDataResult commandDataLengthByteDataResult = readData(2, SEQUENCE_READ_DATA_TIMEOUT);
            // log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取数据，数据长度：" + commandDataLengthByteDataResult.length);
            if (commandDataLengthByteDataResult.length > 0) {
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取数据，数据内容：" + ByteUtils.byte2Hex(commandDataLengthByteDataResult.getData()));
            }

            if (commandDataLengthByteDataResult.length < 0) {//错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取命令数据长度错误");
                return -1;
            } else if (commandDataLengthByteDataResult.length < 2) {//长度不够或超时
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取命令数据长度不够或超时");
                return 0;
            } else if (commandDataLengthByteDataResult.length == 2) {  //判断类型
                // log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取命令数据长度成功");
                int length = ((commandDataLengthByteDataResult.getData()[1] & 0xff) << 8);
                length += ((commandDataLengthByteDataResult.getData()[0] & 0xff));
                return length;
            } else { //错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandDataLength 读取命令数据长度错误");
                return -2;
            }

        }

        /**
         * 读取命令结果数据
         *
         * @param length int 预期读取数据长度
         * @return DataResult
         */
        private ByteDataResult readCommandResultData(int length) {
            ByteDataResult commandByteDataResult = readData(length, SEQUENCE_READ_DATA_TIMEOUT);
            //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandResultData 读取数据，数据长度：" + commandByteDataResult.length);
            if (commandByteDataResult.length > 0) {
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandResultData 读取数据，数据内容：" + ByteUtils.byte2Hex(commandByteDataResult.getData()));
            }
            if (commandByteDataResult.length < length) {//长度不够或超时，错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandResultData 读取命令结果数据失败");
                return null;
            } else if (commandByteDataResult.length == length) {  //成功
                //log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandResultData 读取命令结果数据成功");
                return commandByteDataResult;
            } else { //错误
                log.info("线程[" + threadId + "] 设备[" + DeviceAddr + "] readCommandResultData 读取命令结果数据错误");
                return null;
            }

        }



    }

    public static void logThreadPool() {
        //开始打印设备连接池信息
        ArrayList<DeviceThreadInfo> DeviceThreadInfos = DeviceListUtil.getInstance().query();
        System.out.println("[设备连接池]开始输出连接池信息");
        System.out.println("[设备连接池]当前连接数：" + DeviceThreadInfos.size());
        for (int i = 0; i < DeviceThreadInfos.size(); i++) {
            DeviceThreadInfo deviceThreadInfo = DeviceThreadInfos.get(i);
            System.out.println("[设备连接池]连接" + (i+1) + "信息：线程ID " + deviceThreadInfo.getThreadId()
                    + " 设备ID " + deviceThreadInfo.getDeviceAddr()
                    + " 创建时间 " + SimpleDateFormat.getInstance().format(new Date(deviceThreadInfo.getCreateTime() * 1000L)));

        }
        System.out.println("[设备连接池]结束输出连接池信息");

    }
    /**
     * 设备在线列表处理类
     * @author yuxd
     */
    static class DeviceListUtil {
        private volatile static DeviceListUtil instance = null;

        public static Map<Long, DeviceThreadInfo> deviceThreadMap = new ConcurrentHashMap<>(300);

        /**
         * 私有化构造方法
         */
        private DeviceListUtil() {

        }

        public static DeviceListUtil getInstance() {
            if (instance == null) {
                synchronized (DeviceListUtil.class) {
                    if (instance == null) {
                        instance = new DeviceListUtil();
                    }
                }

            }
            return instance;
        }

        public void add(DeviceThreadInfo deviceThreadInfo){
            //查询是否已存在
            DeviceThreadInfo oldValue = deviceThreadMap.get(deviceThreadInfo.getThreadId());

            if (null == oldValue) {
                //增加
                deviceThreadMap.putIfAbsent(deviceThreadInfo.getThreadId(), deviceThreadInfo);

            } else {
                //替换
                deviceThreadMap.replace(deviceThreadInfo.getThreadId(), oldValue, deviceThreadInfo);
            }
        }

        public void del(long threadId){
            deviceThreadMap.remove(threadId);
        }

        public ArrayList<DeviceThreadInfo> query(){
            ArrayList<DeviceThreadInfo> deviceThreadInfoArrayList = new ArrayList<>();

            for(Long key : deviceThreadMap.keySet()) {
                deviceThreadInfoArrayList.add(deviceThreadMap.get(key));
            }

            return deviceThreadInfoArrayList;
        }
    }

    /**
     * 设备线程信息类
     *
     * @author yuxd
     */
    static class DeviceThreadInfo {
        //线程ID
        private long threadId;
        //设备ID
        private String DeviceAddr;
        //创建时间
        private long createTime;


        public DeviceThreadInfo(long threadId, String DeviceAddr, long createTime) {
            this.threadId = threadId;
            this.DeviceAddr = DeviceAddr;
            this.createTime = createTime;
        }

        public long getThreadId() {
            return threadId;
        }

        public String getDeviceAddr() {
            return DeviceAddr;
        }

        public long getCreateTime() {
            return createTime;
        }
    }

}
