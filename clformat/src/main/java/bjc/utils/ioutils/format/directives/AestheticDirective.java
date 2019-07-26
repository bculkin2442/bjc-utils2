package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the A directive.
 * 
 * @author student
 *
 */
public class AestheticDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Tape<Object> itemTape = dirParams.tParams;

		// Check that we have an item
		CLFormatter.checkItem(dirParams.item, 'A');

		// Parameter values
		int mincol = 0;
		int colinc = 1;
		int minpad = 0;

		char padchar = ' ';

		CLParameters params = dirParams.getParams();
		CLModifiers mods = dirParams.getMods();

		// We take 0, 1 or 4 parameters
		switch (params.length()) {
		case 0:	
			// Zero parameters, use all defaults
			break;
		case 1:
			params.mapIndices("mincol");

			mincol = params.getInt(itemTape, "mincol", "minimum column count", "A", 0);
			break;
		case 4:
			params.mapIndices("mincol", "colinc", "minpad", "padchar");

			mincol = params.getInt(itemTape, "mincol", "minimum column count", "A", 0);
			colinc = params.getInt(itemTape, "colinc", "padding increment", "A", 1);
			minpad = params.getInt(itemTape, "minpad", "minimum amount of padding", "A", 0);

			padchar = params.getChar(itemTape, "padchar", "padding character", "A", ' ');
			break;
		default:
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		}

		String tmp = dirParams.item.toString();

		StringBuilder work = new StringBuilder();

		if (mods.atMod) {
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

		if (!mods.atMod) {
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
