package bjc.utils.test.parserutils;

import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static bjc.utils.parserutils.TokenUtils.*;

public class TokenUtilsTest_removeDQuoted {
	@Rule
	public ExpectedException exp;

	public TokenUtilsTest_removeDQuoted(ExpectedException exp) {
		this.exp = exp;
	}

	/*
	 * Check handling of mismatched strings with no matching strings.
	 */
	@Test
	public void testRemoveDQuoted_MismatchedStringNoMatch() throws IllegalArgumentException {
		exp.expect(IllegalArgumentException.class);
		exp.expectMessage(containsString("Opening quote was at position 0"));

		removeDQuotedStrings("\"hello");
	}

	/*
	 * Check handling of mismatched strings with a matching string.
	 */
	@Test
	public void testRemoveDQuoted_MismatchedStringMatch() throws IllegalArgumentException {
		exp.expect(IllegalArgumentException.class);
		exp.expectMessage(containsString("Opening quote was at position 7"));

		removeDQuotedStrings("\"hello\"\"");
	}

	/*
	 * Check handling of strings with a single embedded string.
	 */
	@Test
	public void testRemoveDQuoted_SingleString() {
		List<String> onSingleMatchString = removeDQuotedStrings("hello\"there\"");

		assertThat(onSingleMatchString, hasItems("hello", "\"there\""));
	}

	/*
	 * Check handling of strings without embedded strings.
	 */
	@Test
	public void testRemoveDQuote_NoString() {
		List<String> onNonmatchingString = removeDQuotedStrings("hello");

		assertThat(onNonmatchingString, hasItems("hello"));
	}

	/*
	 * Check handling of empty strings.
	 */
	@Test
	public void testRemoveDQuote_EmptyString() {
		List<String> onEmptyString = removeDQuotedStrings("");

		assertThat(onEmptyString, hasItems(""));
	}
}