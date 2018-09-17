package bjc.utils.test.ioutils;

import static org.junit.Assert.*;

import java.io.IOException;

import java.util.Arrays;

import bjc.utils.ioutils.format.CLFormatter;

import org.junit.Test;

import static java.util.Arrays.asList;
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
		// Print literal strings exactly
		assertEquals("foo", format("foo"));
	}

	@Test
	public void testDecimalPrinting() {
		// Test decimal printing
		assertEquals("5",   format("~D", 5));
		assertEquals("  5", format("~3D", 5));
		assertEquals("005", format("~3,'0D", 5));
		assertEquals("6|55|35", format("~,,'|,2:D", 0xFFFF));
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
		assertEquals("print length = 5", format("~@[print level = ~D~]~@[print length = ~D~]", null, 5));
	}

	@Test
	public void testIterationPrinting() {
		assertEquals("The winners are: fred harry jill.", format("The winners are:~{ ~S~}.",
					asList("fred", "harry", "jill")));
		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~{ (~S, ~S)~}.",
					asList(1, 1, 2, 2, 3, 3)));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~:{ (~S, ~S)~}.",
					asList(asList(1, 1), asList(2, 2), asList(3, 3))));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~@{ (~S, ~S)~}.",
					1, 1, 2, 2, 3, 3));

		assertEquals("Pairs: (1, 1) (2, 2) (3, 3).", format("Pairs:~:@{ (~S, ~S)~}.",
					asList(1, 1), asList(2, 2), asList(3, 3)));
	}

	@Test
	public void testRecursivePrinting() {
		assertEquals("<Foo 5> 7", format("~? ~D", "<~A ~D>", asList("Foo", 5), 7));
		assertEquals("<Foo 5> 7", format("~? ~D", "<~A ~D>", asList("Foo", 5, 14), 7));

		assertEquals("<Foo 5> 7",  format("~@? ~D", "<~A ~D>", "Foo", 5, 7));
		assertEquals("<Foo 5> 14", format("~@? ~D", "<~A ~D>", "Foo", 5, 14, 7));
	}

	@Test
	public void testEscapePrinting() {
		assertEquals("Done.",                     format("Done.~^ ~D warning.~^ ~D error."));
		assertEquals("Done. 3 warning.",          format("Done.~^ ~D warning.~^ ~D error.", 3));
		assertEquals("Done. 1 warning. 5 error.", format("Done.~^ ~D warning.~^ ~D error.", 1, 5));
	}

	@Test
	public void testCapsPrinting() {
		assertEquals("XIV xiv", format("~@R ~(~@R~)", 14, 14));
	}

	@Test
	public void testListPrinting() {
		// Test printing a list
		// String fmtStr = "Items:~#[ none~; ~A~; ~A and ~A~:;~@{~#[~; and~] ~A~^,~}~].";
		String fmtStr = "Items:~#[ none~; ~A~; ~A and ~A~:;~@{~#*[ ~A,~; and ~A~; ~A~]~}~].";

		assertEquals("Items: none.",                   format(fmtStr));
		assertEquals("Items: foo.",                    format(fmtStr, "foo"));
		assertEquals("Items: foo and bar.",            format(fmtStr, "foo", "bar"));
		assertEquals("Items: foo, bar and baz.",       format(fmtStr, "foo", "bar", "baz"));
		assertEquals("Items: foo, bar, baz and quux.", format(fmtStr, "foo", "bar", "baz", "quux"));
	}

	@Test
	public void testRandomCases() {
		// Random test cases
		assertEquals("3 dogs are here", format("~D dog~:[s are~; is~] here", 3, 3 == 1));
	}

	private String format(String str, Object... params) {
		try {
			return fmt.formatString(str, params);
		} catch (IOException ioex) {
			return null;
		}
	}
}