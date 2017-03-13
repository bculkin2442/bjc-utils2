package bjc.utils.funcutils;

import java.util.regex.Pattern;

/**
 * Split a string and keep given delimiters.
 *
 * @author Ben Culkin
 */
public class NeoTokenSplitter {
	/*
	 * This string is a format template for the delimiter matching regex
	 *
	 * It does two things 1. Match the provided delimiter by positive
	 * lookahead 2. Match the provided delimiter by positive lookbehind
	 *
	 * Thus, it will only match in places where the delimiter is, but won't
	 * actually match the delimiter, leaving split to put it into the stream
	 */
	private static String WITH_DELIM = "((?<=%1$s)|(?=%1$s))";

	/*
	 * This string is a format template for the multi-delimiter matching
	 * regex.
	 *
	 * It does the same thing as the single delimiter regex, but has to have
	 * some negative lookahead/lookbehind assertions to avoid splitting a
	 * delimiter into pieces.
	 */
	private static String WITH_MULTI_DELIM = "((?<=%1$s+)(?!%1$s)|(?<!%1$s)(?=%1$s+))";

	private StringBuilder currPatt;
	private StringBuilder currExclusionPatt;

	private Pattern compPatt;
	private Pattern exclusionPatt;

	/**
	 * Create a new token splitter.
	 */
	public NeoTokenSplitter() {
	}

	/**
	 * Split a provided string using configured delimiters, and keeping the
	 * delimiters.
	 *
	 * The splitter must be compiled first.
	 *
	 * @param inp
	 *                The string to split.
	 *
	 * @return The split string, including delimiters.
	 *
	 * @throws IllegalStateException
	 *                 If the splitter isn't compiled.
	 */
	public String[] split(String inp) {
		if (compPatt == null) {
			throw new IllegalStateException("Token splitter has not been compiled yet");
		}

		/*
		 * Don't split something that matches only an operator
		 */
		if (exclusionPatt.matcher(inp).matches())
			return new String[] { inp };
		return compPatt.split(inp);
	}

	/**
	 * Adds a string as a matched delimiter to split on.
	 *
	 * Only works for fixed length delimiters.
	 *
	 * The provided string is regex-escaped before being used.
	 *
	 * @param delim
	 *                The delimiter to match on.
	 */
	public void addDelimiter(String delim) {
		String quoteDelim = Pattern.quote(delim);
		String delimPat = String.format(WITH_DELIM, quoteDelim);

		if (currPatt == null) {
			currPatt = new StringBuilder();
			currExclusionPatt = new StringBuilder();

			currPatt.append("(?:" + delimPat + ")");
			currExclusionPatt.append("(?:" + quoteDelim + ")");
		} else {
			currPatt.append("|(?:" + delimPat + ")");
			currExclusionPatt.append("|(?:" + quoteDelim + ")");
		}
	}

	/**
	 * Adds a character class as a matched delimiter to split on.
	 *
	 * The provided string should be a pattern to match one or more
	 * occurances of.
	 *
	 * @param delim
	 *                The delimiter to split on.
	 */
	public void addMultiDelimiter(String delim) {
		String delimPat = String.format(WITH_MULTI_DELIM, "(?:" + delim + ")");

		if (currPatt == null) {
			currPatt = new StringBuilder();
			currExclusionPatt = new StringBuilder();

			currPatt.append("(?:" + delimPat + ")");
			currExclusionPatt.append("(?:(?:" + delim + ")+)");

		} else {
			currPatt.append("|(?:" + delimPat + ")");
			currExclusionPatt.append("|(?:(?:" + delim + ")+)");
		}
	}

	/**
	 * Compiles the current set of delimiters to a pattern.
	 *
	 * Makes this splitter ready to use.
	 */
	public void compile() {
		compPatt = Pattern.compile(currPatt.toString());
		exclusionPatt = Pattern.compile(currExclusionPatt.toString());
	}
}
