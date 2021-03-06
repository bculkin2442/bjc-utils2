package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.*;

/**
 * Implementation skeleton for number directives.
 *
 * @author student
 *
 */
public abstract class GeneralNumberDirective implements Directive {
	/**
	 * Parameters for doing number formatting.
	 *
	 * @author bjculkin
	 *
	 */
	public static class NumberParams {
		/**
		 * Minimum # of printed columns
		 */
		public CLValue mincol = CLValue.nil();
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
		public CLValue commaChar = CLValue.nil();
	}

	/**
	 * Get the parameters for a general number-handling directive.
	 * 
	 * @param compCTX
	 *                The compilation context at this point.
	 * @param argidx
	 *                The argument index to start looking at.
	 * 
	 * @return The parsed number parameters.
	 */
	protected NumberParams getParams(CompileContext compCTX, int argidx) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers  mods   = compCTX.decr.modifiers;

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
