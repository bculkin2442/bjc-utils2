package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.esodata.*;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the T directive, which is used for some formatting based
 * controls.
 *
 * @author bjculkin
 *
 */
public class TabulateDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLValue colinc = CLValue.nil();
		CLValue colid = CLValue.nil();

		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		if (params.length() > 2) {
			params.mapIndex("colinc", 1);

			colinc = params.resolveKey("colinc");
		}

		boolean isRelative = mods.atMod;
		boolean fromIndent = mods.colonMod;

		if (isRelative) {
			if (params.length() > 1) {
				params.mapIndices("colrel");

				colid = params.resolveKey("colrel");
			}
		} else {
			if (params.length() > 1) {
				params.mapIndices("colnum");

				colid = params.resolveKey("colnum");
			}
		}

		return new TabulateEdict(isRelative, colinc, fromIndent, colid);
	}
}

class TabulateEdict implements Edict {
	private boolean isRelative;
	private boolean fromIndent;

	private CLValue colincVal;
	private CLValue colidVal;

	public TabulateEdict(boolean isRelative, CLValue colinc, boolean fromIndent,
			CLValue colid) {
		this.isRelative = isRelative;
		this.fromIndent = fromIndent;

		this.colincVal = colinc;

		this.colidVal = colid;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		ReportWriter rw = formCTX.writer;

		Tape<Object> itms = formCTX.items;

		char padchar = ' ';

		int currCol;

		if (fromIndent) {
			currCol = rw.getIndentPos();
		} else {
			currCol = rw.getLinePos();
		}

		if (isRelative) {
			int colinc = colincVal.asInt(itms, "column increment", "T", 1);
			int colrel = colidVal.asInt(itms, "relative column number", "T", 1);

			for (int i = 0; i < colrel; i++) {
				rw.write(padchar);
			}

			int nSpaces = 0;

			while ((currCol + nSpaces) % colinc != 0) {
				rw.write(padchar);

				nSpaces++;
			}
		} else {
			int colinc = colincVal.asInt(itms, "column increment", "T", 1);
			int colnum = colidVal.asInt(itms, "column number", "T", 1);

			if (currCol < colnum) {
				for (int i = currCol; i < colnum; i++) {
					rw.write(padchar);
				}
			} else {
				if (colinc == 0)
					return;

				int k = 0;

				while (colnum > (currCol + (k * colinc))) {
					rw.write(padchar);

					k++;
				}
			}
		}
	}
}
