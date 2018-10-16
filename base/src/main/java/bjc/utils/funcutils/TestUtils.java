package bjc.utils.funcutils;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

/**
 * Utilities for testing.
 * 
 * @author bjculkin
 *
 */
public class TestUtils {
	/**
	 * Assert an iterator provides a particular sequence of values.
	 * 
	 * @param src
	 *                The iterator to pull values from.
	 * @param vals
	 *                The values to expect from the iterator.
	 */
	public static <T> void assertIteratorEquals(Iterator<T> src, T... vals) {
		for (T val : vals) {
			assertEquals(val, src.next());
		}
	}

	/**
	 * Assert an iterator provides a particular sequence of values.
	 * 
	 * @param src
	 *                The iterator to pull values from.
	 * @param hasMore
	 *                The expected value of hasNext for the iterator.
	 * @param vals
	 *                The values to expect from the iterator.
	 */
	public static <T> void assertIteratorEquals(Iterator<T> src, boolean hasMore, T... vals) {
		assertIteratorEquals(src, vals);
		
		assertEquals(hasMore, src.hasNext());
	}
}
