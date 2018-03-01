package bjc.utils.test.ioutils;

import static org.junit.Assert.*;

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
		assertEquals(fmt.formatString("foo"), "foo");
	}

	@Test
	public void testFormatStringD() {
		assertEquals(fmt.formatString("~D", 5), "5");
		assertEquals(fmt.formatString("~3D", 5), "  5");
		assertEquals(fmt.formatString("~3,'0D", 5), "005");
	}

	public void testFormatStringR() {
		assertEquals(fmt.formatString("~R dog~:*~[s are~; is~] here", 3, 3 == 1), "3 dogs are here");
	}

}
