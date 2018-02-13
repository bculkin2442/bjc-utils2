package bjc.utils.ioutils.format;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;

class NumberDirective extends GeneralNumberDirective {

	public NumberDirective(int argidx, int radix) {
		this.argidx = argidx;
		this.radix = radix;
	}

	private int	argidx;
	private int	radix;

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(item, 'B');

		if(!(item instanceof Number)) {
			throw new IllegalFormatConversionException('B', item.getClass());
		}

		long val = ((Number) item).longValue();

		handleNumberDirective(sb, mods, params, argidx, val, radix);

		tParams.right();
	}

}