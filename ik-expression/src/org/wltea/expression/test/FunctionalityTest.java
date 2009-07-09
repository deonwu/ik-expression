/**
 * 
 */
package org.wltea.expression.test;

import java.util.ArrayList;
import java.util.List;

import org.wltea.expression.ExpressionExecutor;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.IllegalExpressionException;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 功能测试
 * @author 林良益
 *
 */
public class FunctionalityTest extends TestCase {
	
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
		expressions.add("100>10 ? 11:1.0 ");
		expressions.add("100>10 ? 11111L:3.3f ");
		//APPEND
		expressions.add("\"a String \" # true # 111 # [2009-10-10 10:10:10] # null");
		
		for(String expression : expressions){
			try {
				System.out.println("expression : " + expression);
				List<ExpressionToken> list = ee.analyze(expression);			
				list = ee.compile(list);		
				
				String s1 = ee.tokensToString(list);
//				System.out.println("s1 -- " + s1);
				List<ExpressionToken> tokens = ee.stringToTokens(s1);
				String s2 = ee.tokensToString(tokens);
//				System.out.println("s2 -- " + s2);
				Assert.assertEquals(s1, s2);
				System.out.println("result = " + ee.execute(tokens).toJavaObject());
				System.out.println();
				
			} catch (IllegalExpressionException e) {
				e.printStackTrace();
			} 
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("----------------testOperators over------------------");
		System.out.println("----------------------------------------------------");
	}
	
	/**
	 * 测试内部函数
	 * @throws Exception
	 */
	public void testInnerFunctions() throws Exception {
		System.out.println("testInnerFunctions");
		ExpressionExecutor ee = new ExpressionExecutor();
		ArrayList<String> expressions = new ArrayList<String>();
		//$CONTAINS
		expressions.add("$CONTAINS(\"aabbcc\",\"abc\")");
		expressions.add("$CONTAINS(\"aabcbcc\",\"abc\")");
		//$STARTSWITH
		expressions.add("$STARTSWITH(\"aabcbcc\",\"abc\")");
		expressions.add("$STARTSWITH(\"abccbcc\",\"abc\")");
		//$ENDSWITH
		expressions.add("$ENDSWITH(\"aabcbcc\",\"abc\")");
		expressions.add("$ENDSWITH(\"abccbcc\",\"bcc\")");
		//$CALCDATE
		expressions.add("$CALCDATE([2008-01-01],1,1,1,1,1,1)");
		expressions.add("$CALCDATE([2008-01-01],0,0,0,0,0,0)");
		expressions.add("$CALCDATE([2008-01-01 1:2:8],-1,-1,-1,-1,-1,-1)");
		expressions.add("$CALCDATE([2008-01-01],0,0,0,0,0,60)");
		expressions.add("$CALCDATE([2008-01-01],0,0,0,0,60,0)");
		expressions.add("$CALCDATE([2008-01-01],0,0,0,24,0,0)");
		expressions.add("$CALCDATE([2008-01-01],0,0,31,0,0,0)");
		expressions.add("$CALCDATE([2008-01-01],0,12,0,0,0,0)");
		//闰年测试
		expressions.add("$CALCDATE([2008-03-01],0,0,-1,0,0,0)");
		expressions.add("$CALCDATE([2008-02-28 23:00:00],0,0,0,1,0,0)");
		expressions.add("$CALCDATE([2008-02-29 23:00:00],1,0,0,0,0,0)");
		expressions.add("$CALCDATE($SYSDATE(),-1,12,0,0,0,0)");
		//$SYSDATE
		expressions.add("$SYSDATE()");
		//$DAYEQUALS
		expressions.add("$DAYEQUALS([2008-01-01],[2008-01-01])");
		expressions.add("$DAYEQUALS([2008-01-02],[2008-01-01])");
		expressions.add("$DAYEQUALS($SYSDATE(),[2009-2-10])");
		
		for(String expression : expressions){
			try {
				System.out.println("expression : " + expression);
				List<ExpressionToken> list = ee.analyze(expression);			
				list = ee.compile(list);		
				
				String s1 = ee.tokensToString(list);
				//System.out.println("s1 -- " + s1);
				List<ExpressionToken> tokens = ee.stringToTokens(s1);
				String s2 = ee.tokensToString(tokens);
				//System.out.println("s2 -- " + s2);
				Assert.assertEquals(s1, s2);
				System.out.println("result = " + ee.execute(tokens).toJavaObject());
				System.out.println();
				
			} catch (IllegalExpressionException e) {
				e.printStackTrace();
			}
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("--------------testInnerFunctions over---------------");
		System.out.println("----------------------------------------------------");		
	}
	
	
	public static void main(String[] args){
		System.out.println(100>10 ? 11111L:3.3f );
	}
}
