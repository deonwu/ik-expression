/**
 * 
 */
package org.wltea.expression.datameta;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
	
	public static Variable createVariable(String variableName, DataType variableDataType , String variableValueStr){
		if(BaseDataMeta.DataType.DATATYPE_BOOLEAN == variableDataType){
			return new Variable(variableName, variableDataType, new Boolean(variableValueStr));
		}else if(BaseDataMeta.DataType.DATATYPE_DATE == variableDataType){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return new Variable(variableName, variableDataType, sdf.parse(variableValueStr));
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}else if(BaseDataMeta.DataType.DATATYPE_DOUBLE == variableDataType){
			return new Variable(variableName, variableDataType, new Double(variableValueStr));
		}else if(BaseDataMeta.DataType.DATATYPE_FLOAT == variableDataType){
			return new Variable(variableName, variableDataType, new Float(variableValueStr));
		}else if(BaseDataMeta.DataType.DATATYPE_INT == variableDataType){
			return new Variable(variableName, variableDataType, new Integer(variableValueStr));
		}else if(BaseDataMeta.DataType.DATATYPE_LONG == variableDataType){
			return new Variable(variableName, variableDataType, new Long(variableValueStr));
		}else if(BaseDataMeta.DataType.DATATYPE_STRING == variableDataType){
			return new Variable(variableName, variableDataType, variableValueStr);
		} else {
			return null;
		}
	}

	public Variable(String variableName){
		this(variableName , null , null , variableName);	
	}
	
	public Variable(String variableName , DataType variableDataType){
		this(variableName , variableDataType , null , variableName);		
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
			throw new IllegalArgumentException("非法参数：变量类型不能为NULL");
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
