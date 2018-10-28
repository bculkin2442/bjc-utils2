package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.io.StringWriter;
import bjc.inflexion.InflectionML;
import bjc.utils.ioutils.ReportWriter;

/**
 * Inflection directive.
 * @author bjculkin
 *
 */
public class InflectDirective implements Directive {
	//private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		StringBuffer condBody = new StringBuffer();

		int nestLevel = 1;

		while (dirParams.dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirParams.dirMatcher.group("name");

			if (dirName != null) {
				/* Append everything up to this directive. */
				dirParams.dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("`[")) {
					if (nestLevel > 0) {
						condBody.append(dirParams.dirMatcher.group());
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirParams.dirMatcher.group());
				} else if (dirName.equals("`]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration. */
					if (nestLevel == 0) break;
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);
				} else {
					/* Not a special directive. */
					condBody.append(dirParams.dirMatcher.group());
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
