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
 * @author Administrator
 *
 */
public class CustomFunctionTest  extends TestCase {
	public static final ThreadLocal<String> localString = new ThreadLocal<String>();
	
	public void testCustomFunction(){
		localString.set("test String");

		ExpressionExecutor ee = new ExpressionExecutor();
		ArrayList<String> expressions = new ArrayList<String>();

		expressions.add("$自定义函数(\"hello \")");
		
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
				System.out.println("result = " + ee.execute(tokens).getDataValueText());
				System.out.println();
				
			} catch (IllegalExpressionException e) {
				e.printStackTrace();
			}
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("--------------testCustomFunctions over---------------");
		System.out.println("----------------------------------------------------");		
		
	}

}
