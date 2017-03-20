package bjc.utils.parserutils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities useful for operating on PL tokens.
 *
 * @author EVE
 *
 */
public class TokenUtils {
	/*
	 * This regex matches potential single character escape sequences.
	 */
	private static Pattern possibleEscape = Pattern.compile("\\\\.");
	/*
	 * This regex matches java-style string escapes
	 */
	private static String escapeString = "\\\\([btnfr\"'\\\\]" // Match shortform escape sequences like \t or \"
			+ "|[0-3]?[0-7]{1,2}"  // Match octal escape sequences
			+ "|u[0-9a-fA-F]{4})"; // Match unicode escape sequences

	private static Pattern escapePatt = Pattern.compile(escapeString);

	/*
	 * This regular expression matches java style double quoted strings
	 */
	private static Pattern doubleQuotePatt = Pattern.compile("(\"(" + "[^\\\\\"]+" // Match one or more characters that aren't quotes or slashes
			+ "|" + escapeString + ")" // Match escape sequences
			+ "*\")"); // Match all of those things zero or more times, followed by a closing quote

	/**
	 * Remove double quoted strings from a string.
	 *
	 * Splits a string around instances of java-style double-quoted strings.
	 *
	 * @param inp
	 *                The string to split.
	 *
	 * @return An list containing alternating bits of the string and the
	 *         embedded double-quoted strings that separated them.
	 */
	public static List<String> removeDQuotedStrings(String inp) {
		if(inp == null) {
			throw new NullPointerException("inp must not be null");
		}

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
		if(inp == null) {
			throw new NullPointerException("inp must not be null");
		}

		StringBuffer work = new StringBuffer();

		Matcher possibleEscapeFinder = possibleEscape.matcher(inp);
		Matcher escapeFinder = escapePatt.matcher(inp);

		while(possibleEscapeFinder.find()) {
			if(!escapeFinder.find()) {
				throw new IllegalArgumentException(
						"Illegal escape sequence " + possibleEscapeFinder.group());
			}

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
				/*
				 * Skip past the second slash.
				 */
				possibleEscapeFinder.find();
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
		try {
			int codepoint = Integer.parseInt(seq, 16);

			return new String(Character.toChars(codepoint));
		} catch(IllegalArgumentException iaex) {
			IllegalArgumentException reiaex = new IllegalArgumentException(
					String.format("'%s' is not a valid Unicode escape sequence'", seq));

			reiaex.initCause(iaex);

			throw reiaex;
		}
	}

	private static String handleOctalEscape(String seq) {
		try {
			int codepoint = Integer.parseInt(seq, 8);

			if(codepoint > 255) {
				throw new IllegalArgumentException(String
						.format("'%d' is outside the range of octal escapes', codepoint"));
			}

			return new String(Character.toChars(codepoint));
		} catch(IllegalArgumentException iaex) {
			IllegalArgumentException reiaex = new IllegalArgumentException(
					String.format("'%s' is not a valid octal escape sequence'", seq));

			reiaex.initCause(iaex);

			throw reiaex;
		}
	}

	/**
	 * Check if a given string would be successfully converted to a double
	 * by {@link Double#parseDouble(String)}.
	 * 
	 * @param inp
	 *                The string to check.
	 * @return Whether the string is a valid double or not.
	 */
	public static boolean isDouble(String inp) {
		return DoubleMatcher.floatingLiteral.matcher(inp).matches();
	}

	private static Pattern intLitPattern = Pattern.compile("\\A[+\\-]?\\d+\\Z");

	/**
	 * Check if a given string would be successfully converted to a integer
	 * by {@link Integer#parseInt(String)}.
	 * 
	 * NOTE: This only checks syntax. Using values out of the range of
	 * integers will still cause errors.
	 * 
	 * @param inp
	 *                The input to check.
	 * @return Whether the string is a valid double or not.
	 */
	public static boolean isInt(String inp) {
		return intLitPattern.matcher(inp).matches();
	}
}
