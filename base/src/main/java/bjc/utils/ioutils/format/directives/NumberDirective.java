package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.ioutils.ReportWriter;

/**
 * Implements radix based numbers.
 * 
 * @author student
 *
 */
public class NumberDirective extends GeneralNumberDirective {

	/**
	 * Create a new radix based number directive.
	 * 
	 * @param argidx
	 *            The argument offset to use.
	 * @param radix
	 *            The radix of the number to use.
	 */
	public NumberDirective(int argidx, int radix, char directive) {
		this.argidx = argidx;
		this.radix = radix;

		this.directive = directive;
	}

	private int argidx;
	private int radix;

	private char directive;

	@Override
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		CLFormatter.checkItem(item, directive);

		if (!(item instanceof Number)) {
			throw new IllegalFormatConversionException(directive, item.getClass());
		}

		long val = ((Number) item).longValue();

		handleNumberDirective(rw, mods, params, argidx, val, radix);

		tParams.right();
	}

}
