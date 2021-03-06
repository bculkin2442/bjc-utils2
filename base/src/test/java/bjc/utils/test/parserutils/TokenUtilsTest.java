package bjc.utils.test.parserutils;

import static bjc.utils.parserutils.TokenUtils.descapeString;
import static bjc.utils.parserutils.TokenUtils.removeDQuotedStrings;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/*
 * Tests for TokenUtils
 */
@SuppressWarnings("javadoc")
public class TokenUtilsTest {
	@Rule
	public ExpectedException exp = ExpectedException.none();

	/*
	 * Test removeDQuoted
	 */

	/*
	 * Check handling of mismatched strings with no matching strings.
	 */
	@Test
	public void testRemoveDQuoted_MismatchedStringNoMatch()
			throws IllegalArgumentException {
		exp.expect(IllegalArgumentException.class);
		exp.expectMessage(containsString("Opening quote was at position 0"));

		removeDQuotedStrings("\"hello");
	}

	/*
	 * Check handling of mismatched strings with a matching string.
	 */
	@Test
	public void testRemoveDQuoted_MismatchedStringMatch()
			throws IllegalArgumentException {
		exp.expect(IllegalArgumentException.class);
		exp.expectMessage(containsString("Opening quote was at position 7"));

		removeDQuotedStrings("\"hello\"\"");
	}

	/*
	 * Check handling of strings with a single embedded string.
	 */
	@Test
	public void testRemoveDQuoted_SingleString() {
		final List<String> onSingleMatchString = removeDQuotedStrings("hello\"there\"");

		assertThat(onSingleMatchString, hasItems("hello", "\"there\""));
	}

	/*
	 * Check handling of strings with multiple quoted strings in a row.
	 */
	@Test
	public void testRemoveDQuoted_MultipleSerialString() {
		final List<String> onMultipleSerialMatchString
				= removeDQuotedStrings("\"hello\"\"there\"");

		assertThat(onMultipleSerialMatchString, hasItems("\"hello\"", "\"there\""));
	}

	/*
	 * Check handling of strings with multiple interleaved strings.
	 */
	@Test
	public void testRemoveDQuoted_MultipleInterleavedString() {
		final List<String> onMultipleInterleaveMatchString
				= removeDQuotedStrings("one\"two\"three\"four\"");

		assertThat(onMultipleInterleaveMatchString,
				hasItems("one", "\"two\"", "three", "\"four\""));
	}

	/*
	 * Check handling of strings without embedded strings.
	 */
	@Test
	public void testRemoveDQuote_NoString() {
		final List<String> onNonmatchingString = removeDQuotedStrings("hello");

		assertThat(onNonmatchingString, hasItems("hello"));
	}

	/*
	 * Check handling of empty strings.
	 */
	@Test
	public void testRemoveDQuote_EmptyString() {
		final List<String> onEmptyString = removeDQuotedStrings("");

		assertThat(onEmptyString, hasItems(""));
	}

	/*
	 * Test descapeString
	 */
	/*
	 * Check handling of empty strings.
	 */
	@Test
	public void testDescapeString_EmptyString() {
		final String onEmptyString = descapeString("");

		assertThat(onEmptyString, is(""));
	}

	/*
	 * Check handling of strings without escapes
	 */
	@Test
	public void testDescapeString_NonescapeString() {
		final String onNonescapeString = descapeString("hello there");

		assertThat(onNonescapeString, is("hello there"));
	}

	/*
	 * Check handling of strings with single escapes.
	 */
	@Test
	public void testDescapeString_SingleEscapeString() {
		final String onSingleEscapeString = descapeString("hello\\tthere");

		assertThat(onSingleEscapeString, is("hello\tthere"));
	}

	/*
	 * Check handling of strings with multiple escapes.
	 */
	@Test
	public void testDescapeString_MultipleEscapeString() {
		final String onMultipleEscapeString = descapeString("hello\\tthere\\tworld");

		assertThat(onMultipleEscapeString, is("hello\tthere\tworld"));
	}

	/*
	 * Check handling of strings with invalid single escapes.
	 */
	@Test
	public void testDescapeString_InvalidSingleEscapeString()
			throws IllegalArgumentException {
		exp.expect(IllegalArgumentException.class);
		exp.expectMessage(containsString("at position 0"));

		descapeString("\\x");
	}
}