package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;

public class CaseDirective implements Directive {
	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		CLModifiers mods = dirParams.getMods();

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
				if (dirName.equals("(")) {
					if (nestLevel > 0) {
						condBody.add(decr);
					}

					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.add(decr);
				} else if (dirName.equals(")")) {
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

		dirParams.fmt.doFormatString(condBody, nrw, dirParams.tParams, false);

		String strang = nrw.toString();

		if (mods.colonMod && mods.atMod) {
			strang = strang.toUpperCase();
		} else if (mods.colonMod) {
			Matcher mat = wordPattern.matcher(strang);

			StringBuffer sb = new StringBuffer();
			while(mat.find()) {
				mat.appendReplacement(sb, "");

				String word = mat.group(1);
				String ward = word.substring(0, 1).toUpperCase() + word.substring(1);

				sb.append(ward);
				sb.append(mat.group(2));
			}

			mat.appendTail(sb);

			strang = sb.toString();
		} else if (mods.atMod) {
			Matcher mat = wordPattern.matcher(strang);

			StringBuffer sb = new StringBuffer();
			boolean doCap = true;
			while(mat.find()) {
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
