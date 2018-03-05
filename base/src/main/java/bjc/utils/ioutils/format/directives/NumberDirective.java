package bjc.utils.ioutils.format.directives;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

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
	public NumberDirective(int argidx, int radix) {
		this.argidx = argidx;
		this.radix = radix;
	}

	private int argidx;
	private int radix;

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(item, 'B');

		if (!(item instanceof Number)) {
			throw new IllegalFormatConversionException('B', item.getClass());
		}

		long val = ((Number) item).longValue();

		handleNumberDirective(sb, mods, params, argidx, val, radix);

		tParams.right();
	}

}
