package org.wltea.expression;

/**
 * 支持运算符操作的对象接口。
 * 
 * @author deonwu
 *
 */
public interface Evaluable<T> {

	/**
	 * 逻辑非 ~
	 * @return
	 */
	public T opNOT();
	
	/**
	 * 求负数 -
	 * @return
	 */
	public T opNG();
	
	/**
	 * 乘法运算 *
	 * @param obj -- 乘数
	 * @return
	 */
	public T opMUTI(T obj);

	/**
	 * 除法运算 /
	 * @param obj
	 * @return
	 */
	public T opDIV(T obj);
	
	/**
	 * 求模 %
	 * @param obj
	 * @return
	 */
	public T opMOD(T obj);
	
	/**
	 * 加法 + 
	 * @param obj
	 * @return
	 */
	public T opPLUS(T obj);
	
	
	/**
	 * 减法 - 
	 * @param obj
	 * @return
	 */
	public T opMINUS(T obj);
	
	/**
	 * 逻辑小于 <
	 * @param obj
	 * @return
	 */
	public boolean boolLT(T obj);
	
	/**
	 * 逻辑小于等于 <=
	 * @param obj
	 * @return
	 */
	public boolean boolLE(T obj);
	
	/**
	 * 逻辑大于 >
	 * @param obj
	 * @return
	 */
	public boolean boolGT(T obj);
	
	/**
	 * 逻辑大于等于 >=
	 * @param obj
	 * @return
	 */
	public boolean boolGE(T obj);
	
	/**
	 * 逻辑等于 ==
	 * @param obj
	 * @return
	 */
	public boolean boolEQ(T obj);
	
	/**
	 * 逻辑不等于 !=
	 * @param obj
	 * @return
	 */
	public boolean boolNEQ(T obj);

	/**
	 * 逻辑与 &&
	 * @param obj
	 * @return
	 */
	public boolean boolAND(T obj);
	
	/**
	 * 逻辑或 ||
	 * @param obj
	 * @return
	 */
	public boolean boolOR(T obj);

	/**
	 * 或运算 |
	 * @param obj
	 * @return
	 */
	public T opOR(T obj);	

	/**
	 * 且运算 &
	 * @param obj
	 * @return
	 */
	public T opAND(T obj);		
}
