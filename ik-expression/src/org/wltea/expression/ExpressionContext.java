package org.wltea.expression;

import java.util.HashMap;
import java.util.Map;

import org.wltea.expression.datameta.Variable;

/**
 * 表达式计算，过程中用到的变量或者函数。
 * 
 * @author deonwu
 *
 */
public class ExpressionContext extends HashMap<String, Object> {

	private static final long serialVersionUID = -373852423907495496L;
	private Evaluator evaluator = null;
	
	/**
	 * 是否做严格的表达是检查。
	 * 如果有包含对象的求值，不需要做严格的类型检查，只需要检查参数个数就可以了。
	 * 
	 * 默认是严格检查。
	 */
	private boolean isStrict = true;

	public Variable getVariable(String variableName){
		Object obj = null; //this.get(variableName);
		Variable var = null;
		
		if(containsKey(variableName)){
			obj = get(variableName);
		}else {
			obj = bindObject(variableName);
		}
		
		if(obj instanceof Variable){
			var = (Variable)obj;
		}else {
			var = Variable.createVariable(variableName, obj);		
		}
		
		return var;
	}
	
	/**
	 * 根据一个字符串，关联一个对象。变量、常量、方法等。
	 * @param name -- 表达式中的符号。
	 * @return -- 返回符号关联的对象。
	 */
	public Object bindObject(String name){
		return null;
	}
	
	/**
	 * 所有变量都转换为内部格式
	 * @return
	 */
	public Map<String, Variable> getVariableMap(){
		Map<String, Variable> tmp = new HashMap<String, Variable>();
		
		for(String key : keySet()){
			tmp.put(key, getVariable(key));
		}
		
		return tmp;	
	}

	/**
	 * 返回当前的求值器。
	 * @return
	 */
	public Evaluator<?> getEvaluator() {
		return evaluator;
	}

	/**
	 * 设置求值器。
	 * @param evaluator
	 */
	public void setEvaluator(Evaluator<?> evaluator) {
		this.evaluator = evaluator;
	}

	public boolean isStrict() {
		return isStrict;
	}

	public void setStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}
	
	
}
