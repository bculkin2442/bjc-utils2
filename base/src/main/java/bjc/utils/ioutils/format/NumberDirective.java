package bjc.utils.ioutils.format;

import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.NumberUtils;

public class NumberDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher) {
		/*
		 * Initialize the two padding related parameters, and then fill them in from the
		 * directive parameters if they are present.
		 */
		int mincol = 0;
		char padchar = ' ';
		if (params.length() > (argidx + 2)) {
			mincol = params.getIntDefault(argidx + 1, "minimum column count", 'R', 0);
		}
		if (params.length() > (argidx + 3)) {
			padchar = params.getCharDefault(argidx + 2, "padding character", 'R', ' ');
		}

		if (mods.colonMod) {
			/*
			 * We're doing commas, so check if the two comma-related parameters were
			 * supplied.
			 */
			int commaInterval = 0;
			char commaChar = ',';
			if (params.length() > (argidx + 3)) {
				commaChar = params.getCharDefault((argidx + 3), "comma character", 'R', ' ');
			}
			if (params.length() > (argidx + 4)) {
				commaInterval = params.getIntDefault((argidx + 4), "comma interval", 'R', 0);
			}

			NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, mods.atMod, radix);
		} else {
			NumberUtils.toNormalString(val, mincol, padchar, mods.atMod, radix);
		}
	}

}
