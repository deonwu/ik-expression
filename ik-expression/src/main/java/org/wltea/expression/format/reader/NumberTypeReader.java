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
 * 读取数字类型
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 21, 2008
 */
public class NumberTypeReader implements ElementReader {
	public static final String NUMBER_CHARS = "01234567890.";//表示数值的字符
	public static final String LONG_MARKS = "lL";//long的结尾标志
	public static final String FLOAT_MARKS = "fF";//float的结尾标志
	public static final String DOUBLE_MARKS = "dD";//double的结尾标志
	
	/**
	 * 从流中读取数字类型的ExpressionToken
	 * @param sr
	 * @return
	 * @throws FormatException 不是合法的数字类型时抛出
	 */
	public Element read(ExpressionReader sr) throws FormatException, IOException {
		int index = sr.getCruuentIndex();
		StringBuffer sb = new StringBuffer();
		int b = -1;
		while ((b = sr.read()) != -1) {
			char c = (char)b;
			if (NUMBER_CHARS.indexOf(c) == -1) {
				if (LONG_MARKS.indexOf(c) >= 0) {
					if (sb.indexOf(".") >= 0) {//有小数点
						throw new FormatException("long类型不能有小数点");
					}
					return new Element(sb.toString(), index, ElementType.LONG);
				} else if (FLOAT_MARKS.indexOf(c) >= 0) {
					checkDecimal(sb);
					return new Element(sb.toString(), index, ElementType.FLOAT);
				} else if (DOUBLE_MARKS.indexOf(c) >= 0) {
					checkDecimal(sb);
					return new Element(sb.toString(), index, ElementType.DOUBLE);
				} else {
					sr.reset();
					if (sb.indexOf(".") >= 0) {//没有结束标志，有小数点，为double
						checkDecimal(sb);
						return new Element(sb.toString(), index, ElementType.DOUBLE);
					} else {//没有结束标志，无小数点，为int
						return new Element(sb.toString(), index, ElementType.INT);
					}
				}
			}
			sb.append(c);
			sr.mark(0);
		}
		//读到结未
		if (sb.indexOf(".") >= 0) {//没有结束标志，有小数点，为double
			checkDecimal(sb);
			return new Element(sb.toString(), index, ElementType.DOUBLE);
		} else {//没有结束标志，无小数点，为int
			return new Element(sb.toString(), index, ElementType.INT);
		}
	}
	
	/**
	 * 检查是否只有一个小数点
	 * @param sb
	 * @throws FormatException
	 */
	public static void checkDecimal(StringBuffer sb) throws FormatException {
		if (sb.indexOf(".") != sb.lastIndexOf(".")) {
			throw new FormatException("数字最多只能有一个小数点");
		}
	}
}
