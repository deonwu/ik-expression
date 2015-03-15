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
import org.wltea.expression.datameta.Reference;

/**
 * 运算符及内嵌方法调用
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-18
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
	 * @throws IllegalExpressionException 
	 */
	@SuppressWarnings("unchecked")
	public static Constant execute(String functionName , int position , Constant[] args) throws IllegalExpressionException{
		if(functionName == null){
			throw new IllegalArgumentException("函数名为空");
		}
		if(args == null){
			throw new IllegalArgumentException("函数参数列表为空");
		}
		for(int i = 0 ; i < args.length ; i++){
			//如果参数为引用类型，则执行引用
			if(args[i].isReference()){
				Reference ref = (Reference)args[i].getDataValue();
				args[i] = ref.execute();
			}			
		}
		
		//转化方法参数
		Object[] parameters;
		try {
			parameters = convertParameters(functionName , position , args);
		} catch (IllegalExpressionException e) {
			throw new IllegalArgumentException("函数\"" + functionName + "\"运行时参数类型错误");
		}
		
		try {
			Object result = FunctionLoader.invokeFunction(functionName, parameters);
			
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
				return  new Constant(BaseDataMeta.DataType.DATATYPE_LIST , result);
				
			}else {
				return new Constant(BaseDataMeta.DataType.DATATYPE_OBJECT , result);	
				
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

		//通过方法名和参数数组，获取方法，及方法的返回值，并转化成ExpressionToken
		try {
			Method funtion = FunctionLoader.loadFunction(functionName);
			//校验方法参数类型
			Class<?>[] parametersType = funtion.getParameterTypes();
			if(args.length == parametersType.length){
				//注意，传入参数的顺序是颠倒的
				for(int i = args.length - 1 ; i >= 0  ; i--){
					Class<?> javaType = args[i].mapTypeToJavaClass();
					if(javaType != null){						
						if(!isCompatibleType(parametersType[parametersType.length - i - 1] , javaType)){
							//抛异常
							throw new IllegalExpressionException("函数\"" + functionName + "\"参数类型不匹配,函数参数定义类型为：" + parametersType[i].getName() + " 传入参数实际类型为：" + javaType.getName() 
									, functionName
									, position
									);
						}						
					}else{
						//传入参数为null，忽略类型校验						
					}					
				}				
			}else{
				//抛异常
				throw new IllegalExpressionException("函数\"" + functionName + "\"参数个数不匹配"
						, functionName
						, position
						);
				
			}
			
			Class<?> returnType = funtion.getReturnType();
			
			//转换成ExpressionToken
			if(boolean.class == returnType 
					|| Boolean.class == returnType){
				return  new Constant(BaseDataMeta.DataType.DATATYPE_BOOLEAN, Boolean.FALSE );
				
			}else if(Date.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DATE, null);
				
			}else if(double.class == returnType
					|| Double.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_DOUBLE, Double.valueOf(0.0));
				
			}else if(float.class == returnType
					|| Float.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_FLOAT, Float.valueOf(0.0f));
				
			}else if(int.class == returnType
					|| Integer.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_INT, Integer.valueOf(0));
				
			}else if(long.class == returnType
					|| Long.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_LONG, Long.valueOf(0L));
				
			}else if(String.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_STRING , null);	
				
			}else if(List.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_LIST , null);	
				
			}else if(Object.class == returnType){
				return new Constant(BaseDataMeta.DataType.DATATYPE_OBJECT , null);	
				
			}else if(void.class == returnType
					|| Void.class == returnType){
					return new Constant(BaseDataMeta.DataType.DATATYPE_OBJECT , null);					
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

	/**
	 * 检查数据类型的兼容性
	 * 类型相同，一定兼容
	 * 如果parametersType 为Object 则兼容所有类型
	 * 如果parametersType 为double 则兼容 int ，long ，float
	 * 如果parametersType 为float 则兼容 int ，long 
	 * 如果parametersType 为long 则兼容 int  
	 * @param parametersType 方法定义的参数类型
	 * @param argType 实际参数类型
	 * @return
	 */
	private static boolean isCompatibleType(Class<?> parametersType , Class<?> argType){
		if(Object.class == parametersType){
			return true;
			
		}else if(parametersType == argType){
			return true;
			
		}else if(double.class == parametersType){
			return float.class == argType || long.class == argType || int.class == argType;
			
		}else if(Double.class == parametersType){
			return double.class == argType;
			
		}else if(float.class == parametersType){
			return long.class == argType || int.class == argType;
			
		}else if(Float.class == parametersType){
			return float.class == argType;
			
		}else if(long.class == parametersType){
			return int.class == argType;
			
		}else if(Long.class == parametersType){
			return long.class == argType;
			
		}else if(Integer.class == parametersType){
			return int.class == argType;
			
		}
		return false;

	}	
	
	
}
