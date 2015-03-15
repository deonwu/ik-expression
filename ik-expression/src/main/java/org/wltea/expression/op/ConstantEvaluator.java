package org.wltea.expression.op;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.wltea.expression.Evaluable;
import org.wltea.expression.Evaluator;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Reference;

public class ConstantEvaluator implements Evaluator<Constant> {
	@SuppressWarnings("rawtypes")
	private Evaluator evaluator = null;
	
	private Map<String, Method> evalMethod = new HashMap<String, Method>();	
	
	public ConstantEvaluator(Evaluator<?> root){
		this.evaluator = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Constant evalutor(Operator op, Constant first, Constant second) throws IllegalExpressionException {
		
		Object firstObj = null;
		Object secondObj = null;
		
		//如果第一参数为引用，则执行引用
		if(first.isReference()){
			Reference firstRef = (Reference)first.getDataValue();
			first = firstRef.execute(this);
		}
		firstObj = first.getDataValue();
		
		//如果第二参数为引用，则执行引用
		if(second != null && second.isReference()){
			Reference secondRef = (Reference)second.getDataValue();
			second = secondRef.execute(this);
		}
		if(second != null){
			secondObj = second.getDataValue();
		}
		
		Object obj = null;
		if(evaluator != null && evaluator.canOperator(op, firstObj, secondObj)){
			obj = evaluator.evalutor(op, firstObj, secondObj);
		}else if (firstObj instanceof Evaluable){
			Evaluable o = (Evaluable)firstObj;
			if(op.equals(Operator.AND)){
				obj = new Boolean(o.boolAND(secondObj));
			}else if(op.equals(Operator.BAND)){
				obj = o.opAND(secondObj);
			}else if(op.equals(Operator.BNOT)){
				obj = o.opNOT();
			}else if(op.equals(Operator.BOR)){
				obj = o.opOR(secondObj);
			}else if(op.equals(Operator.DIV)){
				obj = o.opDIV(secondObj);
			}else if(op.equals(Operator.EQ)){
				obj = new Boolean(o.boolEQ(secondObj));
			}else if(op.equals(Operator.GE)){
				obj = new Boolean(o.boolGE(secondObj));
			}else if(op.equals(Operator.GT)){
				obj = new Boolean(o.boolGT(secondObj));
			}else if(op.equals(Operator.LE)){
				obj = new Boolean(o.boolLE(secondObj));
			}else if(op.equals(Operator.LT)){
				obj = new Boolean(o.boolLT(obj));
			}else if(op.equals(Operator.MINUS)){
				obj = o.opMINUS(secondObj);
			}else if(op.equals(Operator.MOD)){
				obj = o.opMOD(secondObj);
			}else if(op.equals(Operator.MUTI)){
				obj = o.opMUTI(secondObj);
			}else if(op.equals(Operator.NEQ)){
				obj = new Boolean(o.boolNEQ(secondObj));
			}else if(op.equals(Operator.NG)){
				obj = o.opNG();
			}else if(op.equals(Operator.NOT)){
				obj = o.opNOT();
			}else if(op.equals(Operator.OR)){
				obj = o.opOR(secondObj);
			}else if(op.equals(Operator.PLUS)){
				obj = o.opPLUS(secondObj);
			}else {
				throw new IllegalExpressionException("不支持:" + op.getToken() + "操作, 在Evaluable对象。");	
			}			
		}else{
			Method m = getOperator(op, firstObj);
			if(m != null){
				Object[] args = second != null ? new Object[]{secondObj} : new Object[]{}; 
				try {
					obj = m.invoke(firstObj, args);
				} catch (Exception e) {
					throw new IllegalExpressionException(e.toString(), e.getCause());
				}
			}else {
				throw new IllegalExpressionException("不支持:" + op.getToken() + "操作, 在对象:" + firstObj.getClass().getName());
			}
		}
		
		return new Constant(DataType.DATATYPE_OBJECT, obj);
	}

	@Override
	public boolean canOperator(Operator op, Constant first, Constant second) throws IllegalExpressionException {
		Object firstObj = null;
		Object secondObj = null;
		
		//如果第一参数为引用，则执行引用
		if(first.isReference()){
			Reference firstRef = (Reference)first.getDataValue();
			first = firstRef.execute(this);
		}
		firstObj = first.getDataValue();
		
		//如果第二参数为引用，则执行引用
		if(second != null && second.isReference()){
			Reference secondRef = (Reference)second.getDataValue();
			second = secondRef.execute(this);
		}
		if(second != null){
			secondObj = second.getDataValue();
		}
		
		if(evaluator != null && evaluator.canOperator(op, firstObj, secondObj)){
			return true;
		}else if(firstObj instanceof Evaluable){
			return true;
		}else if(getOperator(op, firstObj) != null){
			return true;
		}
		
		return false;
	}

	private Method getOperator(Operator op, Object obj){
		if(obj == null) return null;
		String ck = obj.getClass().getName() + op.getToken();
		
		if(!evalMethod.containsKey(ck)){
			
			for(Method om : obj.getClass().getMethods()){
				org.wltea.expression.annotation.Operator anno = om.getAnnotation(org.wltea.expression.annotation.Operator.class);
				if(anno != null && anno.sign().equals(op.getToken())){
					evalMethod.put(ck, om);
					break;
				}
			}			
		}
		
		return evalMethod.get(ck);		
	}
}
