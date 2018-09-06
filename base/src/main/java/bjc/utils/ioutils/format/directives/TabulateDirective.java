package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

public class TabulateDirective implements Directive {
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		// Unsupported feature.
		//
		// I can't really make out what this is supposed to do from the
		// documentation, but I suspect that it depends of font glyph
		// size, not character positions
		if (mods.colonMod) {
			throw new UnsupportedOperationException("Colon mod is not supported for T directive");
		}

		// Support for a possible future feature
		char padchar = ' ';

		if (mods.atMod) {
			int colrel = 1, colinc = 1;

			if (arrParams.length() > 2) {
				colinc = arrParams.getIntDefault(1, "column increment", 'T', 1);
			}

			if (arrParams.length() > 1) {
				colrel = arrParams.getIntDefault(0, "relative column number", 'T', 1);
			}

			for (int i = 0; i < colrel; i++) {
				rw.write(padchar);
			}

			int currCol = rw.getLinePos();

			int nSpaces = 0;

			while ((currCol + nSpaces) % colinc != 0) nSpaces++;

			for (int i = 0; i < nSpaces; i++) {
				rw.write(padchar);
			}
		} else {
			int colnum = 1, colinc = 1;

			if (arrParams.length() > 2) {
				colinc = arrParams.getIntDefault(1, "column increment", 'T', 1);
			}

			if (arrParams.length() > 1) {
				colnum = arrParams.getIntDefault(0, "column number", 'T', 1);
			}

			int currCol = rw.getLinePos();

			if (currCol < colnum) {
				for (int i = currCol; i < colnum; i++) {
					rw.write(padchar);
				}
			} else {
				if (colinc == 0) return;

				int k = 0;

				while (colnum > (currCol + (k * colinc))) k++;

				for (int i = currCol; i < colnum; i++) {
					rw.write(padchar);
				}
			}
		}
	}
}
