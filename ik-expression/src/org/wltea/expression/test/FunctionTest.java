/**
 * 
 */
package org.wltea.expression.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.wltea.expression.ExpressionExecutor;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.IllegalExpressionException;

import junit.framework.TestCase;

/**
 * 功能测试
 * @author 林良益
 *
 */
public class FunctionTest extends TestCase {
	/**
	 * 测试ExpressionEvaluator
	 * @throws Exception
	 */
	public void testEvaluator() throws Exception {
		System.out.println("testEvaluator");
	}
	
	/**
	 * 简单测试表达式中的各个操作符
	 * @throws Exception
	 */
	public void testOperators()  throws Exception {
		ExpressionExecutor ee = new ExpressionExecutor();
		ArrayList<String> expressions = new ArrayList<String>();
		//算术符
		expressions.add("-1+2-3*4/5%6");
		//LE
		expressions.add("\"12345\" <= \"223\"");
		expressions.add("12345 <= 223");
		expressions.add("[2007-01-01] <= [2008-01-01]");
		//GE
		expressions.add("\"12345\" >= \"223\"");
		expressions.add("12345 >= 223");
		expressions.add("[2007-01-01] >= [2008-01-01]");
		//LT
		expressions.add("\"12345\" < \"223\"");
		expressions.add("12345 < 223");
		expressions.add("[2007-01-01] < [2008-01-01]");
		//GT
		expressions.add("\"12345\" > \"223\"");
		expressions.add("12345 > 223");
		expressions.add("[2007-01-01] > [2008-01-01]");
		//EQ
		expressions.add("\"12345\" == \"12345\"");
		expressions.add("223 == 223.0");
		expressions.add("[2009-01-01] == [2009-01-01]");
		expressions.add("[2009-01-01] == [2009-01-01 00:00:00]");
		expressions.add("true == false");
		expressions.add("null == null");
		expressions.add("null == \"a string\"");
		//NEQ
		expressions.add("\"12345\" != \"12345\"");
		expressions.add("223 != 223.0");
		expressions.add("[2009-01-01] != [2009-01-01]");
		expressions.add("[2009-01-01] != [2009-01-01 00:10:00]");
		expressions.add("true != false");
		expressions.add("null != null");
		expressions.add("null != \"a string\"");		
		//AND
		expressions.add("true && false");
		expressions.add("true && true");
		expressions.add("false && true");
		//OR
		expressions.add("true || false");
		expressions.add("false || false");
		expressions.add("false || true");
		//NOT
		expressions.add("!true");
		expressions.add("!false");
		//String +
		expressions.add("[2009-08-08] + false + 123 + \"a String\" + null + 1234 + 12345.88 + true");
		//SELECT
		expressions.add("false ? true ? \"路径1\" : \"路经2\" : true ? \"路径3\" : \"路径4\" ");
		//APPEND
		expressions.add("\"a String \" # true # 111 # [2009-10-10 10:10:10] # null");
		
		for(String expression : expressions){
			try {
				System.out.println("expression : " + expression);
				List<ExpressionToken> list = ee.analyze(expression);			
				list = ee.convertToRPN(list);		
				
				String s1 = ee.tokensToString(list);
				System.out.println("s1 -- " + s1);
				List<ExpressionToken> tokens = ee.stringToTokens(s1);
				String s2 = ee.tokensToString(tokens);
				System.out.println("s2 -- " + s2);
				System.out.println("s1 == s2 ? " + s1.equals(s2));	
				System.out.println("result = " + ee.executeRPN(tokens).toJavaObject());
				System.out.println();
				
			} catch (IllegalExpressionException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 测试内部函数
	 * @throws Exception
	 */
	public void testInnerFunctions() throws Exception {
		System.out.println("testInnerFunctions");
		
	}
	
	
	public static void main(String[] args){
		//long + float -->float 科学计数法
		float f = 10.0f + 10000000000l;
		System.out.println(f);
	}
}
