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
 * 读取字符窜类型
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 21, 2008
 */
public class StringTypeReader implements ElementReader {
	public static final char START_MARK = '"';//字符窜开始标志
	public static final char END_MARK = '"';//字符窜结束标志
	
	public static final char ESCAPE_MARK = '\\';//转义符号
	
	/**
	 * 从流中读取字符窜类型的ExpressionToken
	 * @param sr
	 * @return ExpressionToken
	 * @throws FormatException 不是合法的字符窜类型时抛出
	 * @throws IOException
	 */
	public Element read(ExpressionReader sr) throws FormatException, IOException {
		int index = sr.getCruuentIndex();
		StringBuffer sb = new StringBuffer();
		int b = sr.read();
		if (b == -1 || b != START_MARK) {
			throw new FormatException("不是有效的字符窜开始");
		}
		
		while ((b = sr.read()) != -1) {
			char c = (char)b;
			if (c == ESCAPE_MARK) {//遇到转义字符
				c = getEscapeValue((char)sr.read());
			} else if (c == END_MARK) {//遇到非转义的引号
				return new Element(sb.toString(), index, ElementType.STRING);
			}
			sb.append(c);
		}
		throw new FormatException("不是有效的字符窜结束");
	}
	
	/**
	 * 可转义字符有\"nt
	 * @param c
	 * @return
	 * @throws FormatException
	 */
	private static char getEscapeValue(char c) throws FormatException {
		if (c == '\\' || c == '\"') {
			return c;
		} else if (c == 'n') {
			return '\n';
		} else if (c == 'r') {
			return '\r';
		} else if (c == 't') {
			return '\t';
		}
		throw new FormatException("字符转义出错");
	}
}
