package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.CLPattern;

public class CaseDirective implements Directive {
	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		StringBuilder condBody = new StringBuilder();

		int nestLevel = 1;

		Iterator<String> dirIter = dirParams.dirIter;
		while (dirParams.dirIter.hasNext()) {
			String direc = dirIter.next();
			if (!direc.startsWith("~")) {
				condBody.append(direc);
				continue;
			}

			Matcher dirMat = CLPattern.getDirectiveMatcher(direc);
			dirMat.find();

			/* Process a list of clauses. */
			String dirName = dirMat.group("name");

			if (dirName != null) {
				if (dirName.equals("(")) {
					if (nestLevel > 0) {
						condBody.append(dirMat.group());
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirMat.group());
				} else if (dirName.equals(")")) {
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

		if (dirParams.mods.colonMod && dirParams.mods.atMod) {
			strang = strang.toUpperCase();
		} else if (dirParams.mods.colonMod) {
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
		} else if (dirParams.mods.atMod) {
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

		dirParams.rw.write(strang);
	}
}
