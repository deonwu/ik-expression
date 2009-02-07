/**
 * 
 */
package org.wltea.expression.function;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta;
import org.wltea.expression.datameta.Constant;


/**
 * 运算符及内嵌方法调用
 * @author 林良益
 * 2008-09-18
 * @version 2.0
 */
public class FunctionExecution {
	
	private FunctionExecution(){		
	}
	
	/**
	 * 根据函数名、参数数组，执行操作，并返回结果Token
	 * @param functionName 函数名
	 * @param position
	 * @param args 注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Constant execute(String functionName , int position , Constant[] args){
		if(functionName == null){
			throw new IllegalArgumentException("函数名为空");
		}
		
		//转化方法参数类型数组
		Class<?>[] parametersType;
		//转化方法参数
		Object[] parameters;
		try {
			parametersType = convertParametersType(functionName , position , args);
			parameters = convertParameters(functionName , position , args);
		} catch (IllegalExpressionException e) {
			throw new IllegalArgumentException("函数\"" + functionName + "\"运行时参数类型错误");
		}
		
		try {
			Object result = FunctionLoader.invokeFunction(functionName, parametersType, parameters);
			
			if(result instanceof Boolean){
				return new Constant(BaseDataMeta.DataType.DATATYPE_BOOLEAN, result);
			
			}else if(result instanceof Date){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DATE , result);
				
			}else if(result instanceof Double){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DOUBLE, result);
							
			}else if(result instanceof Float){
				return new Constant(BaseDataMeta.DataType.DATATYPE_FLOAT, result);
							
			}else if(result instanceof Integer){
				return new Constant(BaseDataMeta.DataType.DATATYPE_INT, result);
							
			}else if(result instanceof Long){
				return new Constant(BaseDataMeta.DataType.DATATYPE_LONG, result);
							
			}else if(result instanceof String){
				return new Constant(BaseDataMeta.DataType.DATATYPE_STRING , result);
							
			}else if(result instanceof List){
				return  new Constant(BaseDataMeta.DataType.DATATYPE_COLLECTION , result);
				
			}else{
				throw new IllegalStateException("解析器运行时内部错误：不支持的函数返回类型");
			}
		} catch (NoSuchMethodException e) {
			//抛异常
			e.printStackTrace();
			throw new IllegalStateException("函数\"" + functionName + "\"不存在或参数类型不匹配");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IllegalStateException("函数\"" + functionName + "\"参数类型不匹配");
		} catch (Exception e) {			
			e.printStackTrace();
			throw new IllegalStateException("函数\"" + functionName + "\"访问异常:" + e.getMessage());
			
		}
	}
	
	/**
	 * 检查函数和参数是否合法，是可执行的
	 * 如果合法，则返回含有执行结果类型的Token
	 * 如果不合法，则返回null
	 * @param functionName
	 * @param position
	 * @param args 注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
	 * @return
	 * @throws IllegalExpressionException 
	 */
	public static Constant varify(String functionName , int position ,  BaseDataMeta[] args) throws IllegalExpressionException{
		if(functionName == null){
			throw new IllegalArgumentException("函数名为空");
		}
		
		String innerFunctionName = "_" + functionName;
		//转化方法参数类型数组
		Class<?>[] parameters = convertParametersType(functionName , position , args);
		
		//通过方法名和参数数组，获取方法，及方法的返回值，并转化成ExpressionToken
		try {
			Method funtion = FunctionExecution.class.getDeclaredMethod(innerFunctionName, parameters);
			Class<?> returnType = funtion.getReturnType();
			
			//转换成ExpressionToken
			if(boolean.class == returnType){
				return  new Constant(BaseDataMeta.DataType.DATATYPE_BOOLEAN, Boolean.FALSE );
				
			}else if(Date.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DATE, null);
				
			}else if(double.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DOUBLE, Double.valueOf(0.0));
				
			}else if(float.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_FLOAT, Float.valueOf(0.0f));
				
			}else if(int.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_INT, Integer.valueOf(0));
				
			}else if(long.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_LONG, Long.valueOf(0L));
				
			}else if(String.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_STRING , null);	
				
			}else if(List.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_COLLECTION , null);	
				
			}else{
				throw new IllegalStateException("解析器内部错误：不支持的函数返回类型");
			}
			
		} catch (SecurityException e) {
			//抛异常
			throw new IllegalExpressionException("函数\"" + functionName + "\"不存在或参数类型不匹配"
					, functionName
					, position
					);
		} catch (NoSuchMethodException e) {
			//抛异常
			throw new IllegalExpressionException("函数\"" + functionName + "\"不存在或参数类型不匹配"
					, functionName
					, position
					);
		}
	}
	
	/**
	 * 函数参数类型转化
	 * @param args
	 * @return
	 * @throws IllegalExpressionException 
	 */
	private static Class<?>[] convertParametersType(String functionName , int position  , BaseDataMeta[] args) throws IllegalExpressionException{
		//参数为空，返回空数组
		if(args == null){
			return new Class<?>[0];
		}		
		//转化方法参数类型数组
		Class<?>[] parametersType = new Class<?>[args.length];
		for(int i = args.length - 1 ; i >= 0 ; i--){
			//判定arg是否为null
			if(BaseDataMeta.DataType.DATATYPE_NULL == args[i].getDataType()){
				//抛异常
				throw new IllegalExpressionException("函数\"" + functionName + "\"参数类型不能为NULL"
						, functionName
						, position
						);
			}			
			//映射Java类型
			parametersType[args.length - 1 - i] = args[i].mapTypeToJavaClass();
		}		
		return parametersType;
	}

	/**
	 * 函数参数转化
	 * @param args
	 * @return
	 * @throws IllegalExpressionException 
	 */
	private static Object[] convertParameters(String functionName , int position  , Constant[] args ) throws IllegalExpressionException{
		//参数为空，返回空数组
		if(args == null){
			return new Object[0];
		}
		
		//转化方法参数类型数组
		Object[] parameters = new Object[args.length];
		for(int i = args.length - 1 ; i >= 0 ; i--){
			try {
				parameters[args.length - 1 - i] = args[i].toJavaObject();
			} catch (ParseException e1) {
				//抛异常
				throw new IllegalExpressionException("函数\"" + functionName + "\"参数转化Java对象错误");
			}
		}		
		return parameters;
	}	
	
	
}
