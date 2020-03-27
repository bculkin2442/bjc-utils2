package bjc.utils.test.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bjc.utils.data.BooleanToggle;

/**
 * Test for boolean toggles.
 * @author bjculkin
 *
 */
public class BooleanToggleTest {

	/**
	 * Test that boolean toggles work right.
	 */
	@Test
	public void test() {
		BooleanToggle tog = new BooleanToggle();
		
		// Check initial value is false.
		assertEquals(false, tog.peek());
		// Check that 'get' returns the old value
		assertEquals(false, tog.get());
		// Check that 'get' swaps the value
		assertEquals(true, tog.peek());
		// Check that we can round-trip back.
		assertEquals(true, tog.get());
		assertEquals(false, tog.peek());
		
		tog.set(true);
		
		// Check set works
		assertEquals(true, tog.peek());
	}
}
