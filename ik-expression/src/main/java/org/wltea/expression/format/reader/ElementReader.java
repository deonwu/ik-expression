/**
 * 
 */
package org.wltea.expression.format.reader;

import java.io.IOException;

import org.wltea.expression.format.Element;
import org.wltea.expression.format.ExpressionReader;
import org.wltea.expression.format.FormatException;

/**
 * @author 林良益，卓诗垚
 * @version 2.0 
 * Oct 9, 2008
 */
public interface ElementReader {
	Element read(ExpressionReader sr) throws FormatException, IOException;
}
