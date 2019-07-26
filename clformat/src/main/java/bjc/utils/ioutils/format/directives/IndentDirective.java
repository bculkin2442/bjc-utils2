package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implement the I directive.
 * @author student
 *
 */
public class IndentDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Tape<Object> itemTape = dirParams.tParams;

		CLModifiers mods = dirParams.getMods();
		CLParameters params = dirParams.getParams();

		// Dollar mod is indent configuration
		if (mods.dollarMod) {
			return;
		}

		int nIndents = 1;

		if(params.length() >= 1) {
			params.mapIndices("count");

			nIndents = params.getInt(itemTape, "count", "indent count", "I", 1);
		}

		boolean dedent = false;
		if (nIndents < 0) {
			nIndents = -nIndents;

			dedent = true;
		}

		if (mods.colonMod) {
			if (dedent) dirParams.rw.dedent(nIndents);
			else        dirParams.rw.indent(nIndents);
		} else {
			if (dedent) throw new IllegalArgumentException("Cannot have negative indent level");

			dirParams.rw.setLevel(nIndents);
		}
	}
}
