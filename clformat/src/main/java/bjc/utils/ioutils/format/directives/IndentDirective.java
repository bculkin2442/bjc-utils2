package bjc.utils.ioutils.format.directives;

import java.io.IOException;

/**
 * Implement the I directive.
 * @author student
 *
 */
public class IndentDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		// Dollar mod is indent configuration
		if (dirParams.mods.dollarMod) {

			return;
		}

		int nIndents = 1;

		if(dirParams.arrParams.length() >= 1) {
			nIndents = dirParams.arrParams.getInt(0, "indent count", "I");
		}

		boolean dedent = false;
		if (nIndents < 0) {
			nIndents = -nIndents;

			dedent = true;
		}

		if (dirParams.mods.colonMod) {
			if (dedent) dirParams.rw.dedent(nIndents);
			else        dirParams.rw.indent(nIndents);
		} else {
			if (dedent) throw new IllegalArgumentException("Cannot have negative indent level");

			dirParams.rw.setLevel(nIndents);
		}
	}
}
