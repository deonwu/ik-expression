/**
 * 
 */
package org.wltea.expression;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Variable;
import org.wltea.expression.datameta.VariableContainer;

/**
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 25, 2008
 */
public class ExpressionEvaluator {
	/**
	 * 根据流程上下文，执行公式语言
	 * @param expression
	 * @param variables
	 * @return
	 */
	public static Object evaluate(String expression, Collection<Variable> variables) {
		if (expression == null) {
			return null;
		}
		
		ExpressionExecutor ee = new ExpressionExecutor();
		try{
			//获取上下文的变量，设置到脚本执行器中
			if(variables != null && variables.size() > 0){						
				for(Variable var : variables ){
					//添加变来到脚本变量容器
					VariableContainer.addVariable(var);
				}
			}
			//解析表达式词元
			List<ExpressionToken> expTokens = ee.analyze(expression);
			//转化RPN，并验证
			expTokens = ee.convertToRPN(expTokens);
			//执行RPN
			ExpressionToken resultToken = ee.executeRPN(expTokens);			
			//转化result 到Object
			Constant constant = resultToken.getConstant();
			return constant.toJavaObject();

		} catch (IllegalExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
		}finally{
			//释放脚本变量容器
			VariableContainer.removeVariableMap();
		}
	}
	
	/**
	 * 将原始的表达式编译为RPN字符窜
	 * 如果表达式中有变量，调用此方法前要为之设置变量类型
	 * @param expression
	 * @return
	 */
	public static String compileExpression(String expression) throws IllegalExpressionException {
		if (expression == null || expression.trim().equals("")) {
			return null;
		}
		ExpressionExecutor ee = new ExpressionExecutor();
		List<ExpressionToken> list = ee.analyze(expression);
		list = ee.convertToRPN(list);
		return ee.tokensToString(list);
	}
	
	public static void main(String[] args) {
		String example = "$DAYEQUALS($CALCDATE([2008-11-01 00:00:00],0,0,-2,0,0,0) , [2008-10-31 23:00:00])";
		Object result = ExpressionEvaluator.evaluate(example, null);
		System.out.println(result);
	}
}
