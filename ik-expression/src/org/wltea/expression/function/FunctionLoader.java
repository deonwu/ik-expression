/**
 * 
 */
package org.wltea.expression.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;


/**
 * 表达式中函数加载器，从配置文件中加载可用的外部方法
 * 
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Feb 3, 2009
 */
@SuppressWarnings("unchecked")
public class FunctionLoader {
	
	private static FunctionLoader single = new FunctionLoader();
	
	//所有方法Map
	private HashMap<String, Function> functionMap = new HashMap<String, Function>();
	
	static {
		Properties prop = new Properties();
		try {
			//从属性文件中加载配置信息
			prop.load(FunctionLoader.class.getResourceAsStream("/functionConfig.properties"));
			for (Iterator it = prop.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				String value = prop.getProperty(key);
				if (value == null) {
					continue;
				}
				String[] tem = value.trim().split("@");
				if (tem.length != 2) {
					continue;
				}
				single.functionMap.put(key.trim(), single.new Function(Class.forName(tem[0]), tem[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * 私有，禁止外部新建
	 */
	private FunctionLoader() {
		
	}

	/**
	 * 表达式可用函数除了从配置文件“functionConfig.properties”加载外，
	 * 还可以通过此方法运行时添加
	 * @param functionName 方法别名，表达式使用的名称
	 * @param instance 调用的实例名
	 * @param methodName 调用的方支渠名
	 */
	public static void addFunction(String functionName, Object instance, String methodName) {
		if (functionName == null || instance == null || methodName == null) {
			return;
		}
		single.functionMap.put(functionName, single.new Function(instance, methodName));
	}
	
	/**
	 * 跟据名称与参数类型加载方法
	 * @param functionName 方法别名
	 * @param parametersType 方法参数类型
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method loadFunction(String functionName, 
			Class<?>[] parametersType) throws NoSuchMethodException {
		Function f = single.functionMap.get(functionName);
		if (f == null) {
			throw new NoSuchMethodException();
		}
		return f.load(parametersType);
	}
	
	/**
	 * 执行方法
	 * @param functionName 方法别名
	 * @param parametersType 方法参数类型
	 * @param parameters 方法参数
	 * @return 
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static Object invokeFunction(String functionName, Class<?>[] parametersType, 
		Object[] parameters)  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Function f = single.functionMap.get(functionName);
		if (f == null) {
			throw new NoSuchMethodException();
		}
		return f.invoke(parametersType, parameters);

	}
	
	/**
	 * 
	 * @author zsy
	 * @version Feb 7, 2009
	 */
	class Function {
		String _name;
		Class _class;
		Object _instance;
		Function(Class _class, String _name) {
			this._name = _name;
			this._class = _class;
			if (_class != null) {
				try {
					this._instance = _class.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		Function(Object _instance, String _name) {
			this._name = _name;
			this._instance = _instance;
			this._class = _instance.getClass();
		}
		
		/**
		 * 通过方法加载方法
		 * @param parametersType
		 * @return
		 * @throws NoSuchMethodException
		 */
		Method load(Class<?>[] parametersType) throws NoSuchMethodException {
			if (_class != null) {
				return _class.getMethod(_name, parametersType);
			} else {
				throw new NoSuchMethodException();
			}
		}
		
		/**
		 * 执行方法
		 * @param parametersType
		 * @param parameters
		 * @return
		 * @throws NoSuchMethodException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 * @throws InstantiationException
		 */
		Object invoke(Class<?>[] parametersType, Object[] parameters) throws NoSuchMethodException, 
				IllegalAccessException, InvocationTargetException {
			Method m = load(parametersType);
			return m.invoke(_instance, parameters);
		}
	}

}
