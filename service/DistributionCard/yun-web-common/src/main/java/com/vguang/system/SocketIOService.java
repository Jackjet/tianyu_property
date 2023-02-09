package com.vguang.system;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
//@Component
public class SocketIOService implements ApplicationListener<ContextRefreshedEvent>{
	
	/**
	 * 创建Socket，并设置监听端口
	 */
	public SocketIOService() {}

	private void startServer() {}

	/**
	 * 在root Application context容器启动后,执行以下方法
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {}
}
