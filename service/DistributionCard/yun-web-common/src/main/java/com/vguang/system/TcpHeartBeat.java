package com.vguang.system;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.vguang.utils.ScoketServer;

public class TcpHeartBeat {
	@Autowired
	private SystemConfigs sysConfigs;

	private static final Logger log = LoggerFactory.getLogger(TcpHeartBeat.class);

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				log.info("开始TCP心跳");
				try {
					int handshake_result = ScoketServer.handshake();
					log.info("心跳返回值============="+handshake_result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				log.info("同步消息耗时：{}毫秒", System.currentTimeMillis() - start);
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				log.info("SysConfigs:{}", sysConfigs == null);
				String cron = null;
				if (null != sysConfigs) {
					// 定时任务
					cron = "0 0/10 * * * ?";
					log.info("cron:{}", cron);
				}
				// 任务触发，可修改任务的执行周期
				CronTrigger trigger = new CronTrigger(cron);
				Date nextExec = trigger.nextExecutionTime(triggerContext);
				return nextExec;
			}
		});

	}

}
