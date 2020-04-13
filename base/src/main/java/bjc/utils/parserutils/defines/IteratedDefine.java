package bjc.utils.parserutils.defines;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.data.CircularIterator;

/**
 * An iterated find/replace, using a circular assortment of replacements.
 *
 * @author Ben Culkin
 */
public class IteratedDefine implements UnaryOperator<String> {
	private Pattern patt;

	private Iterator<String> repls;

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

		repls = new CircularIterator<>(Arrays.asList(replacers), circular);
	}

	@Override
	public String apply(String ln) {
		Matcher mat = patt.matcher(ln);
		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			String repl = repls.next();

			mat.appendReplacement(sb, repl);
		}

		mat.appendTail(sb);

		return sb.toString();
	}
}
