package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;

import bjc.utils.ioutils.format.*;
import bjc.utils.math.*;

import static bjc.utils.ioutils.format.directives.GeneralNumberDirective.NumberParams;

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
	 *                  The argument offset to use.
	 * @param radix
	 *                  The radix of the number to use.
	 * @param directive
	 *                  The character that marks this directive.
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
	public Edict compile(CompileContext compCTX) {
		NumberParams np = getParams(compCTX, argidx);

		return new NumberEdict(radix, directive, argidx, np);
	}
}

class NumberEdict implements Edict {
	private int radix;
	private String directive;

	private NumberParams np;

	public NumberEdict(int radix, char directive, int argidx, NumberParams np) {
		this.radix = radix;
		this.directive = Character.toString(directive);

		this.np = np;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Object item = formCTX.items.item();

		CLFormatter.checkItem(item, directive.charAt(0));

		if (!(item instanceof Number)) {
			throw new IllegalFormatConversionException(directive.charAt(0),
					item.getClass());
		}

		long val = ((Number) item).longValue();

		int  mincol  = np.mincol.asInt(formCTX.items, "minimum column count", directive, 0);
		char padchar = np.padchar.asChar(formCTX.items, "padding character", directive, ' ');

		boolean signed = np.signed;

		String res;

		if (np.commaMode) {
			char commaChar = np.commaChar.asChar(formCTX.items, "comma character",
					directive, ',');
			int commaInterval = np.commaInterval.asInt(formCTX.items, "comma interval",
					directive, 0);

			res = NumberUtils.toCommaString(val, mincol, padchar, commaInterval,
					commaChar, signed, radix);
		} else {
			res = NumberUtils.toNormalString(val, mincol, padchar, signed, radix);
		}

		formCTX.writer.write(res);

		formCTX.items.right();
	}
}
