package bjc.utils.test.parserutils.defines;

import static org.junit.Assert.*;

import org.junit.Test;

import bjc.utils.parserutils.defines.IteratedDefine;

/**
 * Test {@link IteratedDefine}
 * @author Ben Culkin
 *
 */
public class IteratedDefineTest {

	/**
	 * Do testing of iterated define.
	 */
	@Test
	public void testSingle() {
		IteratedDefine itrd = new IteratedDefine("a", false, "b");
		
		assertEquals("c", itrd.apply("c"));
		assertEquals("b", itrd.apply("a"));
		assertEquals("bb", itrd.apply("aa"));
	}

	/**
	 * Test iterated define with multiple patterns.
	 */
	@Test
	public void testMultiple() {
		IteratedDefine itrd = new IteratedDefine("a", false, "b", "c");

		assertEquals("d", itrd.apply("d"));
		assertEquals("b", itrd.apply("a"));
		assertEquals("bc", itrd.apply("aa"));
		assertEquals("bcc", itrd.apply("aaa"));
	}
	
	/**
	 * Test iterated define with circular patterns.
	 */
	@Test
	public void testCircular() {
		IteratedDefine itrd = new IteratedDefine("a", true, "b", "c");

		assertEquals("d", itrd.apply("d"));
		assertEquals("b", itrd.apply("a"));
		assertEquals("bc", itrd.apply("aa"));
		assertEquals("bcb", itrd.apply("aaa"));
		assertEquals("bcbcb", itrd.apply("aaaaa"));
	}
}
