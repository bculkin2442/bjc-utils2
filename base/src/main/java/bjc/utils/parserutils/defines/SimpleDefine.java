package bjc.utils.parserutils.defines;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDefine implements UnaryOperator<String> {
	private Pattern	patt;
	private String	repl;

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
