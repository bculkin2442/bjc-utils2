package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.math.NumberUtils;

import java.io.IOException;
import java.util.IllegalFormatConversionException;

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

		if (dirParams.arrParams.length() == 0) {
			if (dirParams.mods.atMod) {
				dirParams.rw.write(NumberUtils.toRoman(val, dirParams.mods.colonMod));
			} else if (dirParams.mods.colonMod) {
				dirParams.rw.write(NumberUtils.toOrdinal(val));
			} else {
				dirParams.rw.write(NumberUtils.toCardinal(val));
			}
		} else {
			if (dirParams.arrParams.length() < 1)
				throw new IllegalArgumentException("R directive requires at least one parameter, the radix");

			int radix = dirParams.arrParams.getInt(0, "radix", 'R');

			handleNumberDirective(dirParams.rw, dirParams.mods, dirParams.arrParams, 0, val, radix);
		}

		dirParams.tParams.right();
	}
}
