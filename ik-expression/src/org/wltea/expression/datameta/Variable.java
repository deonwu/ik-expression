/**
 * 
 */
package org.wltea.expression.datameta;

/**
 * 表达式上下文变量
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-23
 */
public class Variable extends BaseDataMeta{

	//变量名
	String variableName;

	//变量显示名
	String variableDisplayName;
	

	public Variable(String variableName){
		this(variableName , null , null , variableName);	
	}
	
	public Variable(String variableName , DataType variableDataType , Object variableValue){
		this(variableName , variableDataType , variableValue , variableName);		
	}
	
	public Variable(String variableName , DataType variableDataType , Object variableValue  , String variableDisplayName){
		super(variableDataType , variableValue);

		if(variableName == null){
			throw new IllegalArgumentException("非法参数：变量名为空");
		}
		if(DataType.DATATYPE_NULL == variableDataType){
			throw new IllegalArgumentException("非法参数：变量类型不能为DATATYPE_NULL,该类型不对外开放");
		}
		if(DataType.DATATYPE_REFERENCE == variableDataType){
			throw new IllegalArgumentException("非法参数：变量类型不能为DATATYPE_REFERENCE,该类型不对外开放");
		}
		
		this.variableName = variableName ;
		this.variableDisplayName = variableDisplayName;
	}

	public String getVariableName() {
		return variableName;
	}


	public void setVariableValue(Object variableValue) {
		this.dataValue = variableValue;
		//参数类型校验
		verifyDataMeta();
	}
	
	public String getVariableDisplayName() {
		return variableDisplayName;
	}

	public void setVariableDisplayName(String variableDisplayName) {
		this.variableDisplayName = variableDisplayName;
	}
	
	public void setDataType(DataType dataType){
		this.dataType = dataType;
		//参数类型校验
		verifyDataMeta();
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
			
		}else if(o instanceof Variable 
				&& super.equals(o)){
			
			Variable var = (Variable)o;
			if(variableName != null 
					&& variableName.equals(var.variableName)){
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}
	}

}
