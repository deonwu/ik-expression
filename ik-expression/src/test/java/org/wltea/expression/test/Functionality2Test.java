/**
 * 
 */
package org.wltea.expression.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.wltea.expression.Evaluable;
import org.wltea.expression.Evaluator;
import org.wltea.expression.ExpressionContext;
import org.wltea.expression.ExpressionExecutor;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.datameta.Constant;
import org.wltea.expression.op.ConstantEvaluator;
import org.wltea.expression.op.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能测试
 * @author 林良益
 *
 */
public class Functionality2Test extends TestCase {

	class EvaluableValueObject implements Evaluable<Object>{
		private String token = "";
		public EvaluableValueObject(String a){
			this.token = a;
		}

		@Override
		public Object opNOT() {
			return null;
		}

		@Override
		public Object opNG() {
			return null;
		}

		@Override
		public Object opMUTI(Object obj) {
			return null;
		}

		@Override
		public Object opDIV(Object obj) {
			return null;
		}

		@Override
		public Object opMOD(Object obj) {
			return null;
		}

		@Override
		public Object opPLUS(Object obj) {
			return null;
		}

		@Override
		public Object opMINUS(Object obj) {
			return null;
		}

		@Override
		public boolean boolLT(Object obj) {
			return false;
		}

		@Override
		public boolean boolLE(Object obj) {
			return false;
		}

		@Override
		public boolean boolGT(Object obj) {
			return false;
		}

		@Override
		public boolean boolGE(Object obj) {
			return false;
		}

		@Override
		public boolean boolEQ(Object obj) {
			System.out.println(this.token + ", ==" + obj);
			return true;
		}

		@Override
		public boolean boolNEQ(Object obj) {
			return false;
		}

		@Override
		public boolean boolAND(Object obj) {
			return false;
		}

		@Override
		public boolean boolOR(Object obj) {
			return false;
		}

		@Override
		public Object opOR(Object obj) {
			return null;
		}

		@Override
		public Object opAND(Object obj) {
			return null;
		}
	}
	/**
	 * 简单测试表达式中的各个操作符
	 * @throws Exception
	 */
	public void testOperators()  throws Exception {
		ExpressionContext ctx = new ExpressionContext(){
			public Object bindObject(String name){
				return new EvaluableValueObject(name);
			}
		};

		ctx.setStrict(false);
		ctx.setEvaluator(new Evaluator<Object>(){

			@Override
			public Object evalutor(Operator op, Object first, Object second) throws IllegalExpressionException {
				if(first instanceof Boolean && second instanceof Boolean){
					if(op == Operator.AND){
						return (Boolean)first && (Boolean)second;
					}else if(op == Operator.OR){
						return (Boolean)first || (Boolean)second;
					}
				}

				throw new IllegalExpressionException("不支持的op操作类型");

			}

			@Override
			public boolean canOperator(Operator op, Object first, Object second) throws IllegalExpressionException {
				return op == Operator.AND || op == Operator.OR;
			}
		});

		ExpressionExecutor ee = new ExpressionExecutor(ctx);

		//解析表达式词元
		List<ExpressionToken> expTokens = ee.analyze("AA == BB && cc == dd && dd == 3");

		//转化RPN，并验证
		expTokens = ee.compile(expTokens);
		//执行RPN
		Constant constant = ee.execute(expTokens);
		System.out.println(constant.getDataValue());

	}
	

	
	
	public static void main(String[] args) throws Exception {
		new Functionality2Test().testOperators();
	}
}
