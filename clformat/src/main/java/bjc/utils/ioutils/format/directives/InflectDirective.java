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
		List<Decree> condBody = new ArrayList<>();

		int nestLevel = 1;

		Iterator<Decree> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			Decree decr = dirIter.next();
			if (decr.isLiteral) {
				condBody.add(decr);

				continue;
			}

			String dirName = decr.name;

			if (dirName != null) {
				if (dirName.equals("`[")) {
					if (nestLevel > 0) {
						condBody.add(decr);
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.add(decr);
				} else if (dirName.equals("`]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration. */
					if (nestLevel == 0) break;
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);
				} else {
					/* Not a special directive. */
					condBody.add(decr);
				}
			}
		}

		ReportWriter nrw = dirParams.rw.duplicate(new StringWriter());

		dirParams.fmt.doFormatString(condBody.iterator(), nrw, dirParams.tParams, false);

		String strang = nrw.toString();

		strang = InflectionML.inflect(strang);

		dirParams.rw.write(strang);
	}
}
