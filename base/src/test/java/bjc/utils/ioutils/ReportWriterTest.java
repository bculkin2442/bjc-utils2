package bjc.utils.ioutils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

/**
 * Tests for ReportWriter.
 * 
 * @author EVE
 *
 */
@SuppressWarnings("javadoc")
public class ReportWriterTest {
	@Test
	public void testWriteString() {
		try (ReportWriter rw = new ReportWriter(new StringWriter())) {
			rw.write("foo");

			assertEquals("foo", rw.toString());
		} catch (IOException ioex) {

		}
	}
}
