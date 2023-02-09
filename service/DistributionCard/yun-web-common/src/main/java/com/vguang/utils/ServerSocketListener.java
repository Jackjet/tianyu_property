package com.vguang.utils;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

//import com.asiainfo.util.PropertyPlaceholder;

public class ServerSocketListener implements InitializingBean,
		ServletContextAware {
	private static final Logger log = LoggerFactory.getLogger(ServerSocketListener.class);
	
	// 服务器IP
	public static final String SERVER_IP = "127.0.0.1";

	// 服务器端口号
	public static final int SERVER_PORT = 8686;

	@Autowired
	private ScoketServerTKUtils toUpperTCPNonBlockServer;

	
	@Override
	public void setServletContext(ServletContext sce) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("scoket服务端___-------------------------------------------------------------------------------");
				toUpperTCPNonBlockServer.startServer();
			}
		}).start();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
