package com.vguang.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangsir
 *
 *         2017年9月26日
 */
public class RoleCache implements ICache {
	private static final Logger log = LoggerFactory.getLogger(RoleCache.class);

	private Map<String, String> rolemap = new HashMap<String, String>();
	
	public RoleCache() {
		super();
	}

	public RoleCache(Map<String, String> perrolemap) {
		if (perrolemap == null) {
			throw new IllegalArgumentException("rolemap can't not be null.");
		}

		this.rolemap = perrolemap;
	}

	public String get(String key) throws CacheException {
		return rolemap.get(key);
	}

	public String put(String key, String value) throws CacheException {
		return rolemap.put(key, value);
	}

	public String remove(String key) throws CacheException {
		return rolemap.remove(key);
	}

	public void clear() throws CacheException {
		rolemap.clear();
	}

	public Map<String, String> getRolemap() {
		return rolemap;
	}

	public void setRolemap(Map<String, String> rolemap) {
		this.rolemap = rolemap;
	}
	
	

}
