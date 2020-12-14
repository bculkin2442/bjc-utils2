package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import bjc.inflexion.*;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

/**
 * Inflection directive.
 * 
 * @author bjculkin
 *
 */
public class InflectDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		List<SimpleDecree> body = new ArrayList<>();

		int nestLevel = 1;

		Iterator<SimpleDecree> dirIter = compCTX.directives;
		while (dirIter.hasNext()) {
			SimpleDecree decr = dirIter.next();
			if (decr.isLiteral) {
				body.add(decr);

				continue;
			}

			String dirName = decr.name;

			// @FIXME Nov 13th 2020 Ben Culkin :GroupDecree
			// Would it be appropriate to convert this to use GroupDecree?
			if (dirName != null) {
				if (dirName.equals("`[")) {
					if (nestLevel > 0) body.add(decr);

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					body.add(decr);
				} else if (dirName.equals("`]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration if this was the final `]. */
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
	private CLString body;

	public InflectEdict(List<SimpleDecree> body, CLFormatter fmt) {
		this.body = new CLString(fmt.compile(body));
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		ReportWriter newWriter = formCTX.writer.duplicate(new StringWriter());

		body.format(newWriter, formCTX.items);

		String strang = newWriter.toString();

		strang = InflectionML.inflect(strang);

		formCTX.writer.write(strang);
	}
}
