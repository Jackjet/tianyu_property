package com.vguang.utils;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author wangsir
 *
 * 2017年11月8日
 */
public class JedisManager {
	private static final Logger log = LoggerFactory.getLogger(JedisManager.class);
	
    private JedisPool jedisPool;

    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (JedisConnectionException e) {
        	if("Could not get a resource from the pool".equalsIgnoreCase(e.getMessage())){
        		log.info("++++++++++请检查你的redis服务++++++++");
        		System.exit(0);//停止项目
        	}
        	throw new JedisConnectionException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jedis;
    }

    public void returnResource(Jedis jedis, boolean isBroken) {
        if (jedis == null)
            return;
        /**
         * @deprecated starting from Jedis 3.0 this method will not be exposed.
         * Resource cleanup should be done using @see {@link redis.clients.jedis.Jedis#close()}
        if (isBroken){
            getJedisPool().returnBrokenResource(jedis);
        }else{
            getJedisPool().returnResource(jedis);
        }
        */
        jedis.close();
    }
    
    /**
     * jis.set(str)/jis.setex(key, seconds, value)
     */
    public void setValueByStr(int dbIndex, String key, String value, int expireTime){
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.set(key, value);
            //设置有效时间
            if (expireTime > 0){
            	jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }
    
    /**
     * 设置 key 的过期时间，key 过期后将不再可用。单位以秒计。
     */
    public void expireStr(int dbIndex, String key, int expireTime) {
    	Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            if (expireTime > 0)
                jedis.expire(key, expireTime);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
	}
    
    /**
     *  将一个或多个成员元素加入到set集合中，已经存在于集合的成员元素将被忽略。
     * jis.sadd(set, value)
     */
    public void saddByStr(int dbIndex, String set, String value){
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.sadd(set, value);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }
    
    
    /**
     * 移除一个元素
     * jis.srem(set, value)
     */
    public void sremByStr(int dbIndex, String set, String value){
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.srem(set, value);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }

    /**
     * 获取指定key的值,如果key不存在返回null，如果该Key存储的不是字符串，会抛出一个错误。
     * jis.get(str)
     */
    public String getValueByStr(int dbIndex, String key){
        Jedis jedis = null;
        String result = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.get(key);
            expireStr(1, key, 1800);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }
    
    /**
     * 获取指定库中的指定KEY.*为所有
     * @param dbIndex
     * @param key
     * @return
     */
     public Set<String> selectIndexKeys(int dbIndex, String key){
            Jedis jedis = null;
            boolean isBroken = false;
            Set<String> keys =null;
            try {
                jedis = getJedis();
                jedis.select(dbIndex);
                 keys = jedis.keys(key);
            } catch (Exception e) {
                isBroken = true;
                throw e;
            } finally {
                returnResource(jedis, isBroken);
            }
            return keys; 
        }
    
    /**
     * 检查给定 key 是否存在，若 key 存在返回 1 ，否则返回 0 。
     * jis.exists(str)
     */
    public boolean existsStr(int dbIndex, String key){
        Jedis jedis = null;
        boolean result = false;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.exists(key);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }
    
    /**
     * 判断成员元素是否是集合的成员。如果成员元素是集合的成员，返回 1 。 如果成员元素不是集合的成员，或 key 不存在，返回 0 。
     * jis.sismember(key, member)
     */
    public boolean sismemberStr(int dbIndex, String set, String value){
        Jedis jedis = null;
        boolean result = false;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.sismember(set, value);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }
    /**
     * 删除已存在的键。不存在的 key 会被忽略。
     * jis.del(str)
     */
    public void delValueByStr(int dbIndex, String key){
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Long result = jedis.del(key);
            log.info("删除Session结果：{}" , result);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }
    
    /**
     * 用于切换到指定的数据库，数据库索引号 index 用数字值指定，以 0 作为起始索引值。
     * @param dbIndex
     * @param key
     * @return
     */
    public byte[] getValueByByte(int dbIndex, byte[] key){
        Jedis jedis = null;
        byte[] result = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.get(key);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }

    public void deleteByByte(int dbIndex, byte[] key) throws Exception {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Long result = jedis.del(key);
            log.info("删除Session结果：{}" , result);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }
    
    /**
     * 删除当前数据库中所有的key
     * @param dbIndex
     * @param key
     * @throws Exception
     */
    public void deleteDB(int dbIndex) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            String result = jedis.flushDB();
            log.info("删除所选数据库中key的个数：{}" , result);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }

    public void saveValueByKey(int dbIndex, byte[] key, byte[] value, int expireTime){
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.set(key, value);
            if (expireTime > 0)
                jedis.expire(key, expireTime);
        } catch (Exception e) {
            isBroken = true;
            throw e;
        } finally {
            returnResource(jedis, isBroken);
        }
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 通过wxsid获取uid
     */
	public Integer getUidByWxsid(int dbIndex, String wxsid) {
		Integer personid = null;
		if(existsStr(1, wxsid)){
			expireStr(1, wxsid, 1800);
			personid = Integer.valueOf(getValueByStr(1, wxsid)) ;
		}
		return personid;
	}
	 /**
     * 通过alisid获取aliUserId
     */
	public Integer getALiUserIdByALisid(int dbIndex, String alisid) {
		Integer personid = null;
		if(existsStr(1, alisid)){
			expireStr(1, alisid, 1800);
			personid = Integer.valueOf(getValueByStr(1, alisid)) ;
		}
		return personid;
	}
	
	//清除redis中多余的key
	 /*public void delValueByStr2(int dbIndex, String[] keys){
	        Jedis jedis = null;
	        boolean isBroken = false;
	        try {
	            jedis = getJedis();
	            jedis.select(dbIndex);
				//jedis.smembers返回集合中的所有的成员。 不存在的集合 key 被视为空集合。
	            //Set<String> smembers = jedis.smembers(keys);
	            //log.info("smembers:" + smembers);
	            Long result = jedis.del(keys);
	            log.info("删除Session结果：{}" , result);
	            Set<String> keys3 = jedis.keys("*");
	            log.info("keys3:" + keys3);
	        } catch (Exception e) {
	            isBroken = true;
	            throw e;
	        } finally {
	            returnResource(jedis, isBroken);
	        }
	    }*/
	

}
