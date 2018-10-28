package bjc.utils.ioutils.format.directives;

import java.io.IOException;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.math.NumberUtils;

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
			params.mapIndex("mincol", argidx + 1);
			mincol = params.getInt("mincol", "minimum column count", "R", 0);
		}

		if (params.length() >= (argidx + 3)) {
			params.mapIndex("padchar", argidx + 2);
			padchar = params.getChar("padchar", "padding character", "R", ' ');
		}

		String res;

		if (mods.colonMod) {
			/*
			 * We're doing commas, so check if the two comma-related parameters were
			 * supplied.
			 */
			int commaInterval = 0;
			char commaChar = ',';

			// System.err.printf("Comma params (idx %d, len %d): char \"%s\", int \"%s\"\n", argidx, params.length(), params.getRaw(argidx + 3), params.getRaw(argidx + 4));

			if (params.length() >= (argidx + 4)) {
				params.mapIndex("cchar", argidx + 3);
				commaChar = params.getChar("cchar", "comma character", "R", ',');
			}
			if (params.length() >= (argidx + 5)) {
				params.mapIndex("cinterval", argidx + 4);
				commaInterval = params.getInt("cinterval", "comma interval", "R", 0);
			}

			res = NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, mods.atMod, radix);
		} else {
			res = NumberUtils.toNormalString(val, mincol, padchar, mods.atMod, radix);
		}

		rw.write(res);
	}
}
