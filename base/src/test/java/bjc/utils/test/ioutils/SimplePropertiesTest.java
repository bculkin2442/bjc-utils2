package bjc.utils.test.ioutils;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import bjc.utils.ioutils.SimpleProperties;
import bjc.utils.ioutils.SimpleProperties.DuplicateKeys;
import bjc.utils.ioutils.SimpleProperties.InvalidLineFormat;

/**
 * Tests for SimpleProperties.
 *
 * @author Ben Culkin
 *
 */
public class SimplePropertiesTest {

	@Test
	public void testSimpleProperties() {
		SimpleProperties props = new SimpleProperties();

		assertEquals(0, props.size());
		assertTrue(props.isEmpty());
	}

	@Test
	public void testLoadFrom() {
		SimpleProperties props = new SimpleProperties();

		StringReader rdr = new StringReader("a a\nb b\nc c1\nc c2\n#c c3");

		props.loadFrom(rdr, true);

		assertEquals(3, props.size());
		assertEquals("a", props.get("a"));
		assertEquals("b", props.get("b"));
		assertEquals("c2", props.get("c"));
	}

	@Test(expected = DuplicateKeys.class)
	public void testDuplicateKeys() {
		SimpleProperties props = new SimpleProperties();

		StringReader rdr = new StringReader("a a\nb b\nb b");

		props.loadFrom(rdr, false);
	}

	@Test(expected = InvalidLineFormat.class)
	public void testInvalidFormat() {
		SimpleProperties props = new SimpleProperties();

		StringReader rdr = new StringReader("a");

		props.loadFrom(rdr, false);
	}
}