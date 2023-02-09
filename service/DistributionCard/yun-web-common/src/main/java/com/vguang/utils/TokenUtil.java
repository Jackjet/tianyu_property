package com.vguang.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.vguang.utils.encrypt.B64Util;

/**
 * 生成唯一token,防止表单重复提交
 * @author wang
 * date 2017-04-12
 */
public class TokenUtil {

	/*
     *单例设计模式（保证类的对象在内存中只有一个）
     *1、把类的构造函数私有
     *2、自己创建一个类的对象
     *3、对外提供一个公共的方法，返回类的对象
     */
    private TokenUtil(){}
    
    private static final TokenUtil instance = new TokenUtil();
    
    public static TokenUtil getInstance(){
        return instance;
    }
    
    /**
     * 生成Token
     * @throws Exception 
     */
    public String makeToken() throws Exception{ 
        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte md5[] =  md.digest(token.getBytes());
            
            return B64Util.encode(md5);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
