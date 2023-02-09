//package com.vguang.system;
//
//
//import com.vguang.utils.ScoketServerTKUtils;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.web.context.ServletContextAware;
//
//import javax.servlet.ServletContext;
//
////import com.asiainfo.util.PropertyPlaceholder;
//
//public class ServerSocketListener implements InitializingBean,
//		ServletContextAware {
//
//	// 服务器IP
//	public static final String SERVER_IP = "127.0.0.1";
//
//	// 服务器端口号
//	public static final int SERVER_PORT = 8080;
//
//
//
//
//	@Override
//	public void setServletContext(ServletContext sce) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println("scoket服务端___-------------------------------------------------------------------------------");
//				//ScoketServer toUpperTCPNonBlockServer = new ScoketServer();
////				toUpperTCPNonBlockServer.TcpTestOfclient(SERVER_IP, SERVER_PORT);//开启TCP服务 ,端口为8686
//				//toUpperTCPNonBlockServer.startServer(SERVER_IP, SERVER_PORT);//开启TCP服务 ,端口为8686
////				toUpperTCPNonBlockServer.UDPTest();
////				ScoketServerUtils to59TCP=new ScoketServerUtils();
////				to59TCP.startServer();
//				ScoketServerTKUtils weigeng=new ScoketServerTKUtils();
//				weigeng.startServer();
//			}
//		}).start();
//	}
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//
//	}
//
//}
