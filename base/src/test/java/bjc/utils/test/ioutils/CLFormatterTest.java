package bjc.utils.test.ioutils;

import static org.junit.Assert.*;

import java.io.IOException;

import bjc.utils.ioutils.format.CLFormatter;

import org.junit.Test;

/**
 * Tests for CL format strings.
 * 
 * @author EVE
 *
 */
@SuppressWarnings("javadoc")
public class CLFormatterTest {
	private CLFormatter fmt = new CLFormatter();

	@Test
	public void testFormatStringLiteral() {
		assertEquals("foo", format("foo"));
	}

	@Test
	public void testFormatStringD() {
		assertEquals("5",   format("~D", 5));
		assertEquals("  5", format("~3D", 5));
		assertEquals("005", format("~3,'0D", 5));
	}

	public void testFormatStringR() {
		assertEquals("3 dogs are here", format("~R dog~:*~[s are~; is~] here", 3, 3 == 1));
	}

	private String format(String str, Object... params) {
		try {
			return fmt.formatString(str, params);
		} catch (IOException ioex) {
			return null;
		}
	}
}
