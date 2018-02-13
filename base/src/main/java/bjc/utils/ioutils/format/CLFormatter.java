package bjc.utils.ioutils.format;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;

import java.util.HashMap;
import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bjc.utils.PropertyDB.applyFormat;
import static bjc.utils.PropertyDB.getRegex;

/**
 * An implementation of CL's FORMAT.
 * 
 * @author EVE
 *
 */
public class CLFormatter {
	private static final String prefixParam = getRegex("clFormatPrefix");

	private static final String formatMod = getRegex("clFormatModifier");

	private static final String prefixList = applyFormat("delimSeparatedList", prefixParam, ",");

	private static final String directiveName = getRegex("clFormatName");

	private static final String	formatDirective		= applyFormat("clFormatDirective", prefixList,
			formatMod, directiveName);
	private static final Pattern	pFormatDirective	= Pattern.compile(formatDirective);

	private static Map<String, Directive> builtinDirectives;

	private Map<String, Directive> extraDirectives;

	static {
		builtinDirectives = new HashMap<>();

		builtinDirectives.put("A", new AestheticDirective());

		builtinDirectives.put("C", new CharacterDirective());

		builtinDirectives.put("B", new NumberDirective(-1, 2));
		builtinDirectives.put("O", new NumberDirective(-1, 8));
		builtinDirectives.put("D", new NumberDirective(-1, 10));
		builtinDirectives.put("X", new NumberDirective(-1, 16));

		builtinDirectives.put("R", new RadixDirective());

		builtinDirectives.put("&", new FreshlineDirective());

		builtinDirectives.put("%", new LiteralDirective("\n", '%'));
		builtinDirectives.put("|", new LiteralDirective("\f", '|'));
		builtinDirectives.put("~", new LiteralDirective("~", '~'));

		builtinDirectives.put("*", new GotoDirective());

		builtinDirectives.put("^", new EscapeDirective());
		builtinDirectives.put("[", new ConditionalDirective());
	}

	/**
	 * Create a new CL formatter.
	 */
	public CLFormatter() {
		extraDirectives = new HashMap<>();
	}

	static void checkItem(Object itm, char directive) {
		if(itm == null) throw new IllegalArgumentException(
				String.format("No argument provided for %c directive", directive));
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param format
	 *        The format string to use.
	 * @param params
	 *        The parameters for the string.
	 * @return The formatted string.
	 */
	public String formatString(String format, Object... params) {
		StringBuffer sb = new StringBuffer();
		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, sb, tParams);

		return sb.toString();
	}

	void doFormatString(String format, StringBuffer sb, Tape<Object> tParams) {
		Matcher dirMatcher = pFormatDirective.matcher(format);

		while(dirMatcher.find()) {
			dirMatcher.appendReplacement(sb, "");

			String dirName = dirMatcher.group("name");
			String dirFunc = dirMatcher.group("funcname");
			String dirMods = dirMatcher.group("modifiers");
			String dirParams = dirMatcher.group("params");

			CLParameters arrParams = CLParameters.fromDirective(dirParams.split("(?<!'),"), tParams);
			CLModifiers mods = CLModifiers.fromString(dirMods);

			Object item = tParams.item();
			if(dirName == null && dirFunc != null) {
				/*
				 * @TODO implement user-called functions.
				 */
				continue;
			}

			if(extraDirectives.containsKey(dirName)) {
				extraDirectives.get(dirName).format(sb, item, mods, arrParams, tParams, dirMatcher,
						this);

				continue;
			}

			if(builtinDirectives.containsKey(dirName)) {
				builtinDirectives.get(dirName).format(sb, item, mods, arrParams, tParams, dirMatcher,
						this);

				continue;
			}

			if(dirName == null) dirName = "<null>";

			switch(dirName) {
			case "]":
				throw new IllegalArgumentException("Found conditional-end outside of conditional.");
			case ";":
				throw new IllegalArgumentException(
						"Found conditional-seperator outside of conditional.");
			case "T":
			case "<":
			case ">":
				/*
				 * @TODO Figure out how to implement
				 * tabulation/justification in a reasonable
				 * manner.
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
				/*
				 * @TODO figure out if we want to implement
				 * someting for these directives instead of
				 * punting.
				 */
				throw new IllegalArgumentException("S and W aren't implemented. Use A instead");
			default:
				String msg = String.format("Unknown format directive '%s'", dirName);
				throw new UnknownFormatConversionException(msg);
			}
		}

		dirMatcher.appendTail(sb);
	}

}