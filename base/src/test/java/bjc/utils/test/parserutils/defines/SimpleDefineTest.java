package bjc.utils.test.parserutils.defines;

import static org.junit.Assert.*;

import org.junit.Test;

import bjc.utils.parserutils.defines.SimpleDefine;

/**
 * Test of SimpleDefine.
 * @author Ben Culkin
 *
 */
public class SimpleDefineTest {

	/**
	 * Test literal patterns.
	 */
	@Test
	public void testLiteralPatterns() {
		SimpleDefine sd = new SimpleDefine("a b", "b a");
		
		assertEquals("a a a a", sd.apply("a a a a"));
		assertEquals("b a", sd.apply("a b"));
		assertEquals("a a b a", sd.apply("a a a b"));
		assertEquals("b a b a", sd.apply("a b a b"));
	}
	
	/**
	 * Test regex patterns.
	 */
	@Test
	public void testRegexPatterns() {
		SimpleDefine sd2 = new SimpleDefine("a+", "b");
		
		assertEquals("c", sd2.apply("c"));
		assertEquals("b", sd2.apply("a"));
		assertEquals("b", sd2.apply("aaa"));
		assertEquals("bb", sd2.apply("aaab"));
	}

}
