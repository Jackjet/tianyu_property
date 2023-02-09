/**
 * 
 */
package com.vguang.init.task;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.vguang.utils.ScoketServer;
import com.vguang.utils.StringUtil;

/**
 * @author Dingjz
 *
 * @date 2018年7月13日
 */
public class CaptchaTask extends Thread {
	private static final Logger log = LoggerFactory.getLogger(CaptchaTask.class);
	private final String path = "/usr/local/nginx/html/captcha/upgrade-test";
	private final long delay = 30 * 1000;
	private final long period = 60 * 1000;

	@Override
	public void run() {
		super.run();
		log.info(StringUtil.logStr("开启验证码定时删除线程，监听路径:{}"), path);
		File file = new File(path);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				deletefiles(file);
//				log.info("走一次握手指令");
//				try {
//					int handshake_result = ScoketServer.handshake();
//					log.info("心跳返回值============="+handshake_result);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			
			}
		}, delay, period);

	}

	/**
	 * 批量删除文件
	 * 
	 * @param folder
	 */
	public void deletefiles(File folder) {
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteFile(files[i]);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		try {
			if (file.isFile()) {
				// 删除符合条件的文件
				if (canDeleteFile(file)) {
					if (file.delete()) {
						log.info(StringUtil.logStr("文件" + file.getName() + "删除成功!"));
					} else {
						log.info(StringUtil.logStr("文件" + file.getName() + "删除失败!"));
					}
				}
			}
		} catch (Exception e) {
			log.info(StringUtil.logStr("删除文件失败"));
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件是否能够被删除
	 */
	private boolean canDeleteFile(File file) {
		// 文件名必须以png结尾且最后修改时间大于30分钟才能被删除
		String fileName = file.getName();
//		log.info(StringUtil.logStr("fileName:{}"), fileName);
		if (!fileName.endsWith(".png")) {
			return false;
		}

		Date modeDate = getFileModDate(file);
		Date nowDate = new Date();
		long time = ((nowDate.getTime() - modeDate.getTime()) / 1000 / 60);// 当前时间与文件间隔的分钟
//		log.info(StringUtil.logStr("该文件时间时间间隔为:{}"), time);
		if (time > 30) {
			log.info(StringUtil.logStr(fileName+"大于30，删除文件"));
			return true;
		} else {
			log.info(StringUtil.logStr(fileName+"小于30，留它一命"));
			return false;
		}
	}

	/**
	 * 获取文件最后的修改时间
	 * 
	 * @param file
	 * @return
	 */
	private Date getFileModDate(File file) {
		long modifiedTime = file.lastModified();
		Date d = new Date(modifiedTime);
		return d;
	}

}
