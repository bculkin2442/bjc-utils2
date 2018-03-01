package bjc.utils.parserutils.defines;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple implementation of a find/replace operator on a string.
 *
 * @author Ben Culkin
 */
public class SimpleDefine implements UnaryOperator<String> {
	private Pattern patt;
	private String repl;

	/**
	 * Create a new simple define.
	 *
	 * @param pattern
	 *        The pattern to match against.
	 * @param replace
	 *        The text to use as a replacement.
	 */
	public SimpleDefine(Pattern pattern, String replace) {
		patt = pattern;

		repl = replace;
	}

	@Override
	public String apply(String line) {
		Matcher mat = patt.matcher(line);

		return mat.replaceAll(repl);
	}
}
