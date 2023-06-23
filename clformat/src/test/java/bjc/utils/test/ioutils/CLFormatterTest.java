package bjc.utils.test.ioutils;

import bjc.utils.ioutils.format.*;

import org.junit.Test;

import static java.util.Arrays.asList;

import static org.junit.Assert.*;

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
	public void testLiteralString() {
		// @TODO :assertFormat Ben Culkin 1/3/20
		// Convert all of these 'assertEquals(..., format(...))' to use assertFormat
		// instead
		// Print literal strings exactly
		assertEquals("foo", format("foo"));
	}

	@Test
	public void testNumberPrinting() {
		// Test decimal printing
		assertEquals("5", format("~D", 5));
		assertEquals("  5", format("~3D", 5));
		assertEquals("005", format("~3,'0D", 5));
		assertEquals("6|55|35", format("~,,'|,2:D", 0xFFFF));
	}

	@Test
	public void testDecimalPrinting() {
		assertFormat("5.5", "~`D", 5.5);
	}
	
	@Test
	public void testRadixPrinting() {
		// Test radix printing
		assertEquals("1 22", format("~3,,,' ,2:R", 17));
	}

	@Test
	public void testBinaryPrinting() {
		// Test binary printing
		assertEquals("1101", format("~,,' ,4:B", 13));
		assertEquals("1 0001", format("~,,' ,4:B", 17));
		// @NOTE 9/6/18 :CommaPad
		//
		// I'm not sure how this is the expected behavior, unless the
		// comma interval is enforced in the case of a digit padchar.
		// assertEquals("0000 1101 0000 0101", format("~19,0,' ,4:B", 3333));
		assertEquals("000001101 0000 0101", format("~19,0,' ,4:B", 3333));
	}

	@Test
	public void testConditionalPrinting() {
		// Test conditional printing
		assertEquals("print length = 5",
				format("~@[print level = ~D~]~@[print length = ~D~]", null, 5));
	}

	@Test
	public void testIterationPrinting() {
		assertEquals("The winners are: fred harry jill.",
				format("The winners are:~{ ~S~}.", asList("fred", "harry", "jill")));
		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).",
				format("Pairs:~{ (~S, ~S)~}.", asList(1, 1, 2, 2, 3, 3)));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~:{ (~S, ~S)~}.",
				asList(asList(1, 1), asList(2, 2), asList(3, 3))));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).",
				format("Pairs:~@{ (~S, ~S)~}.", 1, 1, 2, 2, 3, 3));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~:@{ (~S, ~S)~}.",
				asList(1, 1), asList(2, 2), asList(3, 3)));
	}

	@Test
	public void testRecursivePrinting() {
		assertEquals("<Foo 5> 7", format("~? ~D", "<~A ~D>", asList("Foo", 5), 7));
		assertEquals("<Foo 5> 7", format("~? ~D", "<~A ~D>", asList("Foo", 5, 14), 7));

		assertEquals("<Foo 5> 7", format("~@? ~D", "<~A ~D>", "Foo", 5, 7));
		assertEquals("<Foo 5> 14", format("~@? ~D", "<~A ~D>", "Foo", 5, 14, 7));
	}

	@Test
	public void testEscapePrinting() {
		assertEquals("Done.", format("Done.~^ ~D warning.~^ ~D error."));
		assertEquals("Done. 3 warning.", format("Done.~^ ~D warning.~^ ~D error.", 3));
		assertEquals("Done. 1 warning. 5 error.",
				format("Done.~^ ~D warning.~^ ~D error.", 1, 5));
	}

	@Test
	public void testCapsPrinting() {
		assertEquals("XIV xiv", format("~@R ~(~@R~)", 14, 14));
	}

//	@Test
	public void testListPrinting() {
		// Test printing a list
		 String fmtStr = "Items:~#[ none~; ~A~; ~A and ~A~:;~@{~#[~; and~]~A~^,~}~].";
//		String fmtStr
//				= "Items:~#[ none~; ~A~; ~A and ~A~:;~@{~#*[ ~A,~; and ~A~; ~A~]~}~].";

		fmt.DEBUG = true;
		assertEquals("Items: none.", format(fmtStr));
		fmt.DEBUG = false;

		assertEquals("Items: foo.", format(fmtStr, "foo"));
		assertEquals("Items: foo and bar.", format(fmtStr, "foo", "bar"));
		assertEquals("Items: foo, bar and baz.", format(fmtStr, "foo", "bar", "baz"));
		assertEquals("Items: foo, bar, baz and quux.",
				format(fmtStr, "foo", "bar", "baz", "quux"));
	}

	@Test
	public void testADirective() {
		assertFormat("foo", "~A", "foo");
		assertFormat("foobar ", "~7A", "foobar");
		assertFormat(" foobar", "~7@A", "foobar");
		assertFormat("     foobar",
				"~#mincol;8,#colinc;2,#minpad;1,#padchar;' @A",
				"foobar");
	}

	@Test
	public void testCasePrinting() {
		assertFormat("abc", "~(~A~)", "AbC", "aBc", "aBc bCD", "abc Cad dAC");
		assertFormat("ABC", "~@:(~A~)", "aBc");
		assertFormat("ABc BCD", "~:(~A ~A~)", "aBc", "bCD");
		assertFormat("Abc Cad dAC", "~@(~A~)", "abc Cad dAC");
	}

	@Test
	public void testFloatPrinting() {
		assertFormat("1", "~`D", 1);
		assertFormat("1.1", "~`D", 1.1);
	}

	@Test
	public void testRandomCases() {
		// Random test cases
		assertEquals("3 dogs are here",
				format("~D dog~:[s are~; is~] here", 3, 3 == 1));
	}

	/*private void assertFormat(String msg, String res, String fomt, Object... params) {
		assertEquals(msg, res, format(fomt, params));
	}*/

	private void assertFormat(String res, String fomt, Object... params) {
		assertEquals(res, format(fomt, params));
	}

	private String format(String str, Object... params) {
		try {
			CLString strang = fmt.compile(str);

			return strang.format(params);
		} catch (Exception ex) {
			ex.printStackTrace();

			return null;
		}
	}
}
