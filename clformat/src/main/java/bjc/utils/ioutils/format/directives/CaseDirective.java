package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.regex.*;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the ( directive.
 *
 * This directive does case manipulation of the contained text.
 *
 * @author Ben Culkin
 */
public class CaseDirective implements Directive {
	/**
	 * Compile a case directive.
	 *
	 * @param compCTX
	 *                The context to use for compilation.
	 */
	@Override
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
		UPPERCASE, WORD_UPPERCASE, FIRST_UPPERCASE, LOWERCASE
	}

	private static final Pattern wordPattern = Pattern.compile("(\\w+)(\\b*)");

	private CLString body;

	private Mode caseMode;

	public CaseEdict(GroupDecree body, Mode caseMode, CLFormatter fmt) {
		this.body = new CLString(fmt.compile(body.unwrap()));

		this.caseMode = caseMode;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		try (ReportWriter nrw = formCTX.getScratchWriter()) {
			String strang = body.format(nrw, formCTX.items);

			switch (caseMode) {
			case UPPERCASE:
				strang = strang.toUpperCase();
				break;
			case WORD_UPPERCASE: {
				Matcher mat = wordPattern.matcher(strang);

				StringBuffer sb = new StringBuffer();
				while (mat.find()) {
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
			case FIRST_UPPERCASE: {
				Matcher mat = wordPattern.matcher(strang);

				StringBuffer sb = new StringBuffer();
				boolean doCap = true;
				while (mat.find()) {
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
			default:
				throw new IllegalArgumentException("INTERNAL ERROR: CaseEdict mode "
						+ caseMode + " is not supported. This is a bug.");
			}

			formCTX.writer.write(strang);
		}
	}
}
