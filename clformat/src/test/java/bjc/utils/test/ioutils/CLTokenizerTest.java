package bjc.utils.test.ioutils;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import bjc.utils.ioutils.format.CLTokenizer;

// Static imports

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

/**
 * Tests for CL format strings.
 * 
 * @author EVE
 *
 */
@SuppressWarnings("javadoc")
public class CLTokenizerTest {
	@Test
	public void testTokenizer() {
		assertIteratorEquals(tokenize(""), "");
		assertIteratorEquals(tokenize("hello"), "hello");
		assertIteratorEquals(tokenize("hello olleh"), "hello olleh");
		assertIteratorEquals(tokenize("A ~A"), "A ", "~A");

		assertIteratorEquals(tokenize("~3,'0D"), "~3,'0D");
		assertIteratorEquals(tokenize("~,,'|,2:D"), "~,,'|,2:D");
		assertIteratorEquals(tokenize("~3,,,' ,2:R"), "~3,,,' ,2:R");

		assertIteratorEquals(tokenize("~@[print level = ~D~]~@[print length = ~D~]"), "~@[", "print level = ", "~D", "~]", "~@[", "print length = ", "~D", "~]");
	}

	private static Iterator<String> tokenize(String inp) {
		return new CLTokenizer(inp);
	}

	private static <T> void assertIteratorEquals(Iterator<T> src, T... vals) {
		for (T val : vals) {
			assertEquals(val, src.next());
		}
	}
}
