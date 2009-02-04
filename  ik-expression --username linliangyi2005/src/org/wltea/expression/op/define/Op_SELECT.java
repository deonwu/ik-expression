/**
 * 
 */
package org.wltea.expression.op.define;

import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta;
import org.wltea.expression.datameta.Constant;
import org.wltea.expression.op.IOperatorExecution;

/**
 * @author 林良益
 * 2009-02-03 
 */
public class Op_SELECT implements IOperatorExecution {

	/**
	 * 
	 */
	public Op_SELECT() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#execute(org.wltea.expression.datameta.Constant[])
	 */
	public Constant execute(Constant[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.wltea.expression.op.IOperatorExecution#verify(int, org.wltea.expression.datameta.BaseDataMeta[])
	 */
	public Constant verify(int opPositin, BaseDataMeta[] args)
			throws IllegalExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

}
