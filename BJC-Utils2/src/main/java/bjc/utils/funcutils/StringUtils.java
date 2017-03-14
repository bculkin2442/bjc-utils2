package bjc.utils.funcutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for operations on strings
 *
 * @author ben
 *
 */
public class StringUtils {
	/**
	 * Checks if the given expression contains the specified operator in a
	 * situation that indicates its use as an infix operator.
	 *
	 * @param expression
	 *                The expression to check
	 * @param operator
	 *                The operator to see if it is contained
	 * @return Whether or not the given expression contains the specified
	 *         operator as a infix operator
	 */
	public static boolean containsInfixOperator(String expression, String operator) {
		// Bit annoying to have to use a full class name, but what are
		// you
		// going to do?
		return org.apache.commons.lang3.StringUtils.countMatches(expression, operator) == 1
				&& !expression.equalsIgnoreCase(operator) && !expression.startsWith(operator);
	}

	/**
	 * Check if a string consists only of one or more matches of a regular
	 * expression
	 *
	 * @param input
	 *                The string to check
	 * @param regex
	 *                The regex to see if the string only contains matches
	 *                of
	 * @return Whether or not the string consists only of multiple matches
	 *         of the provided regex
	 */
	public static boolean containsOnly(String input, String regex) {
		if(input == null)
			throw new NullPointerException("Input must not be null");
		else if(regex == null) throw new NullPointerException("Regex must not be null");

		/*
		 * This regular expression is fairly simple.
		 *
		 * First, we match the beginning of the string. Then, we start a
		 * non-capturing group whose contents are the passed in regex.
		 * That group is then matched one or more times and the pattern
		 * matches to the end of the string
		 */
		return input.matches("\\A(?:" + regex + ")+\\Z");
	}

	/**
	 * Indent the string being built in a StringBuilder n levels
	 *
	 * @param builder
	 *                The builder to indent in
	 * @param levels
	 *                The number of levels to indent
	 */
	public static void indentNLevels(StringBuilder builder, int levels) {
		for(int i = 0; i < levels; i++) {
			builder.append("\t");
		}
	}

	/**
	 * Print out a deque with a special case for easily showing a deque is
	 * empty
	 *
	 * @param <ContainedType>
	 *                The type in the deque
	 * @param queue
	 *                The deque to print
	 * @return A string version of the deque, with allowance for an empty
	 *         deque
	 */
	public static <ContainedType> String printDeque(Deque<ContainedType> queue) {
		return queue.isEmpty() ? "(none)" : queue.toString();
	}

	/*
	 * This regex matches java-style string escapes
	 */
	private static String	escapeString	= "\\\\([btnfr\"'\\\\]"		// Match
										// shortform
										// escape
										// sequences
										// like
										// \t
										// or
										// \"
			+ "|[0-3]?[0-7]{1,2}"					// Match
										// octal
										// escape
										// sequences
			+ "|u[0-9a-fA-F]{4})";					// Match
										// unicode
										// escape
										// sequences
	private static Pattern	escapePatt	= Pattern.compile(escapeString);

	/*
	 * This regular expression matches java style double quoted strings
	 */
	private static Pattern doubleQuotePatt = Pattern.compile("(\"(" + "[^\\\\\"]+" // Match
											// one
											// or
											// more
											// characters
											// that
											// aren't
											// quotes
											// or
											// slashes
			+ "|" + escapeString + ")" // Match escape sequences
			+ "*\")"); // Match all of those things zero or more
					// times, followed by a closing quote

	/**
	 * Remove double quoted strings from a string.
	 *
	 * Splits a string around instances of java-style double-quoted strings.
	 *
	 * @param inp
	 *                The string to split.
	 *
	 * @return An list containing alternating bits of the string and the
	 *         embedded double-quoted strings that seperated them.
	 */
	public static List<String> removeDQuotedStrings(String inp) {
		StringBuffer work = new StringBuffer();
		List<String> res = new LinkedList<>();

		Matcher mt = doubleQuotePatt.matcher(inp);

		while(mt.find()) {
			mt.appendReplacement(work, "");

			res.add(work.toString());
			res.add(mt.group(1));

			work = new StringBuffer();
		}
		mt.appendTail(work);
		res.add(work.toString());

		return res;
	}

	/**
	 * Replace escape characters with their actual equivalents.
	 *
	 * @param inp
	 *                The string to replace escape sequences in.
	 *
	 * @return The string with escape sequences replaced by their equivalent
	 *         characters.
	 */
	public static String descapeString(String inp) {
		StringBuffer work = new StringBuffer();

		Matcher escapeFinder = escapePatt.matcher(inp);
		while(escapeFinder.find()) {
			String escapeSeq = escapeFinder.group();

			String escapeRep = "";
			switch(escapeSeq) {
			case "\\b":
				escapeRep = "\b";
				break;
			case "\\t":
				escapeRep = "\t";
				break;
			case "\\n":
				escapeRep = "\n";
				break;
			case "\\f":
				escapeRep = "\f";
				break;
			case "\\r":
				escapeRep = "\r";
				break;
			case "\\\"":
				escapeRep = "\"";
				break;
			case "\\'":
				escapeRep = "'";
				break;
			case "\\\\":
				escapeRep = "\\";
				break;
			default:
				if(escapeSeq.startsWith("u")) {
					escapeRep = handleUnicodeEscape(escapeSeq.substring(1));
				} else {
					escapeRep = handleOctalEscape(escapeSeq);
				}
			}

			escapeFinder.appendReplacement(work, escapeRep);
		}
		escapeFinder.appendTail(work);

		return work.toString();
	}

	private static String handleUnicodeEscape(String seq) {
		int codepoint = Integer.parseInt(seq, 16);

		return new String(Character.toChars(codepoint));
	}

	private static String handleOctalEscape(String seq) {
		int codepoint = Integer.parseInt(seq, 8);

		return new String(Character.toChars(codepoint));
	}

	public static boolean isDouble(String inp) {
		return DoubleMatcher.floatingLiteral.matcher(inp).matches();
	}

	private static Pattern intLitPattern = Pattern.compile("\\A[+\\-]?\\d+\\Z");

	public static boolean isInt(String inp) {
		return intLitPattern.matcher(inp).matches();
	}
}
