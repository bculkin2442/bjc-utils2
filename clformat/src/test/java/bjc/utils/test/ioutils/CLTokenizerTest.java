package bjc.utils.test.ioutils;

import bjc.utils.ioutils.format.*;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for CLTokenizer.
 *
 * @author Ben Culkin
 */
@SuppressWarnings("javadoc")
public class CLTokenizerTest {
	@Test
	public void testEmptyTokenizer() {
		CLTokenizer tokenzer = new CLTokenizer("");

		assertTrue("Empty tokenizer has a decree", tokenzer.hasNext());
		Decree dec = tokenzer.next();
		assertFalse("Empty tokenizer has only one decree", tokenzer.hasNext());

		assertTrue("Decree from empty tokenizer is a literal", dec.isLiteral);
		assertEquals("Decree from empty tokenizer is empty", "", dec.name);
	}
}
