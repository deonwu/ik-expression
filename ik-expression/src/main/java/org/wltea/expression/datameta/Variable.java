/**
 * 
 */
package org.wltea.expression.datameta;

import java.util.Date;
import java.util.List;

/**
 * 表达式上下文变量
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-23
 */
public class Variable extends BaseDataMeta{

	//变量名
	String variableName;
	
	/**
	 * 根据别名和参数值，构造 Variable 实例
	 * @param variableName
	 * @param variableValue
	 * @return Variable
	 */
	public static Variable createVariable(String variableName , Object variableValue){

		if(variableValue instanceof Boolean){
			return new Variable(variableName , DataType.DATATYPE_BOOLEAN , variableValue);

		}else if(variableValue instanceof Date){
			return new Variable(variableName , DataType.DATATYPE_DATE , variableValue);

		}else if(variableValue instanceof Double){
			return new Variable(variableName , DataType.DATATYPE_DOUBLE , variableValue);
						
		}else if(variableValue instanceof Float){
			return new Variable(variableName , DataType.DATATYPE_FLOAT , variableValue);
						
		}else if(variableValue instanceof Integer){
			return new Variable(variableName , DataType.DATATYPE_INT , variableValue);
						
		}else if(variableValue instanceof Long){
			return new Variable(variableName , DataType.DATATYPE_LONG , variableValue);
						
		}else if(variableValue instanceof String){
			return new Variable(variableName , DataType.DATATYPE_STRING , variableValue);
						
		}else if(variableValue instanceof List){
			return new Variable(variableName , DataType.DATATYPE_LIST , variableValue);
			
		}else if(variableValue instanceof Object){
			return new Variable(variableName , DataType.DATATYPE_OBJECT , variableValue);
			
		}else if(variableValue == null){
			return new Variable(variableName , DataType.DATATYPE_NULL , variableValue);
			
		}else {
			throw new IllegalArgumentException("非法参数：无法识别的变量类型");
		}

	}	

	public Variable(String variableName){
		this(variableName , null , null);	
	}
	
	public Variable(String variableName , DataType variableDataType , Object variableValue){
		super(variableDataType , variableValue);

		if(variableName == null){
			throw new IllegalArgumentException("非法参数：变量名为空");
		}
		
		this.variableName = variableName ;
	}

	public String getVariableName() {
		return variableName;
	}


	public void setVariableValue(Object variableValue) {
		this.dataValue = variableValue;
		//参数类型校验
		verifyDataMeta();
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
