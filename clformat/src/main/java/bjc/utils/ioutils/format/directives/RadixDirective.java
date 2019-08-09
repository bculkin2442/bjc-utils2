package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;
import bjc.utils.math.*;

import static bjc.utils.ioutils.format.directives.GeneralNumberDirective.NumberParams;

/**
 * Generalized radix directive.
 * 
 * @author student
 *
 */
public class RadixDirective extends GeneralNumberDirective {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	public void formatF(FormatParameters dirParams) throws IOException {
		CLFormatter.checkItem(dirParams.item, 'R');

		if (!(dirParams.item instanceof Number)) {
			throw new IllegalFormatConversionException('R', dirParams.item.getClass());
		}

		/*
		 * @TODO see if this is the way we want to do this.
		 */
		long val = ((Number) dirParams.item).longValue();

		CLParameters params = dirParams.getParams();
		CLModifiers mods = dirParams.getMods();

		if (params.length() == 0) {
			if (mods.atMod) {
				dirParams.rw.write(NumberUtils.toRoman(val,mods.colonMod));
			} else if (mods.colonMod) {
				dirParams.rw.write(NumberUtils.toOrdinal(val));
			} else {
				dirParams.rw.write(NumberUtils.toCardinal(val));
			}
		} else {
			if (params.length() < 1)
				throw new IllegalArgumentException("R directive requires at least one parameter, the radix");

			params.mapIndex("radix", 0);

			int radix = params.getInt(dirParams.tParams, "radix", "radix", "R", 10);

			handleNumberDirective(dirParams.tParams, dirParams.rw,
					dirParams.decr, 0, val, radix);
		}

		dirParams.tParams.right();
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		RadixEdict.Mode mode;

		CLValue radixVal = CLValue.nil();

		NumberParams np = null;

		if (params.length() == 0) {
			if (mods.atMod) {
				mode = RadixEdict.Mode.ROMAN;
			} else if (mods.colonMod) {
				mode = RadixEdict.Mode.ORDINAL;
			} else {
				mode = RadixEdict.Mode.CARDINAL;
			}
		} else {
			mode = RadixEdict.Mode.NORMAL;

			if (params.length() < 1)
				throw new IllegalArgumentException("R directive requires at least one parameter, the radix");

			params.mapIndex("radix", 0);
			radixVal = params.resolveKey("radix");

			np = getParams(compCTX, 0);
		}

		return new RadixEdict(mode, radixVal, np, mods.colonMod);
	}
}

class RadixEdict implements Edict {
	public static enum Mode {
		NORMAL,
		ROMAN,
		ORDINAL,
		CARDINAL
	}

	private Mode mode;

	private CLValue radixVal;

	private NumberParams np;

	private boolean isClassic;

	public RadixEdict(Mode mode, CLValue radix, NumberParams np, boolean isClassic) {
		this.mode = mode;

		this.radixVal = radix;

		this.np = np;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Object item = formCTX.items.item();

		CLFormatter.checkItem(item, 'R');

		if (!(item instanceof Number)) {
			throw new IllegalFormatConversionException('R', item.getClass());
		}

		long val = ((Number) item).longValue();

		String res;
		switch (mode) {
		case ROMAN:
			res = NumberUtils.toRoman(val, isClassic);
			break;
		case ORDINAL:
			res = NumberUtils.toOrdinal(val);
			break;
		case CARDINAL:
			res = NumberUtils.toCardinal(val);
			break;
		case NORMAL: 
		{
			int radix = radixVal.asInt(formCTX.items, "radix", "R", 10);

			int  mincol  = np.mincol.asInt(formCTX.items, "minimum column count", "R", 0);
			char padchar = np.padchar.asChar(formCTX.items, "padding character", "R", ' ');

			boolean signed = np.signed;

			if (np.commaMode) {
				char commaChar     = np.commaChar.asChar(formCTX.items, "comma character", "R", ',');
				int  commaInterval = np.commaInterval.asInt(formCTX.items, "comma interval", "R", 0);

				res = NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, signed, radix);
			} else {
				res = NumberUtils.toNormalString(val, mincol, padchar, signed, radix);
			}

			break;
		}

		default:
			throw new IllegalArgumentException("Unsupported radix mode " + mode);
		}

		formCTX.writer.write(res);

		formCTX.items.right();
	}
}
