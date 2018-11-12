/**
 * 
 */
package org.wltea.expression.datameta;

import org.wltea.expression.Evaluator;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.ExpressionToken.ETokenType;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.function.FunctionExecution;
import org.wltea.expression.op.Operator;



/**
 * 引用对象
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2009-02-07 
 */
public class Reference {
	
	private ExpressionToken token;
	
	private Constant[] arguments;
	//引用对象实际的数据类型
	private DataType dataType;

	private Evaluator<Constant> evaluator;

	public Reference(ExpressionToken token , Constant[] args, Evaluator<Constant> evaluator) throws IllegalExpressionException{
		this(token, args, true, evaluator);
	}
	
	public Reference(ExpressionToken token , Constant[] args, boolean isStrict, Evaluator<Constant> evaluator) throws IllegalExpressionException{
		this.token = token;
		this.arguments = args;
		//记录Reference实际的数据类型
		if(ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == token.getTokenType()){
			Constant result = FunctionExecution.varify(token.getFunctionName(), token.getStartPosition() , args);
			dataType = result.getDataType();
		}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == token.getTokenType()){
			if(isStrict){
				Operator op = token.getOperator();
				Constant result = op.verify(token.getStartPosition() , args);
				dataType = result.getDataType();
			}else {
				dataType = DataType.DATATYPE_OBJECT;
			}
		}

		this.evaluator = evaluator;
	}
	
	public DataType getDataType() {
		return dataType;
	}

	public Constant[] getArgs() {
		return arguments;
	}
	
	public void setArgs(Constant[] args) {
		this.arguments = args;
	}
	
	public ExpressionToken getToken() {
		return token;
	}

	public void setToken(ExpressionToken token) {
		this.token = token;
	} 

	/**
	 * 执行引用对象指待的表达式（操作符或者函数）
	 * @return
	 */
	public Constant execute() throws IllegalExpressionException{
		return execute(evaluator);
	}
	
	/**
	 * 执行引用对象指待的表达式（操作符或者函数）
	 * @return
	 */
	public Constant execute(Evaluator<Constant> evaluator)throws IllegalExpressionException{
		
		if(ETokenType.ETOKEN_TYPE_OPERATOR == token.getTokenType()){
			//执行操作符
			Operator op = token.getOperator();
			Constant first = arguments[0];
			Constant second = null;
			if(arguments.length > 1){
				first = arguments[1];
				second = arguments[0];
			}
			
			if(evaluator != null && evaluator.canOperator(op, first, second)){
				return evaluator.evalutor(op, first, second);
			}else {			
				return op.execute(arguments);
			}	
		}else if(ETokenType.ETOKEN_TYPE_FUNCTION == token.getTokenType()){
			//执行函数
			return	FunctionExecution.execute(token.getFunctionName(), token.getStartPosition() , arguments);
			
		}else{
			throw new IllegalExpressionException("不支持的Reference执行异常");
		}
	}
	
}
