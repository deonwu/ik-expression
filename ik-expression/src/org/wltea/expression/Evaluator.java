package org.wltea.expression;

import org.wltea.expression.op.Operator;

/**
 * 对象求值运算器接口， 用于自定义对象之间的操作。
 * 
 * @author deonwu
 *
 */
public interface Evaluator<T> {

	/**
	 * 对象之间运算求值。
	 * 
	 * @param op -- 运算操作，
	 * @param first -- 目标对象
	 * @param second -- 参数对象。如果是单目运算符，参数对象为null。
	 * @return
	 */
	public T evalutor(Operator op, T first, T second) throws IllegalExpressionException;
	
	/**
	 * 检查是否支持运算符。
	 * @param op
	 * @param first
	 * @param second
	 * @return
	 */
	public boolean canOperator(Operator op, T first, T second) throws IllegalExpressionException;
}
