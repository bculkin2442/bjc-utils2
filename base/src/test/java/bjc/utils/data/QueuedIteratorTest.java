package bjc.utils.data;

import static bjc.utils.data.QueuedIterator.queued;
import static bjc.utils.funcutils.TestUtils.assertIteratorEquals;
import static java.util.Arrays.asList;

import org.junit.Test;

/**
 * Test of QueuedIterator.
 * 
 * @author bjculkin
 *
 */
public class QueuedIteratorTest {

	/**
	 * Test of functionality.
	 */
	@Test
	public void test() {
		assertIteratorEquals(false, queued());

		assertIteratorEquals(false, queued(asList(1, 2, 3)), 1, 2, 3);
		assertIteratorEquals(false, queued(asList(1, 2, 3), asList(3, 2, 1)), 1, 2, 3, 3, 2, 1);

	}

	/**
	 * Test of before() method.
	 */
	@Test
	public void testBefore() {
		QueuedIterator<Integer> itr = queued(asList(1, 2, 3));

		assertIteratorEquals(true, itr, 1, 2);

		itr.before(asList(1, 2, 3));

		assertIteratorEquals(false, itr, 1, 2, 3, 3);
	}

	/**
	 * Test of after() method.
	 */
	@Test
	public void testAfter() {
		QueuedIterator<Integer> itr = queued(asList(1, 2, 3));

		assertIteratorEquals(true, itr, 1, 2);

		itr.after(asList(1, 2, 3));

		assertIteratorEquals(false, itr, 3, 1, 2, 3);
	}
	
	/**
	 * Test of last() method.
	 */
	@Test
	public void testLast() {
		QueuedIterator<Integer> itr = queued(asList(1, 2, 3));

		assertIteratorEquals(true, itr, 1, 2);

		itr.after(asList(4));
		itr.last(asList(1, 2, 3));

		assertIteratorEquals(false, itr, 3, 4, 1, 2, 3);
	}
}
