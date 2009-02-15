package org.wltea.expression.test;

import java.util.ArrayList;
import java.util.Date;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

import junit.framework.TestCase;

public class VariableTest extends TestCase {

	public void testOperators()  throws Exception {

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
}
