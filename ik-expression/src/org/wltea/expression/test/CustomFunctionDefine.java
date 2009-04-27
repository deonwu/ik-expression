/**
 * 
 */
package org.wltea.expression.test;

/**
 * @author Administrator
 *
 */
public class CustomFunctionDefine {
	
	public void doCustomFunction(String param){
		String localString = CustomFunctionTest.localString.get();
		System.out.println( param + " | " + localString);		
	}
}
