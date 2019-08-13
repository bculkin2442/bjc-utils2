package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.inflexion.*;

import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;

/**
 * Inflection directive.
 * @author bjculkin
 *
 */
public class InflectDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		List<Decree> body = new ArrayList<>();

		int nestLevel = 1;

		Iterator<Decree> dirIter = compCTX.directives;
		while (dirIter.hasNext()) {
			Decree decr = dirIter.next();
			if (decr.isLiteral) {
				body.add(decr);

				continue;
			}

			String dirName = decr.name;

			if (dirName != null) {
				if (dirName.equals("`[")) {
					if (nestLevel > 0) {
						body.add(decr);
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					body.add(decr);
				} else if (dirName.equals("`]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration. */
					if (nestLevel == 0) break;
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);
				} else {
					/* Not a special directive. */
					body.add(decr);
				}
			}
		}

		return new InflectEdict(body, compCTX.formatter);
	}
}

class InflectEdict implements Edict {
	private List<Decree> body;

	private CLFormatter fmt;

	public InflectEdict(List<Decree> body, CLFormatter fmt) {
		this.body = body;

		this.fmt = fmt;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		ReportWriter nrw = formCTX.writer.duplicate(new StringWriter());

		fmt.doFormatString(body, nrw, formCTX.items, false);

		String strang = nrw.toString();

		strang = InflectionML.inflect(strang);

		formCTX.writer.write(strang);
	}
}
