package org.wltea.expression.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

import junit.framework.TestCase;

public class VariableTest extends TestCase {

	/**
	 * 简单测试带变量的表达式操作符
	 * @throws Exception
	 */
	public void testOperators()  throws Exception {
		System.out.println("testOperators");

		ArrayList<String> expressions = new ArrayList<String>();
		//算术符
		expressions.add("vInt + 2 - 3 * 4 / 5 % 6");
		//LE
		expressions.add("vString <= \"223\"");
		//GE
		expressions.add("vDate >= [2008-01-01]");
		//EQ
		expressions.add("223 == vDouble");
		expressions.add("223 == vNull");
		//NEQ
		expressions.add("vBoolean != false");
		expressions.add("vNull != null");
		expressions.add("vNull != \"a string\"");		
		//AND
		expressions.add("true && vBoolean");
		//OR
		expressions.add("vBoolean || false");
		//NOT
		expressions.add("!vBoolean");
		//String +
		expressions.add("vDate + vBoolean + vInt + vString + vNull  + vDouble + vBoolean");
		//SELECT
		expressions.add("false ? true ? vString_p1 : vString_p3 : vBoolean ? vString_p3 : vString_p4 ");
		//APPEND
		expressions.add("vString # vBoolean # vInt # vDate # vNull");
		
		//设置上下文变量
		ArrayList<Variable> variables = new ArrayList<Variable>();		
		variables.add(Variable.createVariable("vInt", new Integer(-1)));
		variables.add(Variable.createVariable("vString", "12345"));
		variables.add(Variable.createVariable("vDate", new Date()));
		variables.add(Variable.createVariable("vDouble", new Double(223.0)));
		variables.add(Variable.createVariable("vBoolean", new Boolean(true)));
		variables.add(Variable.createVariable("vNull", null));
		variables.add(Variable.createVariable("vString_p1", "路径1"));
		variables.add(Variable.createVariable("vString_p2", "路径2"));
		variables.add(Variable.createVariable("vString_p3", "路径3"));
		variables.add(Variable.createVariable("vString_p4", "路径4"));		
		
		
		for(String expression : expressions){
			System.out.println("expression : " + expression);
			Object result = ExpressionEvaluator.evaluate(expression, variables);
			System.out.println("result = " + result);
			System.out.println();
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("----------------testOperators over------------------");
		System.out.println("----------------------------------------------------");
		
	}
	
	/**
	 * 测试带变量的内部函数
	 * @throws Exception
	 */
	public void testInnerFunctions() throws Exception {
		System.out.println("testInnerFunctions");
		
		List<String> expressions = new ArrayList<String>();
		//$CONTAINS
		expressions.add("$CONTAINS(vString1 ,\"abc\")");
		expressions.add("$CONTAINS(vString2 ,\"abc\")");
		//$STARTSWITH
		expressions.add("$STARTSWITH(vString2 ,\"abc\")");
		expressions.add("$STARTSWITH(vString3 ,\"abc\")");
		//$ENDSWITH
		expressions.add("$ENDSWITH(vString2 ,\"abc\")");
		expressions.add("$ENDSWITH(vString3 ,\"bcc\")");
		//$CALCDATE
		expressions.add("$CALCDATE(vDate,1,1,1,1,1,1)");
		expressions.add("$CALCDATE(vDate,0,0,0,0,0,0)");
		expressions.add("$CALCDATE(vDate,-1,-1,-1,-1,-1,-1)");
		expressions.add("$CALCDATE(vDate,0,0,0,0,0,60)");
		expressions.add("$CALCDATE(vDate,0,0,0,0,60,0)");
		expressions.add("$CALCDATE(vDate,0,0,0,24,0,0)");
		expressions.add("$CALCDATE(vDate,0,0,31,0,0,0)");
		expressions.add("$CALCDATE(vDate,0,12,0,0,0,0)");
		//$DAYEQUALS
		expressions.add("$DAYEQUALS(vDate,[2008-01-01])");
		
		//设置上下文变量
		List<Variable> variables = new ArrayList<Variable>();		
		variables.add(Variable.createVariable("vString1", "aabbcc"));
		variables.add(Variable.createVariable("vString2", "aabcbcc"));
		variables.add(Variable.createVariable("vString3", "abccbcc"));
		variables.add(Variable.createVariable("vDate", new Date()));
		
		
		for(String expression : expressions){
			System.out.println("expression : " + expression);
			Object result = ExpressionEvaluator.evaluate(expression, variables);
			System.out.println("result = " + result);
			System.out.println();
		}
		System.out.println("----------------------------------------------------");		
		System.out.println("--------------testInnerFunctions over---------------");
		System.out.println("----------------------------------------------------");		
	}
	
	/**
	 * Hello World Example
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length == 0){
			args = new String[1];
			args[0] = "IK Expression";
		}
		//定义表达式
		String expression = "\"Hello World \" + 用户名";
		//给表达式中的变量userName付上下文的值
		List<Variable> variables = new ArrayList<Variable>();
		variables.add(Variable.createVariable("用户名", args[0]));
		//执行表达式
		Object result = ExpressionEvaluator.evaluate(expression, variables);
		System.out.println("Result = " + result);		
	}	
}
