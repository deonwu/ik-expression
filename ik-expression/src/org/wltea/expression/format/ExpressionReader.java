/**
 * 
 */
package org.wltea.expression.format;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

import org.wltea.expression.format.reader.ElementReader;
import org.wltea.expression.format.reader.ElementReaderFactory;

/**
 * 表达式读取器
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Sep 21, 2008
 */
public class ExpressionReader extends StringReader {
	
	private static final String IGNORE_CHAR = " \r\n\t";//词元间的忽略字符
	
	private int currentIndex = 0;//当前索引
	
	private int markIndex = 0;//被标记后索引
	
	private boolean prefixBlank = false;//与上一个读到的ElementToken之间是否有空格
	
	public ExpressionReader(String s) {
		super(s);
	}
	
	 /**
	  * 取得当前位置
	  * @return
	  */
	public int getCruuentIndex() {
		return currentIndex;
	}
	
	/**
	 * Element之前是否有空格
	 * @return
	 */
	public boolean isPrefixBlank() {
		return prefixBlank;
	}

	public void setPrefixBlank(boolean prefixBlank) {
		this.prefixBlank = prefixBlank;
	}

	@Override
	public int read() throws IOException {
		int c = super.read();
		if (c != -1) {
			currentIndex++;
			markIndex++;
		}
		return c;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException {
		int c = super.read(cbuf);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}
	
	@Override
	public int read(CharBuffer target) throws IOException {
		int c = super.read(target);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}
    
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int c = super.read(cbuf, off, len);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}
	
	@Override
	public void reset() throws IOException {
		super.reset();
		currentIndex = currentIndex - markIndex;
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		super.mark(readAheadLimit);
		markIndex = 0;
	}
	 
	/**
	 * 以流形式读取ExpressionToken
	 * @return ExpressionToken
	 * @throws IOException
	 * @throws FormatException
	 * @throws AntipathicException
	 */
	public Element readToken() throws IOException, FormatException {
		prefixBlank = false;
		while (true) {
			//去除空格
			mark(0);//标记
			int b = read();
			if (b == -1) {
				return null;
			}
			char c = (char)b;
			if (IGNORE_CHAR.indexOf(c) >= 0) {//去除开始的空格
				prefixBlank = true;
				continue;
			}
			reset();//重置
			
			//构造一个词元读取器
			ElementReader er = ElementReaderFactory.createElementReader(this);
			
			return er.read(this);
		}
	}
	
	public static void main(String[] a) {
		ExpressionReader eReader = new ExpressionReader(" aa+\"AB\\\\CD\"!=null&&[2008-1-1 12:9]-$max(aa,bb,\"cc\")>2l3f4d1");
		
		Element ele = null;
		try {
			while ((ele = eReader.readToken()) != null) {
				System.out.println(ele.getType() + "……" + ele.getText() + "……" + ele.getIndex());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}
}
