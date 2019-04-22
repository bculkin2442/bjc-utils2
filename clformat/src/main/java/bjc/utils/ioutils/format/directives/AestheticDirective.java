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
		// Check that we have an item
		CLFormatter.checkItem(dirParams.item, 'A');

		// Parameter values
		int mincol = 0;
		int colinc = 1;
		int minpad = 0;

		char padchar = ' ';

		CLParameters params = dirParams.arrParams;

		// We take 0, 1 or 4 parameters
		switch (params.length()) {
		case 0:	
			// Zero parameters, use all defaults
			break;
		case 1:
			params.mapIndices("mincol");

			mincol = params.getInt("mincol", "minimum column count", "A", 0);
			break;
		case 4:
			params.mapIndices("mincol", "colinc", "minpad", "padchar");

			mincol = params.getInt("mincol", "minimum column count", "A", 0);
			colinc = params.getInt("colinc", "padding increment", "A", 1);
			minpad = params.getInt("minpad", "minimum amount of padding", "A", 0);

			padchar = params.getChar("padchar", "padding character", "A", ' ');
			break;
		default:
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		}

		String tmp = dirParams.item.toString();

		StringBuilder work = new StringBuilder();

		if (dirParams.mods.atMod) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length() + tmp.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		work.append(tmp);

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
