package bjc.utils.ioutils.format.directives;

import java.io.IOException;

import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLParameters;

/**
 * Implementation of the A directive.
 * 
 * @author student
 *
 */
public class AestheticDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		CLFormatter.checkItem(dirParams.item, 'A');

		int mincol = 0, colinc = 1, minpad = 0;
		char padchar = ' ';

		CLParameters params = dirParams.arrParams;
		if (params.length() == 0) {
			// Zero parameters, use all defaults
		} else if (params.length() == 1) {
			mincol = params.getIntDefault(0, "minimum column count", "A", 0);
		} else if (params.length() < 4) {
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		} else {
			colinc = params.getIntDefault(1, "padding increment", "A", 1);
			minpad = params.getIntDefault(2, "minimum amount of padding", "A", 0);
			padchar = params.getCharDefault(3, "padding character", "A", ' ');
		}

		StringBuilder work = new StringBuilder();

		if (dirParams.mods.atMod) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		work.append(dirParams.item.toString());

		if (!dirParams.mods.atMod) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		dirParams.rw.write(work.toString());

		dirParams.tParams.right();
	}
}
