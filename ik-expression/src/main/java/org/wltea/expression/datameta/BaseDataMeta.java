/**
 * 
 */
package org.wltea.expression.datameta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 基础数据描述对象
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-23
 */
public abstract class BaseDataMeta {
	
	//数据类型
	public enum DataType {

		//NULL类型
		DATATYPE_NULL ,
		//字符窜
		DATATYPE_STRING ,
		//布尔类
		DATATYPE_BOOLEAN ,
		//整数
		DATATYPE_INT ,
		//长整数
		DATATYPE_LONG ,
		//浮点数
		DATATYPE_FLOAT ,
		//双精度浮点
		DATATYPE_DOUBLE ,
		//日期时间
		DATATYPE_DATE ,
		//集合对象
		DATATYPE_LIST,
		//通用对象类型
		DATATYPE_OBJECT,
		;

	}
	
	//数据类型
	DataType dataType;
	//值
	Object dataValue;
	//引用类型标识
	private boolean isReference;
	
	public BaseDataMeta(DataType dataType , Object dataValue){
		this.dataType = dataType;
		this.dataValue = dataValue; 
		//参数类型校验
		verifyDataMeta();		
		
	}

	public DataType getDataType() {
		if(isReference){
			return this.getReference().getDataType();
		}else{
			return dataType;
		}
	}	
	
