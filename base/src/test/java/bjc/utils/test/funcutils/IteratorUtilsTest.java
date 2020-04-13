package bjc.utils.test.funcutils;

import org.junit.Test;

import static bjc.utils.funcutils.IteratorUtils.*;
import static bjc.utils.funcutils.TestUtils.assertIteratorEquals;

import static java.util.Arrays.asList;

/**
 * Test IteratorUtils functionality.
 *
 * @author Ben Culkin
 */
public class IteratorUtilsTest {
	/**
	 * Test the chain() method works correctly.
	 */
	@Test
	public void testChain() {
		assertIteratorEquals(
				chain(I(asList("a b", "b c")), arg -> I(asList(arg.split(" ")))), "a",
				"b", "b", "c");
	}
}
