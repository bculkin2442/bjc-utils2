package bjc.utils.ioutils;

import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.utils.PropertyDB;
import bjc.utils.esodata.Tape;
import bjc.utils.esodata.SingleTape;

import static bjc.utils.PropertyDB.applyFormat;
import static bjc.utils.PropertyDB.getCompiledRegex;
import static bjc.utils.PropertyDB.getRegex;

public class CLFormatter {
	public static class CLModifiers {
		public final boolean atMod;
		public final boolean colonMod;

		public CLModifiers(boolean at, boolean colon) {
			atMod    = at;
			colonMod = colon;
		}

		public static CLModifiers fromString(String modString) {
			boolean atMod    = false;
			boolean colonMod = false;
			if(modString != null) {
				atMod    = modString.contains("@");
				colonMod = modString.contains(":");
			}

			return new CLModifiers(atMod, colonMod);
		}
	}

	public static class EscapeException extends RuntimeException {
		public final boolean endIteration;

		public EscapeException() {
			endIteration = false;
		}

		public EscapeException(boolean end) {
			endIteration = end;
		}
	}

	@FunctionalInterface
	public interface Directive {
		/*
		 * @TODO fill in parameters
		 */
		public void format();
	}

	private static final String  prefixParam  = getRegex("clFormatPrefix");
	private static final Pattern pPrefixParam = Pattern.compile(prefixParam);

	private static final String  formatMod  = getRegex("clFormatModifier");

	private static final String prefixList = applyFormat("delimSeparatedList", prefixParam, ",");

	private static final String directiveName = getRegex("clFormatName");

	private static final String  formatDirective  = applyFormat("clFormatDirective", prefixList, formatMod, directiveName);
	private static final Pattern pFormatDirective = Pattern.compile(formatDirective);

	private Map<String, Directive> extraDirectives;

	public CLFormatter() {
		extraDirectives = new HashMap<>();
	}

	private void checkItem(Object itm, char directive) {
		if(itm == null)
			throw new IllegalArgumentException(String.format("No argument provided for %c directive", directive));
	}

	public String formatString(String format, Object... params) {
		StringBuffer sb = new StringBuffer();
		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape(params);

		doFormatString(format, sb, tParams);

		return sb.toString();
	}

	private void doFormatString(String format, StringBuffer sb, Tape<Object> tParams) {
		Matcher dirMatcher = pFormatDirective.matcher(format);

		while(dirMatcher.find()) {
			dirMatcher.appendReplacement(sb, "");

			String dirName   = dirMatcher.group("name");
			String dirFunc   = dirMatcher.group("funcname");
			String dirMods   = dirMatcher.group("modifiers");
			String dirParams = dirMatcher.group("params");

			CLParameters arrParams = CLParameters.fromDirective(dirParams.split("(?<!'),"), tParams);
			CLModifiers  mods      = CLModifiers.fromString(dirMods);

			Object item = tParams.item();
			if(dirName == null && dirFunc != null) {
				/*
				 * @TODO implement user-called functions.
				 */
				continue;
			}

			switch(dirName) {
				case "A":
					checkItem(item, 'A');
					handleAestheticDirective(sb, item, mods, arrParams);
					tParams.right();
					break;
				case "B":
					checkItem(item, 'B');
					if(!(item instanceof Number)) {
						throw new IllegalFormatConversionException('B', item.getClass());
					}
					handleNumberDirective(sb, mods, arrParams, -1, ((Number)item).longValue(), 2);
					tParams.right();
					break;
				case "C":
					checkItem(item, 'C');
					handleCDirective(sb, item, mods);
					tParams.right();
					break;
				case "D":
					checkItem(item, 'D');
					if(!(item instanceof Number)) {
						throw new IllegalFormatConversionException('D', item.getClass());
					}
					handleNumberDirective(sb, mods, arrParams, -1, ((Number)item).longValue(), 10);
					tParams.right();
					break;
				case "O":
					checkItem(item, 'O');
					if(!(item instanceof Number)) {
						throw new IllegalFormatConversionException('O', item.getClass());
					}
					handleNumberDirective(sb, mods, arrParams, -1, ((Number)item).longValue(), 8);
					tParams.right();
					break;
				case "R":
					checkItem(item, 'R');
					handleRadixDirective(sb, mods, arrParams, item);
					tParams.right();
					break;
				case "X":
					checkItem(item, 'X');
					if(!(item instanceof Number)) {
						throw new IllegalFormatConversionException('X', item.getClass());
					}
					handleNumberDirective(sb, mods, arrParams, -1, ((Number)item).longValue(), 16);
					tParams.right();
					break;
				case "&":
					handleFreshlineDirective(sb, arrParams);
					break;
				case "%":
					handleLiteralDirective(sb, arrParams, "\n", '%');
					break;
				case "|":
					handleLiteralDirective(sb, arrParams, "\f", '|');
					break;
				case "~":
					handleLiteralDirective(sb, arrParams, "~", '~');
					break;
				case "*":
					handleGotoDirective(mods, arrParams, tParams);
					break;
				case "^":
					handleEscapeDirective(mods, arrParams, tParams);
					break;
				case "[":
					handleConditionalDirective(sb, mods, arrParams, tParams, dirMatcher);
					break;
				case "]":
					throw new IllegalArgumentException("Found conditional-end outside of conditional.");
				case ";":
					throw new IllegalArgumentException("Found conditional-seperator outside of conditional.");
				case "T":
				case "<":
				case ">":
					/* @TODO
					 * Figure out how to implement
					 * tabulation/justification in a
					 * reasonable manner.
					 */
					throw new IllegalArgumentException("Layout-control directives aren't implemented yet.");
				case "F":
				case "E":
				case "G":
				case "$":
					/* @TODO implement floating point directives. */
					throw new IllegalArgumentException("Floating-point directives aren't implemented yet.");
				case "S":
				case "W":
					/* @TODO 
					 * figure out if we want to implement
					 * someting for these directives instead
					 * of punting.
					 * */
					throw new IllegalArgumentException("S and W aren't implemented. Use A instead");
				default:
					String msg = String.format("Unknown format directive '%s'", dirName);
					throw new UnknownFormatConversionException(msg);
			}
		}

		dirMatcher.appendTail(sb);
	}

