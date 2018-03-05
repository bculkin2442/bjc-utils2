package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

import java.util.regex.Matcher;

/**
 * Implements directives that create a literal string.
 * 
 * @author student
 *
 */
public class LiteralDirective implements Directive {

	private char directive;
	private String lit;

	/**
	 * Create a new literal directive.
	 * 
	 * @param lit
	 *            The string for the directive.
	 * @param directive
	 *            The character for the directive.
	 */
	public LiteralDirective(String lit, char directive) {
		this.directive = directive;
		this.lit = lit;
	}

	@Override
	public void format(StringBuffer buff, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		int nTimes = 1;

		if (params.length() >= 1) {
			nTimes = params.getInt(0, "occurance count", directive);
		}

		for (int i = 0; i < nTimes; i++) {
			buff.append(lit);
		}

	}

}
