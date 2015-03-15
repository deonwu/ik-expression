package org.wltea.expression.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.PreparedExpression;
import org.wltea.expression.datameta.Variable;
import org.wltea.expression.datameta.BaseDataMeta.DataType;

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
//		if(args.length == 0){
//			args = new String[1];
//			args[0] = "IK Expression V2.0.5";
//		}
//		//定义表达式
//		String expression = "\"Hello \" + 版本";
//		//给表达式中的变量 [版本] 付上下文的值
//		List<Variable> variables = new ArrayList<Variable>();
//		variables.add(Variable.createVariable("版本", args[0]));
//		
//		//预编译表达式
//		PreparedExpression pe  = ExpressionEvaluator.preparedCompile(expression, variables);
//		//执行表达式
//		Object result = pe.execute();
//		System.out.println("Result = " + result);
//		
//		//更改参数，再次执行预编译式
//		pe.setArgument("版本", "IK Expression V2.0.6");
//		result = pe.execute();
//		System.out.println("Result = " + result);
		
		
		
		//定义表达式
		
		String expression = "$问好(数字类型)";
		//给表达式中的变量 [用户名] 付上下文的值
		List<Variable> variables = new ArrayList<Variable>();
		variables.add(new Variable("数字类型" , DataType.DATATYPE_DOUBLE , new Integer(0)));
		//执行表达式
		PreparedExpression pe  = ExpressionEvaluator.preparedCompile(expression, variables);
		System.out.println("Result = " + pe.execute());
		
		pe.setArgument("数字类型", new Float(100));
		System.out.println("Result = " + pe.execute());		
		
		pe.setArgument("数字类型", new Double(100));
		System.out.println("Result = " + pe.execute());
		
		Object result = null;  
		result = ExpressionEvaluator.evaluate("$问好(1.0)");     
		System.out.println("Result = " + result);     
		result = ExpressionEvaluator.evaluate("$问好(1)");     
		System.out.println("Result = " + result);  
		
		//*************************
		Map<String , Object> vars = new HashMap<String , Object>();
		System.out.println("-----------IK Expression");
		variables = new ArrayList<Variable>();
		Object[] keys = vars.keySet().toArray();
		for (int i=0;keys!=null && i<keys.length;i++){
			Object key =keys[i];
			variables.add(Variable.createVariable(key.toString(), vars.get(key)));
		}

		result = ExpressionEvaluator.evaluate("mobile!=null",variables);
		
		Boolean b = (Boolean)result;
		System.out.println(b.booleanValue());		
		
	}	
}
