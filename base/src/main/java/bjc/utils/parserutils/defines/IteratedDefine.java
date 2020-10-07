package bjc.utils.parserutils.defines;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.data.CircularIterator;
import bjc.data.ResettableIterator;

/**
 * An iterated find/replace, using a circular assortment of replacements.
 *
 * @author Ben Culkin
 */
public class IteratedDefine implements UnaryOperator<String> {
	private Pattern patt;

	private ResettableIterator<String> repls;

	/**
	 * Create a new iterated define.
	 *
	 * @param pattern
	 *                  The pattern to use for matching.
	 * @param circular
	 *                  Whether or not to loop through the list of replacers, or
	 *                  just repeat the last one.
	 * @param replacers
	 *                  The set of replacement strings to use.
	 */
	public IteratedDefine(Pattern pattern, boolean circular, String... replacers) {
		patt = pattern;

		Iterator<String> tmp = new CircularIterator<>(Arrays.asList(replacers), circular);
		repls = new ResettableIterator<>(tmp);
	}

	/**
	 * Create a new iterated define.
	 *
	 * @param pattern
	 *                  The pattern to use for matching.
	 * @param circular
	 *                  Whether or not to loop through the list of replacers, or
	 *                  just repeat the last one.
	 * @param replacers
	 *                  The set of replacement strings to use.
	 */
	public IteratedDefine(String pattern, boolean circular, String... replacers) {
		this(Pattern.compile(pattern), circular, replacers);
	}

	@Override
	public String apply(String ln) {
		/*
		 * NOTE Oct 6 2020 - Ben Culkin - Should this be configurable to do/not do?
		 */

		/*
		 * Reset the iterator. This means that the fact that you iterated over a
		 * replacer previously, doesn't keep it from being used again.
		 */
		repls.reset();

		Matcher mat = patt.matcher(ln);
		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			/*
			 * @NOTE Oct 6, 2020 - Ben Culkin
			 * 
			 * Policy question here. Should we throw an exception if we exhaust our
			 * replacers and we weren't supposed to?
			 * 
			 * Other alternatives are:
			 * 
			 * a) Default to the empty string
			 * 
			 * b) Keep the last valid replacer. This seems to be what we do as of now, per
			 * the behavior of CircularIterator.
			 * 
			 * c) Use the replacer "$0", which is the same as not doing a replace at all
			 */
			String repl = repls.next();

			mat.appendReplacement(sb, repl);
		}

		mat.appendTail(sb);

		return sb.toString();
	}
}
