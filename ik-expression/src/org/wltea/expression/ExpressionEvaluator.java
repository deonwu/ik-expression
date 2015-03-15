/**
 * 
 */
package org.wltea.expression;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Variable;

/**
 * IK-Expression 表达式解析器（主入口程序）
 * ExpressionEvaluator 是多线程安全的
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 25, 2008
 */
public class ExpressionEvaluator {
	
	/**
	 * 验证表达式
	 * @param expression
	 * @return
	 */
	public static String compile(String expression){
		return compile(expression , new ExpressionContext());
	}
	
	/**
	 * 验证表达式
	 * @param expression
	 * @param variables
	 * @return
	 * @throws IllegalExpressionException 
	 */	
	public static String compile(String expression, Collection<Variable> variables){
		ExpressionContext ctx = new ExpressionContext();
		//获取上下文的变量，设置到脚本执行器中
		if(variables != null && variables.size() > 0){						
			for(Variable var : variables ){
				//添加变来到脚本变量容器
				//VariableContainer.addVariable(var);
				ctx.put(var.getVariableName(), var);
			}
		}

		return compile(expression, ctx);
	}
	
	/**
	 * 验证表达式
	 * @param expression
	 * @param ctx -- 变量上下文
	 * @return
	 * @throws IllegalExpressionException 
	 */
	public static String compile(String expression, ExpressionContext ctx){
		if (expression == null) {
			throw new RuntimeException("表达式为空");
		}
		
		ExpressionExecutor ee = new ExpressionExecutor(ctx);
		try{
			//解析表达式词元
			List<ExpressionToken> expTokens = ee.analyze(expression);
			//转化RPN，并验证
			expTokens = ee.compile(expTokens);	
			//以字符串形式输出RPN
			return ee.tokensToString(expTokens);
		} catch (IllegalExpressionException e) {			
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 编译期检查异常");
		}
	}

	/**
	 * 获取预编译的表达式对象 
	 * @param expression 表达式的字符串表示
	 * @param variables 表达式的参数集合
	 * @return PreparedExpression 编译的表达式对象
	 * @throws IllegalExpressionException
	 */	
	public static PreparedExpression preparedCompile(String expression, Collection<Variable> variables){
		ExpressionContext ctx = new ExpressionContext();
		//获取上下文的变量，设置到脚本执行器中
		if(variables != null && variables.size() > 0){						
			for(Variable var : variables ){
				//添加变来到脚本变量容器
				//VariableContainer.addVariable(var);
				ctx.put(var.getVariableName(), var);
			}
		}

		return preparedCompile(expression, ctx);
	}
	
	/**
	 * 获取预编译的表达式对象 
	 * @param expression 表达式的字符串表示
	 * @param ctx 变量上下文
	 * @return PreparedExpression 编译的表达式对象
	 * @throws IllegalExpressionException
	 */
	public static PreparedExpression preparedCompile(String expression ,  ExpressionContext ctx){
		if (expression == null) {
			throw new RuntimeException("表达式为空");
		}
		
		ExpressionExecutor ee = new ExpressionExecutor(ctx);
		try{
			//解析表达式词元
			List<ExpressionToken> expTokens = ee.analyze(expression);
			//转化RPN，并验证
			expTokens = ee.compile(expTokens);
			//生成预编译表达式
			PreparedExpression pe = new PreparedExpression(expression , expTokens , ctx.getVariableMap());
			return pe;
		} catch (IllegalExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 预编译异常");
		}
	}

	
	/**
	 * 执行无变量表达式
	 * @param expression
	 * @return
	 */
	public static Object evaluate(String expression){
		return evaluate(expression , new ExpressionContext());
	}
	
	/**
	 * 根据流程上下文，执行公式语言
	 * @param expression
	 * @param variables
	 * @return
	 */
	public static Object evaluate(String expression, Collection<Variable> variables) {
		ExpressionContext ctx = new ExpressionContext();
		//获取上下文的变量，设置到脚本执行器中
		if(variables != null && variables.size() > 0){						
			for(Variable var : variables ){
				//添加变来到脚本变量容器
				//VariableContainer.addVariable(var);
				ctx.put(var.getVariableName(), var);
			}
		}

		return evaluate(expression, ctx);
	}

	/**
	 * 根据流程上下文，执行公式语言
	 * @param expression
	 * @param ctx
	 * @return
	 */
	public static Object evaluate(String expression, ExpressionContext ctx) {
		return evaluate(expression, ctx, null);
	}

	/**
	 * 根据流程上下文，执行公式语言
	 * @param expression
	 * @param ctx
	 * @return
	 */
	public static Object evaluate(String expression, ExpressionContext ctx, Evaluator evaluator) {
		if (expression == null) {
			return null;
		}
		
		if(evaluator != null){
			ctx.setEvaluator(evaluator);
		}
		
		ExpressionExecutor ee = new ExpressionExecutor(ctx);
		try{
			//解析表达式词元
			List<ExpressionToken> expTokens = ee.analyze(expression);
			//转化RPN，并验证
			expTokens = ee.compile(expTokens);
			//执行RPN
			Constant constant = ee.execute(expTokens);	
			return constant.toJavaObject();

		} catch (IllegalExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
		}finally{
			//释放脚本变量容器
			//VariableContainer.removeVariableMap();
		}
	}
	
	
}
