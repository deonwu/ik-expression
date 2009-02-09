/**
 * 
 */
package org.wltea.expression.format.reader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.wltea.expression.format.Element;
import org.wltea.expression.format.ExpressionReader;
import org.wltea.expression.format.FormatException;
import org.wltea.expression.format.Element.ElementType;

/**
 * 读取运算符类型
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 21, 2008
 */
public class OperatorTypeReader implements ElementReader {
	
private static final Set<String> OPERATOR_WORDS = new HashSet<String>();
	
	static{
		OPERATOR_WORDS.add("+");
		OPERATOR_WORDS.add("-");
		OPERATOR_WORDS.add(">");
		OPERATOR_WORDS.add("<");
		OPERATOR_WORDS.add(">=");
		OPERATOR_WORDS.add("<=");
		OPERATOR_WORDS.add("==");
		OPERATOR_WORDS.add("!=");
		OPERATOR_WORDS.add("*");
		OPERATOR_WORDS.add("/");
		OPERATOR_WORDS.add("%");
		OPERATOR_WORDS.add("&&");
		OPERATOR_WORDS.add("||");
		OPERATOR_WORDS.add("!");
		OPERATOR_WORDS.add("#");
		OPERATOR_WORDS.add("?:");
		OPERATOR_WORDS.add("?");
		OPERATOR_WORDS.add(":");
	}
	
	/**
	 * 判断字符串是否是合法的操作符
	 * @param tokenText
	 * @return
	 */
	public static boolean isOperatorWord(String tokenText){
		return OPERATOR_WORDS.contains(tokenText);
	}
	
	/**
	 * 从流中读取运算符类型的ExpressionToken
	 * @param sr
	 * @return
	 * @throws FormatException 不是合法的运算符类型时抛出
	 * @throws IOException
	 */
	public Element read(ExpressionReader sr) throws FormatException, IOException {
		int index = sr.getCruuentIndex();
		StringBuffer sb = new StringBuffer();
		int b = sr.read();
		if (b == -1) {
			throw new FormatException("表达式已结束");
		}
		char c = (char)b;
		sb.append(c);
		if (isOperatorWord(sb.toString())) {
			if (sb.length() == 1) {//两个符号的运算符优先，如<=，不应该认为是<运算符
				sr.mark(0);
				b = sr.read();
				if (b != -1) {
					if (isOperatorWord(sb.toString() + (char)b)) {
						return new Element(sb.toString() + (char)b, index,
								ElementType.OPERATOR);
					}
				}
				sr.reset();
			}
			return new Element(sb.toString(), index,
					ElementType.OPERATOR);
		}
		
		while ((b = sr.read()) != -1) {
			c = (char)b;
			sb.append(c);
			if (isOperatorWord(sb.toString())) {
				return new Element(sb.toString(), index,
						ElementType.OPERATOR);
			}
			if (VariableTypeReader.STOP_CHAR.indexOf(c) >= 0) {//单词停止符
				throw new FormatException("不是有效的运算符：" + sb.toString());
			}
		}
		throw new FormatException("不是有效的运算符结束");
	}
	
	/**
	 * 测试是否为运算符
	 * @param sr
	 * @return
	 * @throws IOException
	 */
	public static boolean isOperatorStart(ExpressionReader sr) throws IOException {
		sr.mark(0);
		try {
			StringBuffer sb = new StringBuffer();
			int b = sr.read();
			if (b == -1) {
				return false;
			}
			char c = (char)b;
			sb.append(c);
			if (isOperatorWord(sb.toString())) {
				return true;
			}
			while ((b = sr.read()) != -1) {
				c = (char)b;
				sb.append(c);
				if (isOperatorWord(sb.toString())) {
					return true;
				}
				if (VariableTypeReader.STOP_CHAR.indexOf(c) >= 0) {//单词停止符
					return false;
				}
				
			}
			return false;
		} finally{
			sr.reset();
		}
		
	}
}
