/**
 * 
 */
package bjc.utils.test.parserutils;

import static org.junit.Assert.*;

import bjc.utils.parserutils.TokenUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/**
 * Tests on token utils.
 * 
 * @author EVE
 *
 */
public class TokenUtilsTest {

	/**
	 * Test method for
	 * {@link bjc.utils.parserutils.TokenUtils#removeDQuotedStrings(java.lang.String)}.
	 */
	@Test
	public void testRemoveDQuotedStrings() {
		/*
		 * Check handling of empty strings.
		 */
		List<String> onEmptyString = TokenUtils.removeDQuotedStrings("");
		assertThat(onEmptyString.size(), is(1));
		assertThat(onEmptyString.get(0), is(""));

		/*
		 * Check handling of strings without embedded strings.
		 */
		List<String> onNonmatchingString = TokenUtils.removeDQuotedStrings("hello");
		assertThat(onNonmatchingString.size(), is(1));
		assertThat(onNonmatchingString.get(0), is("hello"));
		
		/*
		 * Check handling of strings with a single embedded string.
		 */
		List<String> onSingleMatchString = TokenUtils.removeDQuotedStrings("hello\"there\"");
		assertThat(onSingleMatchString.size(), is(2));
		assertThat(onSingleMatchString.get(0), is("hello"));
		assertThat(onSingleMatchString.get(1), is("\"there\""));
		
		/*
		 * Check handling a string with mismatched quotes.
		 * 
		 * TODO is this the right behavior, or should we fail instead?
		 */
		List<String> onMismatchString = TokenUtils.removeDQuotedStrings("hello\"there");
		assertThat(onMismatchString.size(), is(1));
		assertThat(onMismatchString.get(0), is("hello\"there"));
	}

	/**
	 * Test method for
	 * {@link bjc.utils.parserutils.TokenUtils#descapeString(java.lang.String)}.
	 */
	@Test
	public void testDescapeString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link bjc.utils.parserutils.TokenUtils#isDouble(java.lang.String)}.
	 */
	@Test
	public void testIsDouble() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link bjc.utils.parserutils.TokenUtils#isInt(java.lang.String)}.
	 */
	@Test
	public void testIsInt() {
		fail("Not yet implemented"); // TODO
	}

}
