package bjc.utils.parserutils.defines;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple match-and-replace operation on strings.
 * 
 * @author EVE
 *
 */
public class SimpleDefine implements UnaryOperator<String> {
	private Pattern	patt;
	private String	repl;

	/**
	 * Create a new simple define.
	 * 
	 * @param pattern
	 *        The pattern to look for.
	 * @param replace
	 *        The thing to replace it with.
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
