/**
 * 
 */
package org.wltea.expression.format.reader;

import java.io.IOException;

import org.wltea.expression.format.Element;
import org.wltea.expression.format.ExpressionReader;
import org.wltea.expression.format.FormatException;
import org.wltea.expression.format.Element.ElementType;

/**
 * 读取函数类型
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 21, 2008
 */
public class FunctionTypeReader implements ElementReader {
	public static final char START_MARK = '$';//函数开始
	public static final char END_MARK = '(';//函数结束
	
	/**
	 * 从流中读取函数类型的ExpressionToken
	 * @param sr
	 * @return
	 * @throws FormatException
	 * @throws IOException
	 */
	public Element read(ExpressionReader sr) throws FormatException, IOException {
		int index = sr.getCruuentIndex();
		StringBuffer sb = new StringBuffer();
		int b = sr.read();
		if (b == -1 || b != FunctionTypeReader.START_MARK) {
			throw new FormatException("不是有效的函数开始");
		}
		boolean readStart = true;
		while ((b = sr.read()) != -1) {
			char c = (char)b;
			if (c == FunctionTypeReader.END_MARK) {
				if (sb.length() == 0) {
					throw new FormatException("函数名称不能为空");
				}
				sr.reset();
				return new Element(sb.toString(), index, ElementType.FUNCTION);
			}
			if (!Character.isJavaIdentifierPart(c)) {
				throw new FormatException("名称不能为非法字符：" + c);
			}
			if (readStart) {
				if (!Character.isJavaIdentifierStart(c)) {
					throw new FormatException("名称开头不能为字符：" + c);
				}
				readStart = false;
			}
			sb.append(c);
			sr.mark(0);
		}
		throw new FormatException("不是有效的函数结束");
	}
}
