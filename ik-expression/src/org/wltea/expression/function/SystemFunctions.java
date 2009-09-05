/**
 * 
 */
package org.wltea.expression.function;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 系统默认函数
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Feb 3, 2009
 */
public class SystemFunctions {

	//	//字符串包含比较
	//	CONTAINS
	//	//字符串前缀比较
	//	STARTSWITH
	//	//字符串后缀比较
	//	ENDSWITH
	//  日期计算函数
	//  CALCDATE
	//  当前日期函数
	//  SYSDATE
	//  日期相等比较
	//  DAYEQUALS
	
	/**
	 * 字符串包含比较
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean contains(String str1 , String str2){
		if(str1 == null || str2 == null){
			throw new NullPointerException("函数\"CONTAINS\"参数为空");
		}
		return str1.indexOf(str2) >= 0; 
	}
	
	/**
	 * 字符串前缀比较
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean startsWith(String str1 , String str2){
		if(str1 == null || str2 == null){
			throw new NullPointerException("函数\"STARTSWITH\"参数为空");
		}
		return str1.startsWith(str2);
	}
	
	/**
	 * 字符串后缀比较
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean endsWith(String str1 , String str2){
		if(str1 == null || str2 == null){
			throw new NullPointerException("函数\"ENDSWITH\"参数为空");
		}
		return str1.endsWith(str2);
	}
	
	/**
	 * 日期计算
	 * @param date 原始的日期
	 * @param years 年份偏移量
	 * @param months 月偏移量
	 * @param days 日偏移量
	 * @param hours 小时偏移量
	 * @param minutes 分偏移量
	 * @param seconds 秒偏移量
	 * @return 偏移后的日期
	 */
	public Date calcDate(Date date ,int years , int months , int days , int hours , int minutes , int seconds){
		if(date == null){
			throw new NullPointerException("函数\"CALCDATE\"参数为空");
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR , years);
		calendar.add(Calendar.MONTH , months);
		calendar.add(Calendar.DAY_OF_MONTH , days);
		calendar.add(Calendar.HOUR , hours);
		calendar.add(Calendar.MINUTE , minutes);
		calendar.add(Calendar.SECOND , seconds);		
		return calendar.getTime();
	}
	
	/**
	 * 获取系统当前时间
	 * @return
	 */
	public Date sysDate(){
		return new Date();
	}	
	
	/**
	 * 日期相等比较，精确到天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public boolean dayEquals(Date date1  , Date date2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayOfDate1 = sdf.format(date1);
		String dayOfDate2 = sdf.format(date2);
		return dayOfDate1.equals(dayOfDate2);
	}

}
