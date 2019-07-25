package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

public class TabulateDirective implements Directive {
	public void format(FormatParameters dirParams) throws IOException {
		Tape<Object> itemTape = dirParams.tParams;

		// Support for a possible future feature
		char padchar = ' ';

		int currCol = dirParams.rw.getLinePos();
		if (dirParams.mods.colonMod) {
			currCol = dirParams.rw.getIndentPos();
		}


		CLParameters params = dirParams.arrParams;
		if (dirParams.mods.atMod) {
			int colrel = 1, colinc = 1;

			if (params.length() > 2) {
				params.mapIndex("colinc", 1);

				colinc = params.getInt(itemTape, "colinc", "column increment", "T", 1);
			}

			if (params.length() > 1) {
				params.mapIndices("colrel");

				colrel = params.getInt(itemTape, "colrel", "relative column number", "T", 1);
			}

			for (int i = 0; i < colrel; i++) {
				dirParams.rw.write(padchar);
			}

			int nSpaces = 0;

			while ((currCol + nSpaces) % colinc != 0) nSpaces++;

			for (int i = 0; i < nSpaces; i++) {
				dirParams.rw.write(padchar);
			}
		} else {
			int colnum = 1, colinc = 1;

			if (params.length() > 2) {
				params.mapIndex("colinc", 1);

				colinc = params.getInt(itemTape, "colinc", "column increment", "T", 1);
			}

			if (params.length() > 1) {
				params.mapIndices("colnum");

				colnum = params.getInt(itemTape, "colnum", "column number", "T", 1);
			}

			if (currCol < colnum) {
				for (int i = currCol; i < colnum; i++) {
					dirParams.rw.write(padchar);
				}
			} else {
				if (colinc == 0) return;

				int k = 0;

				while (colnum > (currCol + (k * colinc))) k++;

				for (int i = currCol; i < colnum; i++) {
					dirParams.rw.write(padchar);
				}
			}
		}
	}
}
