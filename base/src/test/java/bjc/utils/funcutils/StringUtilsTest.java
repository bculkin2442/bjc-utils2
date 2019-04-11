package bjc.utils.funcutils;

import java.io.StringReader;

import java.util.Scanner;

import org.junit.Test;

import static bjc.utils.funcutils.StringUtils.readLines;
import static bjc.utils.funcutils.TestUtils.assertIteratorEquals;

import static org.junit.Assert.assertEquals;

/**
 * Tests of stuff in StringUtils.
 *
 * @author Ben Culkin
 */
public class StringUtilsTest {
	@Test
	public void testReadLines() {
		assertReadLines("", "");

		assertReadLines("hallo there", "hallo there");

		assertReadLines("hallo there\na second line", "hallo there", "a second line");

		assertReadLines("hallo there \\\na continued line", "hallo there a continued line");

		assertReadLines("hallo there\\\\\na second line", "hallo there\\", "a second line");
	
		assertReadLines("a\n\nb", "a", "", "b");
	}

	private static void assertReadLinesOpts(String inp, String... outp) {
		StringReader sr = new StringReader(inp);
		Scanner scn = new Scanner(sr);
		assertIteratorEquals(readLines(scn), outp);
	}

	private static void assertReadLines(String inp, String... outp) {
		StringReader sr = new StringReader(inp);
		Scanner scn = new Scanner(sr);
		assertIteratorEquals(readLines(scn), outp);
	}
}
