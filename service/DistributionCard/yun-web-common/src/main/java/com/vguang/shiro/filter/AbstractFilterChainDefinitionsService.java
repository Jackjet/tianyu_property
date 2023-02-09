package com.vguang.shiro.filter;

import java.util.Map;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public abstract class AbstractFilterChainDefinitionsService implements IFilterChainDefinitionsService {
	private static final Logger log = LoggerFactory.getLogger(AbstractFilterChainDefinitionsService.class);
	private String definitions = "";
	@Autowired
	private ShiroFilterFactoryBean shiroFilterFactoryBean;

	@Override
	public void intiPermission(Map<String, String> map) {
		log.info("===初始化权限===");
		
		Ini ini = new Ini();
		//加载主过滤器的权限配置
		ini.load(definitions);
		//从map中获取urls对应的Section
		Section section = ini.getSection("urls");

		if (CollectionUtils.isEmpty(section)) {
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME); // 如不存在默认节点切割,则使用空字符转换
		}
		//获取其它的权限配置
		Map<String, String> permissionMap = initOtherPermission(map);
		if(null != permissionMap && !permissionMap.isEmpty()){
			Gson gson = new Gson();
			section.putAll(permissionMap);
			
			System.out.println("动态修改没有成功：" + gson.toJson(section));
		}
		
		log.info("section:{}", new Gson().toJson(section));
		shiroFilterFactoryBean.setFilterChainDefinitionMap(section);
		log.info("initialize shiro permission success...");
	}
	//读取权限
	private Section obtainPermission() {
		Ini ini = new Ini();
		//加载主过滤器的权限配置
		ini.load(definitions);
		//从map中获取urls对应的Section
		Section section = ini.getSection("urls");

		if (CollectionUtils.isEmpty(section)) {
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME); // 如不存在默认节点切割,则使用空字符转换
		}
		
//		//获取其它的权限配置
//		Map<String, String> permissionMap = initOtherPermission();
//		if(null != permissionMap && !permissionMap.isEmpty()){
//			section.putAll(permissionMap);
//		}
		
		return section;
	}

	/**
	 * 更新权限配置
	 */
	@Override
	public void updatePermission() {
		synchronized (shiroFilterFactoryBean) {
			MyShiroFilter shiroFilter = null;
			
			try {
				shiroFilter = (MyShiroFilter) shiroFilterFactoryBean.getObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
			DefaultFilterChainManager filterManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
			
			//清空初始权限
			filterManager.getFilterChains().clear();
//			shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
			
			//重新生成权限
//			shiroFilterFactoryBean.setFilterChainDefinitions(definitions);
			Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
			
			for(Map.Entry<String, String> entry: chains.entrySet()){
				String uri = entry.getKey();
				String value = entry.getValue().trim().replace(" ", ""); 
				
				System.out.println("key:" + uri + ",value:" + value);
				filterManager.createChain(uri, value);
			}
			
			log.debug("update shiro permission success..");
		}
	}

	@Override
	public abstract Map<String, String> initOtherPermission(Map<String, String> map);

	public String getDefinitions() {
		return definitions;
	}

	public void setDefinitions(String definitions) {
		this.definitions = definitions;
	}
	

}
