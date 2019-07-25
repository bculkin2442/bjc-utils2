package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.IllegalFormatConversionException;

import bjc.utils.ioutils.format.CLFormatter;

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
	public void format(FormatParameters dirParams) throws IOException {
		CLFormatter.checkItem(dirParams.item, directive);

		if (!(dirParams.item instanceof Number)) {
			throw new IllegalFormatConversionException(directive, dirParams.item.getClass());
		}

		long val = ((Number) dirParams.item).longValue();

		handleNumberDirective(dirParams.tParams, dirParams.rw,
				dirParams.mods, dirParams.arrParams, argidx, val, radix);

		dirParams.tParams.right();
	}

}
