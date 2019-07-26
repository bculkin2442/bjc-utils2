package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;
import bjc.utils.math.*;

/**
 * Implementation skeleton for number directives.
 * 
 * @author student
 *
 */
public abstract class GeneralNumberDirective implements Directive {
	protected static void handleNumberDirective(Tape<Object> itemTape, ReportWriter rw, Decree decr, int argidx,
			long val, int radix) throws IOException {

		CLParameters params = decr.parameters;
		CLModifiers mods = decr.modifiers;

		/*
		 * Initialize the two padding related parameters, and then fill them in from the
		 * directive parameters if they are present.
		 */
		int mincol = 0;
		char padchar = ' ';
		if (params.length() >= (argidx + 2)) {
			params.mapIndex("mincol", argidx + 1);
			mincol = params.getInt(itemTape, "mincol", "minimum column count", "R", 0);
		}

		if (params.length() >= (argidx + 3)) {
			params.mapIndex("padchar", argidx + 2);
			padchar = params.getChar(itemTape, "padchar", "padding character", "R", ' ');
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
				params.mapIndex("cchar", argidx + 3);
				commaChar = params.getChar(itemTape, "cchar", "comma character", "R", ',');
			}
			if (params.length() >= (argidx + 5)) {
				params.mapIndex("cinterval", argidx + 4);
				commaInterval = params.getInt(itemTape, "cinterval", "comma interval", "R", 0);
			}

			res = NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, mods.atMod, radix);
		} else {
			res = NumberUtils.toNormalString(val, mincol, padchar, mods.atMod, radix);
		}

		rw.write(res);
	}
}
