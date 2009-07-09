/**
 * 
 */
package org.wltea.expression.op.define;

import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta;
import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Reference;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.op.IOperatorExecution;
import org.wltea.expression.op.Operator;

/**
 * 逻辑与操作
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 27, 2008
 */
public class Op_AND implements IOperatorExecution {

	public static final Operator THIS_OPERATOR = Operator.AND;
	
	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#execute(org.wltea.expression.ExpressionToken[])
	 */
	public Constant execute(Constant[] args) throws IllegalExpressionException {
		//参数校验
		if(args == null || args.length != 2){
			throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "操作缺少参数");
		}
		Constant first = args[1];
		if(null == first || null == first.getDataValue()){
			//抛NULL异常
			throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
		}
		Constant second = args[0];
		if(null == second || null == second.getDataValue()){
			//抛NULL异常
			throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
		}
		//运算：
		//如果第一参数为引用，则执行引用
		if(first.isReference()){
			Reference firstRef = (Reference)first.getDataValue();
			first = firstRef.execute();
		}
		if(DataType.DATATYPE_BOOLEAN ==  first.getDataType()){
			//对AND操作的优化处理，first为false，则忽略计算第二参数
			if(first.getBooleanValue()){
				//如果第二参数为引用，则执行引用
				if(second.isReference()){
					Reference secondRef = (Reference)second.getDataValue();
					second = secondRef.execute();
				}
				if(DataType.DATATYPE_BOOLEAN == second.getDataType()){
					return second;
				}else{
					//抛异常
					throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "\"第二参数类型错误");
				}
				
			}else{
				return first;
			}
		}else {
			//抛异常
			throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "\"第一参数类型错误");

		}
	}

	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#verify(int, org.wltea.expression.ExpressionToken[])
	 */
	public Constant verify(int opPositin, BaseDataMeta[] args)
			throws IllegalExpressionException {

		if(args == null){
			throw new IllegalArgumentException("运算操作符参数为空");
		}
		if(args.length != 2){
			//抛异常
			throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数丢失"
						, THIS_OPERATOR.getToken()
						, opPositin
					);
		}
		
		BaseDataMeta first = args[1];
		BaseDataMeta second = args[0];
		if(first == null || second == null){
			throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
		}
		
		if(DataType.DATATYPE_BOOLEAN ==  first.getDataType()
				&& DataType.DATATYPE_BOOLEAN == second.getDataType()){
			
			return new Constant(DataType.DATATYPE_BOOLEAN , Boolean.FALSE);
			
		}else {
			//抛异常
			throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数类型错误"
					, THIS_OPERATOR.getToken()
					, opPositin
					);

		}		

	}

}
