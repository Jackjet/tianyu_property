package com.vguang.utils;

/**
 * 统计工具
 * @author mi
 *
 */
public class StatisticsUtil {

	/**
	 * 取余
	 * @param int1 
	 * @param int2
	 * @return
	 */
	public static int getRemainder(int int1, int int2) {
		int int3 = int1%int2;
		return int3;
	}
	/**
	 * 取商
	 * @param int1 
	 * @param int2
	 * @return
	 */
	public static int getQuotient(int int1, int int2) {
		int int3 = int1/int2;
		return int3;
	}
	
	/**
	 * 计算周期总数
	 * @param ints 规律数组
	 * @param Remainder 余数
	 * @param Quotient  商数
	 * @param Initial   初始数
	 * @return
	 */
	public static Integer TotalCycle(int[]ints,int Remainder,int Quotient,int Initial ) {
		 int LawTotal = 0;//周期总数
		 for (int i = 0; i < ints.length; i++) {
			  int a = ints[i];
			  LawTotal= LawTotal+a;
		}
		//获取余数的周期数值
		 int RemainderLawTotal = 0;//余数周期总数
		 for (int i = 0; i < Remainder; i++) {
			  int a = ints[i];
			  RemainderLawTotal= RemainderLawTotal+a;
		}
		 if(LawTotal == 0) {
			 return null;
		 }else if (Quotient == 0) {
			 int nowOfficeOrgs = LawTotal+RemainderLawTotal+Initial;
			 return nowOfficeOrgs;
		 }else {
			 int nowOfficeOrgs = LawTotal*Quotient+RemainderLawTotal+Initial;
			 return nowOfficeOrgs;
		 }
		
	}
}
