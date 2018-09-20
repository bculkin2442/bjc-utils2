package bjc.utils.ioutils.format.directives;

import java.io.IOException;

/**
 * Implements directives that create a literal string.
 * 
 * @author student
 *
 */
public class LiteralDirective implements Directive {

	private String directive;
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
		this.directive = Character.toString(directive);
		this.lit = lit;
	}

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		int nTimes = 1;

		if (dirParams.arrParams.length() >= 1) {
			dirParams.arrParams.mapIndices("count");

			nTimes = dirParams.arrParams.getInt("count", "occurance count", directive, 1);
		}

		for (int i = 0; i < nTimes; i++) {
			dirParams.rw.write(lit);
		}

	}

}
