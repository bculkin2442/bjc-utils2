package bjc.utils.test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import bjc.utils.math.NumberUtils;

public class NumberUtilsTest {

	@Test
	public void testRomanNumerals() {
		assertRomanEquals("N", 0);

		assertRomanEquals("I", 1);
		assertRomanEquals("IV", 4);

		assertRomanEquals("VI", 6);

		assertRomanEquals("CCCXLIX", 349);
		assertRomanEquals("CCCLX", 360);

		assertRomanEquals("CDXC", 490);

		assertRomanEquals("DIX", 509);

		assertRomanEquals("CMX", 910);
		assertRomanEquals("MIV", 1004);

		assertRomanEquals("-IIII", -4, true);
		assertRomanEquals("IIII", 4, true);
	}

	private static void assertRomanEquals(String exp, long res) {
		assertRomanEquals(exp, res, false);
	}

	private static void assertRomanEquals(String exp, long res, boolean classic) {
		assertEquals(exp, NumberUtils.toRoman(res, classic));
	}
}
