package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;

import bjc.utils.ioutils.format.*;
import bjc.utils.math.*;

/**
 * Generalized radix directive.
 * 
 * @author student
 *
 */
public class RadixDirective extends GeneralNumberDirective {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
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
}