	public Object getDataValue() {
		return dataValue;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String getDataValueText() {
		if(dataValue == null){
			return null;
			
		}else if(DataType.DATATYPE_DATE ==  this.dataType){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)dataValue);
			
		}else if(BaseDataMeta.DataType.DATATYPE_LIST == this.dataType){
			StringBuffer buff = new StringBuffer("[");			
			List col = (List)dataValue;
			for(Object o : col){
				if (o == null) {
					buff.append("null, ");
				}else if(o instanceof Date){
					buff.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)o)).append(", ");
				}else{
					buff.append(o.toString()).append(", ");
				}
			}
			buff.append("]");
			if(buff.length() > 2){
				buff.delete(buff.length()-3, buff.length()-1);
			}
			return buff.toString();

		}else{
			return dataValue.toString();
		}
	}
	
	/**
	 * 获取Token的字符窜类型值
	 * @return
	 */
	public String getStringValue(){
		return getDataValueText();
	}
	/**
	 * 获取Token的boolean类型值
	 * @return
	 */
	public Boolean getBooleanValue(){
		if(DataType.DATATYPE_BOOLEAN != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}		
		return (Boolean)dataValue;
	}
	
	/**
	 * 获取Token的int类型值
	 * @return
	 */
	public Integer getIntegerValue(){
		
		if(DataType.DATATYPE_INT != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		return (Integer)dataValue;
	}
	
	/**
	 * 获取Token的long类型值
	 * @return
	 */
	public Long getLongValue(){
		
		if(DataType.DATATYPE_INT != this.dataType 
				&& DataType.DATATYPE_LONG != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		if(dataValue == null){
			return null;
		}
		return Long.valueOf(dataValue.toString());
	}
	
	/**
	 * 获取Token的float类型值
	 * @return
	 */
	public Float getFloatValue(){
		
		if(DataType.DATATYPE_INT != this.dataType 
				&& DataType.DATATYPE_FLOAT != this.dataType
				&& DataType.DATATYPE_LONG != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		if(dataValue == null){
			return null;
		}
		return Float.valueOf(dataValue.toString());
	}
	
	/**
	 * 获取Token的double类型值
	 * @return
	 */
	public Double getDoubleValue(){		
		if(DataType.DATATYPE_INT != this.dataType 
				&& DataType.DATATYPE_LONG != this.dataType
				&& DataType.DATATYPE_FLOAT != this.dataType
				&& DataType.DATATYPE_DOUBLE != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		if(dataValue == null){
			return null;
		}
		return Double.valueOf(dataValue.toString());
	}
	
	/**
	 * 获取Token的Date类型值
	 * @return
	 * @throws ParseException 
	 */
	public Date getDateValue(){
		if(DataType.DATATYPE_DATE != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		return (Date)dataValue;
	}
	
	
	/**
	 * 获取数据的集合对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getCollection() {
		if(DataType.DATATYPE_LIST != this.dataType){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		return (List<Object>)dataValue;
	}
	
	/**
	 * 获取Token的引用对象
	 * @return
	 */
	public Reference getReference() {
		if(!this.isReference){
			throw new UnsupportedOperationException("当前常量类型不支持此操作");
		}
		return (Reference)dataValue;
	}
			
	
	@Override
	public boolean equals(Object o){
		
		if( o == this ){
			return true;
			
		}else if(o instanceof BaseDataMeta){
			
			BaseDataMeta bdo = (BaseDataMeta)o;
			if(this.isReference() && bdo.isReference){
				return this.getReference() == bdo.getReference();
			}
			
			if(bdo.dataType == dataType){
				if(bdo.dataValue != null 
						&& bdo.dataValue.equals(dataValue)){
					return true;
				}else if(bdo.dataValue == null 
						&& dataValue == null){
					return true;
				}else{
					return false;
				}				
			}else{
				return false;
			}
			
		}else{
			return false;
		}
	}
	
	/**
	 * 校验数据类型和值得合法性
	 */
	protected void verifyDataMeta(){
		if(dataType != null && dataValue !=null){
			if(DataType.DATATYPE_NULL == dataType && dataValue != null){
				throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值不为空");
				
			}else if(DataType.DATATYPE_BOOLEAN == dataType){
				try{
					getBooleanValue();
				}catch(UnsupportedOperationException e){
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}				

			}else if(DataType.DATATYPE_DATE == dataType){
				try {
					getDateValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}
				
			}else if(DataType.DATATYPE_DOUBLE == dataType){
				try {
					getDoubleValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}

			}else if(DataType.DATATYPE_FLOAT == dataType){
				try {
					getFloatValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}
				
			}else if(DataType.DATATYPE_INT == dataType){
				try {
					getIntegerValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}
				
			}else if(DataType.DATATYPE_LONG == dataType){
				try {
					getLongValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}

			}else if(DataType.DATATYPE_STRING == dataType){
				try {
					getStringValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}
				
			}else if(DataType.DATATYPE_LIST == dataType){
				try {
					getCollection();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}
				
			}else if(this.isReference){
				try {
					getReference();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}

			}else if(DataType.DATATYPE_OBJECT == dataType){
				try {
					getDataValue();
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException("数据类型不匹配; 类型：" + dataType + ",值:" + dataValue);
				}

			}
		}
	}
	
	

	public Class<?> mapTypeToJavaClass(){
		
		if(BaseDataMeta.DataType.DATATYPE_BOOLEAN == this.getDataType()){
			return boolean.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_DATE == this.getDataType()){
			return Date.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_DOUBLE == this.getDataType()){
			return double.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_FLOAT == this.getDataType()){
			return float.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_INT == this.getDataType()){
			return int.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_LONG == this.getDataType()){
			return long.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_STRING == this.getDataType()){
			return String.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_LIST == this.getDataType()){
			return List.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_OBJECT == this.getDataType()){
			return Object.class;
			
		}else if(BaseDataMeta.DataType.DATATYPE_NULL == this.getDataType()){
			return null;
			
		}
		throw new RuntimeException("映射Java类型失败：无法识别的数据类型");
	}
	
	/**
	 * 检查数据类型的兼容性
	 * 类型相同，一定兼容
	 * 类型不同，则可兼容的数据类型包括int ，long ，float ， double
	 * null 类型与所有类型兼容
	 * @param another
	 * @return
	 */
	private boolean isCompatibleType(BaseDataMeta another){
		
		if(DataType.DATATYPE_NULL == this.getDataType() 
				|| DataType.DATATYPE_NULL == another.getDataType()){
			return true;
			
		}else if(this.getDataType() == another.getDataType()){
			return true;
			
		}else if(DataType.DATATYPE_INT != this.getDataType() 
				&& DataType.DATATYPE_LONG != this.getDataType()
				&& DataType.DATATYPE_FLOAT != this.getDataType()
				&& DataType.DATATYPE_DOUBLE != this.getDataType()){
			return false;
			
		}else if(DataType.DATATYPE_INT != another.getDataType() 
				&& DataType.DATATYPE_LONG != another.getDataType()
				&& DataType.DATATYPE_FLOAT != another.getDataType()
				&& DataType.DATATYPE_DOUBLE != another.getDataType()){
			return false;
			
		}else{
			return true;
		}
	}
	
	/**
	 * 获取两数的兼容类型
	 * 如果两个数据类型无法兼容，返回null
	 * @param another
	 * @return
	 */
	public DataType getCompatibleType(BaseDataMeta another){
		
		if(isCompatibleType(another)){
			if(DataType.DATATYPE_NULL == this.getDataType()){
				return another.getDataType();
				
			}else if(DataType.DATATYPE_NULL == another.getDataType()){
				return this.getDataType();
				
			}else if(this.getDataType() == another.getDataType()){
				return this.getDataType();
				
			}else if(DataType.DATATYPE_DOUBLE == this.getDataType() 
						|| DataType.DATATYPE_DOUBLE == another.getDataType()){
				return DataType.DATATYPE_DOUBLE;
				
			}else if(DataType.DATATYPE_FLOAT == this.getDataType() 
					|| DataType.DATATYPE_FLOAT == another.getDataType()){
				return DataType.DATATYPE_FLOAT;
				
			}else if(DataType.DATATYPE_LONG == this.getDataType() 
					|| DataType.DATATYPE_LONG == another.getDataType()){
				return DataType.DATATYPE_LONG;
				
			}else{
				return DataType.DATATYPE_INT;
			}
		}else{
			return null;
		}
	}
	
	/**
	 * @throws ParseException
	 * 
	 */
	public Object toJavaObject() throws ParseException{
		if(null == this.dataValue){
			return null;
		}
		
		if(BaseDataMeta.DataType.DATATYPE_BOOLEAN == this.getDataType()){
			return getBooleanValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_DATE == this.getDataType()){
			return getDateValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_DOUBLE == this.getDataType()){
			return getDoubleValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_FLOAT == this.getDataType()){
			return getFloatValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_INT == this.getDataType()){
			return getIntegerValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_LONG == this.getDataType()){
			return getLongValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_STRING == this.getDataType()){
			return getStringValue();
			
		}else if(BaseDataMeta.DataType.DATATYPE_LIST == this.getDataType()){			
			return getCollection();
			
		}else if(BaseDataMeta.DataType.DATATYPE_OBJECT == this.getDataType()){			
			return getDataValue();

		}else {
			throw new RuntimeException("映射Java类型失败：无法识别的数据类型");
		}	
	}

	public boolean isReference() {
		return isReference;
	}

	void setReference(boolean isReference) {
		this.isReference = isReference;
	}	
	
}
