package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.math.NumberUtils;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

public class RadixDirective extends GeneralNumberDirective {

	@Override
	public void format(StringBuffer buff, Object arg, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(arg, 'R');

		if(!(arg instanceof Number)) {
			throw new IllegalFormatConversionException('R', arg.getClass());
		}

		/*
		 * @TODO see if this is the way we want to do this.
		 */
		long val = ((Number) arg).longValue();

		if(params.length() == 0) {
			if(mods.atMod) {
				buff.append(NumberUtils.toRoman(val, mods.colonMod));
			} else if(mods.colonMod) {
				buff.append(NumberUtils.toOrdinal(val));
			} else {
				buff.append(NumberUtils.toCardinal(val));
			}
		} else {
			if(params.length() < 1) throw new IllegalArgumentException(
					"R directive requires at least one parameter, the radix");

			int radix = params.getInt(0, "radix", 'R');

			handleNumberDirective(buff, mods, params, 0, val, radix);
		}

		tParams.right();
	}
}
