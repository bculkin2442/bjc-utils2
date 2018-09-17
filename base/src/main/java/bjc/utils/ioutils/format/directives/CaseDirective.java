
package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.*;
import bjc.utils.ioutils.ReportWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseDirective implements Directive {
	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	@Override
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		StringBuffer condBody = new StringBuffer();

		int nestLevel = 1;

		while (dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");

			if (dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("(")) {
					if (nestLevel > 0) {
						condBody.append(dirMatcher.group());
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirMatcher.group());
				} else if (dirName.equals(")")) {
					nestLevel = Math.max(0, nestLevel - 1);

					/* End the iteration. */
					if (nestLevel == 0) break;
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);
				} else {
					/* Not a special directive. */
					condBody.append(dirMatcher.group());
				}
			}
		}

		String frmt = condBody.toString();

		ReportWriter nrw = rw.duplicate(new StringWriter());

		fmt.doFormatString(frmt, nrw, tParams, false);

		String strang = nrw.toString();

		if (mods.colonMod && mods.atMod) {
			strang = strang.toUpperCase();
		} else if (mods.colonMod) {
			Matcher mat = wordPattern.matcher(strang);

			StringBuffer sb = new StringBuffer();
			while(!mat.find()) {
				mat.appendReplacement(sb, "");

				String word = mat.group(1);
				
				word = word.substring(0, 1).toUpperCase() + word.substring(1);

				sb.append(word);
				sb.append(mat.group(2));
			}

			mat.appendTail(sb);

			strang = sb.toString();
		} else if (mods.atMod) {
			Matcher mat = wordPattern.matcher(strang);

			StringBuffer sb = new StringBuffer();
			boolean doCap = true;
			while(!mat.find()) {
				mat.appendReplacement(sb, "");

				String word = mat.group(1);
				
				if (doCap) {
					doCap = false;

					word = word.substring(0, 1).toUpperCase() + word.substring(1);
				}

				sb.append(word);
				sb.append(mat.group(2));
			}

			mat.appendTail(sb);

			strang = sb.toString();

		} else {
			strang = strang.toLowerCase();
		}

		rw.write(strang);
	}
}
