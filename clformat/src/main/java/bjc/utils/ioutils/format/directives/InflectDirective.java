package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Matcher;

import bjc.inflexion.InflectionML;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.CLPattern;

/**
 * Inflection directive.
 * @author bjculkin
 *
 */
public class InflectDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		StringBuilder condBody = new StringBuilder();

		int nestLevel = 1;

		Iterator<String> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			String direc = dirIter.next();
			if (!direc.startsWith("~")) {
				condBody.append(direc);
				continue;
			}

			Matcher dirMat = CLPattern.getDirectiveMatcher(direc);

			/* Process a list of clauses. */
			String dirName = dirMat.group("name");

			if (dirName != null) {
				if (dirName.equals("`[")) {
					if (nestLevel > 0) {
						condBody.append(dirMat.group());
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirMat.group());
				} else if (dirName.equals("`]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration. */
					if (nestLevel == 0) break;
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);
				} else {
					/* Not a special directive. */
					condBody.append(dirMat.group());
				}
			}
		}

		String frmt = condBody.toString();

		ReportWriter nrw = dirParams.rw.duplicate(new StringWriter());

		dirParams.fmt.doFormatString(frmt, nrw, dirParams.tParams, false);

		String strang = nrw.toString();

		strang = InflectionML.inflect(strang);

		dirParams.rw.write(strang);
	}
}
