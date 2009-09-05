/**
 * 
 */
package org.wltea.expression;

import java.util.HashMap;
import java.util.Map;

import org.wltea.expression.datameta.Variable;

/**
 * 表达式上下文变量容器
 * 使用本地线程对象，传递上下文变量映射表
 * @author 林良益，卓诗垚
 * @version 2.0 
 * 2008-09-23
 */
public class VariableContainer {
	
	private static ThreadLocal<Map<String , Variable>> variableMapThreadLocal = new ThreadLocal<Map<String , Variable>>();
	
	
	public static Map<String , Variable> getVariableMap(){
		Map<String , Variable> variableMap = variableMapThreadLocal.get();
		if(variableMap == null){
			variableMap = new HashMap<String , Variable>();
			variableMapThreadLocal.set(variableMap);
		}
		return variableMap;
	}
	
	public static void setVariableMap(Map<String , Variable> variableMap){
		removeVariableMap();
		if(variableMap != null){
			variableMapThreadLocal.set(variableMap);
		}
	}
	
	public static void removeVariableMap(){
		Map<String , Variable> variableMap = variableMapThreadLocal.get();
		if(variableMap != null){
			variableMap.clear();
		}		
		variableMapThreadLocal.remove();		
	}	
	
	public static void addVariable(Variable variable){
		if(variable != null){
			getVariableMap().put(variable.getVariableName(), variable);
		}
	}
	
	public static Variable getVariable(String variableName){
		if(variableName != null ){
			return getVariableMap().get(variableName);
		}else {
			return null;
		}
	}
	
	public static Variable removeVariable(String variableName){
		return getVariableMap().remove(variableName);
	}
	
	public static Variable removeVariable(Variable variable){
		if(variable != null){
			return getVariableMap().remove(variable.getVariableName());
		}else {
			return null;
		}			
	}

}
