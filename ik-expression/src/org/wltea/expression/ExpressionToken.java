/**
 * 
 */
package org.wltea.expression;

import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Reference;
import org.wltea.expression.datameta.Variable;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.op.Operator;

/**
 * 表达式解析词元对象
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-18
 */
public class ExpressionToken {
	

	//词元的语法类型
	public enum ETokenType{
		//常量
		ETOKEN_TYPE_CONSTANT ,
		//变量
		ETOKEN_TYPE_VARIABLE ,	
		//操作符
		ETOKEN_TYPE_OPERATOR ,
		//函数
		ETOKEN_TYPE_FUNCTION ,
		//分隔符
		ETOKEN_TYPE_SPLITOR ,
		;
	}
	
	//Token的词元类型：常量，变量，操作符，函数，分割符
	private ETokenType tokenType ;
	//当TokenType = ETOKEN_TYPE_CONSTANT 时,constant存储常量描述
	private Constant constant;
	//当TokenType = ETOKEN_TYPE_VARIABLE 时,variable存储变量描述
	private Variable variable ;
	//当TokenType = ETOKEN_TYPE_OPERATOR 时, operator存储操做符描述
	private Operator operator;
	//存储字符描述
	private String tokenText ;
	//词元在表达式中的起始位置
	private int startPosition = -1;
	
	public static ExpressionToken createConstantToken(DataType dataType , Object dataValue){
		ExpressionToken instance = new ExpressionToken();
		instance.constant = new Constant(dataType , dataValue);
		instance.tokenType = ETokenType.ETOKEN_TYPE_CONSTANT;
		if(dataValue != null){
			instance.tokenText=  instance.constant.getDataValueText();
		}
		return instance;
	}
	
	public static ExpressionToken createConstantToken(Constant constant){
		if(constant == null){
			throw new IllegalArgumentException("非法参数异常：常量为null" );
		}
		ExpressionToken instance = new ExpressionToken();
		instance.constant = constant;
		instance.tokenType = ETokenType.ETOKEN_TYPE_CONSTANT;
		if(constant.getDataValue() != null){
			instance.tokenText=  constant.getDataValueText();
		}
		return instance;
	}
	
	public static ExpressionToken createVariableToken(String variableName){
		ExpressionToken instance = new ExpressionToken();
		instance.variable = new Variable(variableName);
		instance.tokenType = ETokenType.ETOKEN_TYPE_VARIABLE;
		instance.tokenText = variableName;
		return instance;
	}
	
	public static ExpressionToken createReference(Reference ref){
		ExpressionToken instance = new ExpressionToken();
		instance.constant = new Constant(ref);
		instance.tokenType = ETokenType.ETOKEN_TYPE_CONSTANT;
		if(ref != null){
			instance.tokenText=  instance.constant.getDataValueText();
		}
		return instance;
	}	
	
	public static ExpressionToken createFunctionToken(String functionName){
		if(functionName == null){
			throw new IllegalArgumentException("非法参数：函数名称为空");
		}
		ExpressionToken instance = new ExpressionToken();
		instance.tokenText = functionName;
		instance.tokenType = ETokenType.ETOKEN_TYPE_FUNCTION;
		return instance;
	}
	
	public static ExpressionToken createOperatorToken(Operator operator){
		if(operator == null){
			throw new IllegalArgumentException("非法参数：操作符为空");
		}
		ExpressionToken instance = new ExpressionToken();
		instance.operator = operator;
		instance.tokenText = operator.getToken();
		instance.tokenType = ETokenType.ETOKEN_TYPE_OPERATOR;
		return instance;
	}
	
	public static ExpressionToken createSplitorToken(String splitorText){
		if(splitorText == null){
			throw new IllegalArgumentException("非法参数：分隔符为空");
		}
		ExpressionToken instance = new ExpressionToken();
		instance.tokenText = splitorText;
		instance.tokenType = ETokenType.ETOKEN_TYPE_SPLITOR;
		return instance;
	}
	
	/**
	 * 私有构造函数
	 * @param tokenText
	 * @param tokenType
	 * @param dataType
	 */
	private ExpressionToken(){
	}
	
	/**
	 * 获取Token的词元类型
	 * @return
	 */
	public ETokenType getTokenType() {
		return tokenType;
	}

	
	/**
	 * 获取Token的常量描述
	 * @return
	 */
	public Constant getConstant(){
		return this.constant;
	}

	/**
	 * 获取Token的变量描述
	 * @return
	 */
	public Variable getVariable(){
		return this.variable;
	}
	
	/**
	 * 获取Token的操作符类型值
	 * @return
	 */
	public Operator getOperator(){
		return this.operator;
	}
	
	/**
	 * 获取Token的方法名类型值
	 * @return
	 */
	public String getFunctionName(){
		//TODO 考虑后期直接return Method
		return this.tokenText;
	}
	
	/**
	 * 获取Token的分隔符类型值
	 * @return
	 */	
	public String getSplitor(){
		return this.tokenText;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	
	@Override
	public String toString(){
		return tokenText;
	}
}
