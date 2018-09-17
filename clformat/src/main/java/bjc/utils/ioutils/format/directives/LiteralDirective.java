package bjc.utils.ioutils.format.directives;

import java.io.IOException;

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
	public void format(FormatParameters dirParams) throws IOException {
		int nTimes = 1;

		if (dirParams.arrParams.length() >= 1) {
			nTimes = dirParams.arrParams.getInt(0, "occurance count", directive);
		}

		for (int i = 0; i < nTimes; i++) {
			dirParams.rw.write(lit);
		}

	}

}