	private void handleCDirective(StringBuffer buff, Object parm, CLModifiers mods) {
		if(!(parm instanceof Character)) {
			throw new IllegalFormatConversionException('C', parm.getClass());
		}

		char ch = (Character) parm;
		int codepoint = (int) ch;

		if(mods.colonMod) {
			/*
			 * Colon mod means print Unicode character name.
			 */
			buff.append(Character.getName(codepoint));
		} else {
			buff.append(ch);
		}
	}

	private void handleFreshlineDirective(StringBuffer buff, CLParameters params)  {
		int nTimes = 1;

		if(params.length() > 1) {
			nTimes = params.getInt(0, "occurance count", '&');
		}

		if(buff.charAt(buff.length() - 1) == '\n') nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			buff.append("\n");
		}
	}

	private void handleLiteralDirective(StringBuffer buff, CLParameters params, String lit, char directive)  {
		int nTimes = 1;

		if(params.length() > 1) {
			nTimes = params.getInt(0, "occurance count", directive);
		}

		for(int i = 0; i < nTimes; i++) {
			buff.append(lit);
		}
	}

	private void handleNumberDirective(StringBuffer buff, CLModifiers mods, CLParameters params, int argidx, long val, int radix) {
		/*
		 * Initialize the two padding related parameters, and
		 * then fill them in from the directive parameters if
		 * they are present.
		 */
		int  mincol  = 0;
		char padchar = ' ';
		if(params.length() > (argidx + 2)) {
			mincol = params.getIntDefault(argidx + 1, "minimum column count", 'R', 0);
		}
		if(params.length() > (argidx + 3)) {
			padchar = params.getCharDefault(argidx + 2, "padding character", 'R', ' ');
		}

		if(mods.colonMod) {
			/*
			 * We're doing commas, so check if the two
			 * comma-related parameters were supplied.
			 */
			int  commaInterval = 0;
			char commaChar     = ',';
			if(params.length() > (argidx + 3)) {
				commaChar = params.getCharDefault((argidx + 3), "comma character", 'R', ' ');
			}
			if(params.length() > (argidx + 4)) {
				commaInterval = params.getIntDefault((argidx + 4), "comma interval", 'R', 0);
			}

			NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, mods.atMod, radix);
		} else {
			NumberUtils.toNormalString(val, mincol, padchar, mods.atMod, radix);
		}
	}

	private void handleRadixDirective(StringBuffer buff, CLModifiers mods, CLParameters params, Object arg) {
		if(!(arg instanceof Number)) {
			throw new IllegalFormatConversionException('R', arg.getClass());
		}

		/*
		 * @TODO see if this is the way we want to do this.
		 */
		long val = ((Number)arg).longValue();

		if(params.length() == 0) {
			if(mods.atMod) {
				buff.append(NumberUtils.toRoman((Long)val, mods.colonMod));
			} else if(mods.colonMod) {
				buff.append(NumberUtils.toOrdinal(val));
			} else {
				buff.append(NumberUtils.toCardinal(val));
			}
		} else {
			if(params.length() < 1)
				throw new IllegalArgumentException("R directive requires at least one parameter, the radix");

			int radix = params.getInt(0, "radix", 'R');

			handleNumberDirective(buff, mods, params, 0, val, radix);
		}
	}

	private void handleAestheticDirective(StringBuffer buff, Object item, CLModifiers mods, CLParameters params) {
		int mincol = 0, colinc = 1, minpad = 0;
		char padchar = ' ';

		if(params.length() > 1) {
			mincol = params.getIntDefault(0, "minimum column count", 'A', 0);
		}

		if(params.length() < 4) {
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		}

		colinc  = params.getIntDefault(1, "padding increment", 'A', 1);
		minpad  = params.getIntDefault(2, "minimum amount of padding", 'A', 0);
		padchar = params.getCharDefault(3, "padding character", 'A', ' ');

		StringBuilder work = new StringBuilder();

		if(mods.atMod) {
			for(int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for(int i = work.length(); i < mincol; i++) {
				for(int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		work.append(item.toString());

		if(!mods.atMod) {
			for(int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for(int i = work.length(); i < mincol; i++) {
				for(int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}
	}

	private void handleGotoDirective(CLModifiers mods, CLParameters params, Tape<Object> formatParams) {
		if(mods.colonMod) {
			int num = 1;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "number of arguments backward", '*', 1);
			}

			formatParams.left(num);
		} else if(mods.atMod) {
			int num = 0;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "argument index", '*', 0);
			}

			formatParams.first();
			formatParams.right(num);
		} else {
			int num = 1;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "number of arguments forward", '*', 1);
			}

			formatParams.right(num);
		}
	}

	private void handleConditionalDirective(StringBuffer sb, CLModifiers mods, CLParameters arrParams, Tape<Object> formatParams, Matcher dirMatcher) {
		StringBuffer condBody = new StringBuffer();

		List<String> clauses   = new ArrayList<>();
		String       defClause = null;
		boolean      isDefault = false;

		while(dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");
			String dirMods = dirMatcher.group("modifiers");

			if(dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if(dirName.equals("]")) {
					/* End the conditional. */
					String clause = condBody.toString();
					if(isDefault) {
						defClause = clause;
					} else {
						clauses.add(clause);
					}

					break;
				} else if(dirName.equals(";")) {
					/* End the clause. */
					String clause = condBody.toString();
					if(isDefault) {
						defClause = clause;
					} else {
						clauses.add(clause);
					}

					/* Mark the next clause as the default. */
					if(dirMods.contains(":")) {
						isDefault = true;
					}
				} else {
					/* Not a special directive. */
					condBody.append(dirMatcher.group());
				}
			}
		}

		Object par = formatParams.item();
		if(mods.colonMod) {
			formatParams.right();

			if(par == null) {
				throw new IllegalArgumentException("No parameter provided for [ directive.");
			} else if(!(par instanceof Boolean)) {
				throw new IllegalFormatConversionException('[', par.getClass());
			}
			boolean res = (Boolean)par;

			String fmt;
			if(res) fmt = clauses.get(1);
			else    fmt = clauses.get(0);

			doFormatString(fmt, sb, formatParams);
		} else if(mods.atMod) {
			if(par == null) {
				throw new IllegalArgumentException("No parameter provided for [ directive.");
			} else if(!(par instanceof Boolean)) {
				throw new IllegalFormatConversionException('[', par.getClass());
			}
			boolean res = (Boolean)par;

			if(res) {
				doFormatString(clauses.get(0), sb, formatParams);
			} else {
				formatParams.right();
			}
		} else {
			int res;
			if(arrParams.length() > 1) {
				res = arrParams.getInt(0, "conditional choice", '[');
			} else {
				if(par == null) {
					throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if(!(par instanceof Number)) {
					throw new IllegalFormatConversionException('[', par.getClass());
				}
				res = ((Number)par).intValue();

				formatParams.right();
			}

			if(res < 0 || res > clauses.size()) {
				if(defClause != null) doFormatString(defClause, sb, formatParams);
			} else {
				doFormatString(clauses.get(res), sb, formatParams);
			}
		}
		return;
	}

	private void handleEscapeDirective(CLModifiers mods, CLParameters params, Tape<Object> formatParams) {
		boolean shouldExit;

		switch(params.length()) {
		case 0:
			shouldExit = formatParams.size() == 0;
			break;
		case 1:
			int num    = params.getInt(0, "condition count", '^');
			shouldExit = num == 0;
			break;
		case 2:
			int left   = params.getInt(0, "left-hand condition", '^');
			int right  = params.getInt(1, "right-hand condition", '^');
			shouldExit = left == right;
			break;
		case 3:
		default:
			int low    = params.getInt(0, "lower-bound condition", '^');
			int mid    = params.getInt(1, "interval condition", '^');
			int high   = params.getInt(2, "upper-bound condition", '^');
			shouldExit = (low <= mid) && (mid <= high);
			break;
		}

		/* At negates it. */
		if(mods.atMod) shouldExit = !shouldExit;

		if(shouldExit) throw new EscapeException(mods.colonMod);
	}


}
