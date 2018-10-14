package bjc.utils.data;

import static org.junit.Assert.*;
import static bjc.utils.funcutils.TestUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test for circular iterators.,
 * 
 * @author bjculkin
 *
 */
public class CircularIteratorTest {

	/**
	 * Test regular repetition of the entire iterator.
	 */
	@Test
	public void testRegular() {
		List<String> lst = Arrays.asList("a", "b", "c");

		CircularIterator<String> itr = new CircularIterator<>(lst);

		// Check we get initial values correctly, and have more remaining
		assertIteratorEquals(itr, true, "a", "b", "c");

		// Check we repeat correctly, and can still repeat
		assertIteratorEquals(itr, true, "a", "b", "c");
	}

	/**
	 * Test that the last element repeats correctly.
	 */
	@Test
	public void testRepLast() {
		List<String> lst = Arrays.asList("a", "b", "c");

		CircularIterator<String> itr = new CircularIterator<>(lst, false);

		// Check we get initial values correctly, and have more remaining
		assertIteratorEquals(itr, true, "a", "b", "c");

		// Check we repeat correctly, and can still repeat
		assertIteratorEquals(itr, true, "c", "c", "c");
	}
}
