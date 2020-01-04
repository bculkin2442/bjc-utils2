package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the ( directive.
 *
 * This directive does case manipulation of the contained text.
 *
 * @author Ben Culkin
 */
public class CaseDirective implements Directive {
	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	public Edict compile(CompileContext compCTX) {
		CLModifiers mods = compCTX.decr.modifiers;

		GroupDecree condBody = compCTX.directives.nextGroup(compCTX.decr, ")");

		CaseEdict.Mode mode;

		if (mods.colonMod && mods.atMod) {
			mode = CaseEdict.Mode.UPPERCASE;
		} else if (mods.colonMod) {
			mode = CaseEdict.Mode.WORD_UPPERCASE;
		} else if (mods.atMod) {
			mode = CaseEdict.Mode.FIRST_UPPERCASE;
		} else {
			mode = CaseEdict.Mode.LOWERCASE;
		}

		return new CaseEdict(condBody, mode, compCTX.formatter);
	}
}

class CaseEdict implements Edict {
	public static enum Mode {
		UPPERCASE,
		WORD_UPPERCASE,
		FIRST_UPPERCASE,
		LOWERCASE
	}

	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	private CLString body;

	private Mode caseMode;

	private CLFormatter formatter;

	public CaseEdict(GroupDecree body, Mode caseMode, CLFormatter fmt) {
		this.body = new CLString(fmt.compile(body.unwrap()));
		
		this.caseMode = caseMode;

		this.formatter = fmt;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		ReportWriter nrw = formCTX.getScratchWriter();

		//formatter.doFormatString(body, nrw, formCTX.items, false);

		String strang = body.format(nrw, formCTX.items);

		switch (caseMode) {
		case UPPERCASE:
			strang = strang.toUpperCase();
			break;
		case WORD_UPPERCASE:
			{
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
			}
			break;
		case FIRST_UPPERCASE:
			{
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
			}
			break;
		case LOWERCASE:
			strang = strang.toLowerCase();
			break;
		}

		formCTX.writer.write(strang);
	}
}
