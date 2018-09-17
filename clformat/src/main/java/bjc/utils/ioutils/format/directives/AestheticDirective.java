package bjc.utils.ioutils.format.directives;

import java.io.IOException;

import bjc.utils.ioutils.format.CLFormatter;

/**
 * Implementation of the A directive.
 * 
 * @author student
 *
 */
public class AestheticDirective implements Directive {

	@Override
	public void format(FormatParameter dirParams) throws IOException {
		// System.err.printf("Aesthetic directive with item \"%s\" and params: %s\n", item, tParams);
		CLFormatter.checkItem(dirParams.item, 'A');

		int mincol = 0, colinc = 1, minpad = 0;
		char padchar = ' ';

		if (dirParams.arrParams.length() == 0) {
			// Zero parameters, use all defaults
		} else if (dirParams.arrParams.length() == 1) {
			mincol = dirParams.arrParams.getIntDefault(0, "minimum column count", 'A', 0);
		} else if (dirParams.arrParams.length() < 4) {
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		} else {
			colinc = dirParams.arrParams.getIntDefault(1, "padding increment", 'A', 1);
			minpad = dirParams.arrParams.getIntDefault(2, "minimum amount of padding", 'A', 0);
			padchar = dirParams.arrParams.getCharDefault(3, "padding character", 'A', ' ');
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
