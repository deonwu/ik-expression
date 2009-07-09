/**
 * 
 */
package org.wltea.expression.datameta;

import org.wltea.expression.ExpressionToken;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.ExpressionToken.ETokenType;
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
	
	public Reference(ExpressionToken token , Constant[] args) throws IllegalExpressionException{
		this.token = token;
		this.arguments = args;
		//记录Reference实际的数据类型
		if(ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == token.getTokenType()){
			Constant result = FunctionExecution.varify(token.getFunctionName(), token.getStartPosition() , args);
			dataType = result.getDataType();
		}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == token.getTokenType()){
			Operator op = token.getOperator();
			Constant result = op.verify(token.getStartPosition() , args);
			dataType = result.getDataType();
		}
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
	public Constant execute()throws IllegalExpressionException{
		
		if(ETokenType.ETOKEN_TYPE_OPERATOR == token.getTokenType()){
			//执行操作符
			Operator op = token.getOperator();
			return op.execute(arguments);
			
		}else if(ETokenType.ETOKEN_TYPE_FUNCTION == token.getTokenType()){
			//执行函数
			return	FunctionExecution.execute(token.getFunctionName(), token.getStartPosition() , arguments);
			
		}else{
			throw new IllegalExpressionException("不支持的Reference执行异常");
		}
	}
	
}
