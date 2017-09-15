package bjc.utils.ioutils;

import java.util.HashMap;
import java.util.IllegalFormatConversionException;
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
		Matcher dirMatcher = pFormatDirective.matcher(format);

		/*
		 * Put the parameters where we can easily handle them.
		 */
		Tape<Object> tParams = new SingleTape(params);

		while(dirMatcher.find()) {
			dirMatcher.appendReplacement(sb, "");

			String dirName   = dirMatcher.group("name");
			String dirFunc   = dirMatcher.group("funcname");
			String dirMods   = dirMatcher.group("modifiers");
			String dirParams = dirMatcher.group("params");

			CLParameters arrParams = CLParameters.fromDirective(dirParams.split("(?<!'),"), tParams);
			
			boolean atMod    = false;
			boolean colonMod = false;
			if(dirMods != null) {
				atMod    = dirMods.contains("@");
				colonMod = dirMods.contains(":");
			}

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
				handleAestheticDirective(sb, item, atMod, colonMod, arrParams);
				tParams.right();
				break;
			case "B":
				checkItem(item, 'B');
				handleNumberDirective(sb, atMod, colonMod, arrParams, -1, item, 2);
				tParams.right();
				break;
			case "C":
				checkItem(item, 'C');
				handleCDirective(sb, item, atMod, colonMod);
				tParams.right();
				break;
			case "D":
				checkItem(item, 'D');
				handleNumberDirective(sb, atMod, colonMod, arrParams, -1, item, 10);
				tParams.right();
				break;
			case "O":
				checkItem(item, 'O');
				handleNumberDirective(sb, atMod, colonMod, arrParams, -1, item, 8);
				tParams.right();
				break;
			case "R":
				if(item == null)
					throw new IllegalArgumentException("No argument provided for C directive");

				handleRadixDirective(sb, atMod, colonMod, arrParams, item);
				tParams.right();
				break;
			case "X":
				checkItem(item, 'X');
				handleNumberDirective(sb, atMod, colonMod, arrParams, -1, item, 16);
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
			case "F":
			case "E":
			case "G":
			case "$":
				/* @TODO implement floating point directives. */
				throw new IllegalArgumentException("Floating-point directives aren't implemented yet.");
			case "S":
			case "W":
				throw new IllegalArgumentException("S and W aren't implemented. Use A instead");
			default:
				String msg = String.format("Unknown format directive '%s'", dirName);
				throw new UnknownFormatConversionException(msg);
			}
		}

		dirMatcher.appendTail(sb);

		return sb.toString();
	}

	private void handleCDirective(StringBuffer buff, Object parm, boolean atMod, boolean colonMod) {
		if(!(parm instanceof Character)) {
			throw new IllegalFormatConversionException('C', parm.getClass());
		}

		char ch = (Character) parm;
		int codepoint = (int) ch;

		if(colonMod) {
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

	private void handleNumberDirective(StringBuffer buff, boolean atMod, boolean colonMod, CLParameters params, int argidx, long val, int radix) {
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

		if(colonMod) {
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

			NumberUtils.toCommaString(val, mincol, padchar, commaInterval, commaChar, atMod, radix);
		} else {
			NumberUtils.toNormalString(val, mincol, padchar, atMod, radix);
		}
	}

	private void handleRadixDirective(StringBuffer buff, boolean atMod, boolean colonMod, CLParameters params, Object arg) {
		if(!(arg instanceof Number)) {
			throw new IllegalFormatConversionException('R', arg.getClass());
		}

		/*
		 * @TODO see if this is the way we want to do this.
		 */
		long val = ((Number)arg).longValue();

		if(params.length() == 0) {
			if(atMod) {
				buff.append(NumberUtils.toRoman((Long)val, colonMod));
			} else if(colonMod) {
				buff.append(NumberUtils.toOrdinal(val));
			} else {
				buff.append(NumberUtils.toCardinal(val));
			}
		} else {
			if(params.length() < 1)
				throw new IllegalArgumentException("R directive requires at least one parameter, the radix");

			int radix = params.getInt(0, "radix", 'R');

			handleNumberDirective(buff, atMod, colonMod, params, 0, val, radix);
		}
	}

	private void handleAestheticDirective(StringBuffer buff, Object item, boolean atMod, boolean colonMod, CLParameters arrParams) {
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

		if(atMod) {
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

		if(!atMod) {
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
}
