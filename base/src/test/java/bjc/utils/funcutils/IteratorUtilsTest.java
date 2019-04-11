package bjc.utils.funcutils;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static bjc.utils.funcutils.IteratorUtils.*;
import static bjc.utils.funcutils.TestUtils.assertIteratorEquals;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

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
		assertIteratorEquals(chain(I(asList("a b", "b c")), (arg) -> I(asList(arg.split(" ")))), "a", "b", "b", "c");
	}
}
