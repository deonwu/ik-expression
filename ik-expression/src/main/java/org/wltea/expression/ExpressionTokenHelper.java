package org.wltea.expression;

import org.wltea.expression.op.Operator;

/**
 * 表达式字符窜词元处理辅助类
 * 这个类，考虑到性能因素，对词元类型，只进行简单辨别，词元的完整性依赖于RPN已经经过合法校验
 * @author 林良益，卓诗垚
 * @version 2.0 
 */
public class ExpressionTokenHelper {
	
	
	public static boolean isNull(String s){
		return "null".equals(s);
	}
	
	public static boolean isBoolean(String s){
		return  "true".equals(s) || "false".equals(s) ;
	}
	
	/**
	 * 判断是否是整数
	 * @param s
	 * @return
	 */
	public static  boolean isInteger(String s){
		if(s != null && s.length() > 0){
			if(s.length() == 1){
				return isNumber(s.charAt(0)) && '.' != s.charAt(0);
			}else{
				return (isNumber(s.charAt(0)) && isNumber(s.charAt(s.length() - 1)) && s.indexOf('.') < 0);
			}
		}else {
			return false;
		}	
	}
	
	/**
	 * 判断是否是双精度浮点数
	 * @param s
	 * @return
	 */
	public static boolean isDouble(String s){
		if(s != null && s.length() > 1){
			return (isNumber(s.charAt(0)) && isNumber(s.charAt(s.length() - 1)) && s.indexOf('.') >= 0);
		}else {
			return false;
		}			
	}	
	
	/**
	 * 判断是否是长整数
	 * @param s
	 * @return
	 */
	public static boolean isLong(String s){
		if(s != null && s.length() > 1){
			return (isNumber(s.charAt(0)) && s.endsWith("L"));	
		}else {
			return false;
		}			
		
	}	
	
	/**
	 * 判断是否是浮点数
	 * @param s
	 * @return
	 */
	public static boolean isFloat(String s){
		if(s != null && s.length() > 1){
			return (isNumber(s.charAt(0)) && s.endsWith("F"));	
		}else {
			return false;
		}
		
	}	
	
	public static boolean isString(String s){
		if(s != null && s.length() > 1){
			return (s.charAt(0) == '"');
		}else {
			return false;
		}		
	
	}
	
	public static boolean isDateTime(String s){
		if(s != null && s.length() > 1){
			return (s.charAt(0) == '[');
		}else {
			return false;
		}
	}	
	
	/**
	 * 是否是分隔符词元
	 * @param s
	 * @return
	 */
	public static boolean isSplitor(String s){
		return ",".equals(s) || "(".equals(s) || ")".equals(s); 
	}
		
	/**
	 * 是否是函数词元
	 * @param s
	 * @return
	 */
	public static boolean isFunction(String s){
		if(s != null && s.length() > 1){
			return (s.charAt(0) == '$');
		}else {
			return false;
		}
	}
	
	/**
	 * 是否是操作符
	 * @param s
	 * @return
	 */
	public static boolean isOperator(String s){
		if(s != null){
			try{
				Operator.valueOf(s);
				return true;
			}catch(IllegalArgumentException e){
				return false;
			}
		}else {
			return false;
		}
	}
	
	private static boolean isNumber(char c){
		if((c >= '0' && c <= '9') || c == '.'){
			return true;
		}else{
			return false;
		}
	}

}
