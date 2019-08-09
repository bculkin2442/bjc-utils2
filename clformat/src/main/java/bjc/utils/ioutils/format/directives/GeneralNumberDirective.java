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
	public static class NumberParams {
		/**
		 * Minimum # of printed columns
		 */
		public CLValue mincol  = CLValue.nil();
		/**
		 * Character to use for padding if needed.
		 */
		public CLValue padchar = CLValue.nil();

		/**
		 * Should the sign always be printed?
		 */
		public boolean signed;

		/**
		 * Should there be commas inserted into the numbers?
		 */
		public boolean commaMode;

		/**
		 * Number of places to go before inserting a comma.
		 */
		public CLValue commaInterval = CLValue.nil();
		/**
		 * Character to use as a comma.
		 */
		public CLValue commaChar     = CLValue.nil();
	}

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

	protected NumberParams getParams(CompileContext compCTX, int argidx) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods    = compCTX.decr.modifiers;

		NumberParams np = new NumberParams();

		if (params.length() >= (argidx + 2)) {
			params.mapIndex("mincol", argidx + 1);
			np.mincol = params.resolveKey("mincol");
		}

		if (params.length() >= (argidx + 3)) {
			params.mapIndex("padchar", argidx + 2);
			np.padchar = params.resolveKey("padchar");
		}

		if (mods.colonMod) {
			np.commaMode = true;

			if (params.length() >= (argidx + 4)) {
				params.mapIndex("cchar", argidx + 3);
				np.commaChar = params.resolveKey("cchar");
			}

			if (params.length() >= (argidx + 5)) {
				params.mapIndex("cinterval", argidx + 4);
				np.commaInterval = params.resolveKey("cinterval");
			}
		}

		np.signed = mods.atMod;

		return np;
	}
}
