package com.vguang.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListUtil {
	private static final Logger log = LoggerFactory.getLogger(ListUtil.class);
	public static  List Goheavy (ArrayList list) {
		log.info("list查找重复数据");
		List repeatList = new ArrayList<>();//用于存放重复的元素的list
		if(list.size() ==1) {
			log.info("list长度为1"+list.toString());
		}else {
	        for (int i = 0; i < list.size() - 1; i++) {
	            for (int j = list.size() - 1; j > i; j--) {
	                if (list.get(j).equals(list.get(i))) {
	                	log.info("相同的元素为"+list.get(i));
	                	if(list.get(i)!= ""&&!list.get(i).equals("")) {
	                		repeatList.add(list.get(j));//把相同元素加入list(找出相同的)
	                	}

	                }
	            }
	        }
	        log.info("repeatList==="+repeatList);
	        for (Object ss : repeatList) {
				log.info("ss============="+ss);
			}	
		}
		
    return repeatList;
	}
	
	/**
	 * list转Integer数组
	 * @param attendApplyIDS1
	 * @return
	 */
	public static  Integer[] listToIntegerarray (List<Integer> attendApplyIDS1) {
		Integer[] array = new Integer[attendApplyIDS1.size()];
		for (int i = 0; i < attendApplyIDS1.size(); i++) {
			 Integer a = (Integer) attendApplyIDS1.get(i);
			 array[i] = a;
		}
    return array;
	}
}
