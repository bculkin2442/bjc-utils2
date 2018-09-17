package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.math.NumberUtils;

import java.io.IOException;

/**
 * Implementation skeleton for number directives.
 * 
 * @author student
 *
 */
public abstract class GeneralNumberDirective implements Directive {
	protected static void handleNumberDirective(ReportWriter rw, CLModifiers mods, CLParameters params, int argidx,
			long val, int radix) throws IOException {
		/*
		 * Initialize the two padding related parameters, and then fill them in from the
		 * directive parameters if they are present.
		 */
		int mincol = 0;
		char padchar = ' ';
		if (params.length() >= (argidx + 2)) {
			mincol = params.getIntDefault(argidx + 1, "minimum column count", 'R', 0);
		}
		if (params.length() >= (argidx + 3)) {
			padchar = params.getCharDefault(argidx + 2, "padding character", 'R', ' ');
		}

		String res;

		if (mods.colonMod) {
			/*
			 * We're doing commas, so check if the two comma-related parameters were
			 * supplied.
			 */
			int commaInterval = 0;
			char commaChar = ',';
			if (params.length() >= (argidx + 4)) {
				commaChar = params.getCharDefault((argidx + 3), "comma character", 'R', ',');
			}
			if (params.length() >= (argidx + 5)) {
				commaInterval = params.getIntDefault((argidx + 4), "comma interval", 'R', 0);
			}

			res = NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, mods.atMod, radix);
		} else {
			res = NumberUtils.toNormalString(val, mincol, padchar, mods.atMod, radix);
		}

		rw.write(res);
	}
}
