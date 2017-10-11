package bjc.utils.parserutils;

import static bjc.utils.PropertyDB.applyFormat;
import static bjc.utils.PropertyDB.getCompiledRegex;
import static bjc.utils.PropertyDB.getRegex;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.splitter.TokenSplitter;

/**
 * Utilities useful for operating on PL tokens.
 *
 * @author EVE
 *
 */
public class TokenUtils {
	/**
	 * Simple implementation of TokenSplitter for removing double-quoted
	 * strings.
	 * 
	 * @author EVE
	 *
	 */
	public static class StringTokenSplitter implements TokenSplitter {
		@Override
		public IList<String> split(final String input) {
			return new FunctionalList<>(TokenUtils.removeDQuotedStrings(input));
		}
	}

	/*
	 * Patterns and pattern parts.
	 */
	private static String rPossibleEscapeString = getRegex("possibleStringEscape");

	private static Pattern possibleEscapePatt = Pattern.compile(rPossibleEscapeString);

	private static String	rShortEscape	= getRegex("shortFormStringEscape");
	private static String	rOctalEscape	= getRegex("octalStringEscape");
	private static String	rUnicodeEscape	= getRegex("unicodeStringEscape");

	private static String rEscapeString = applyFormat("stringEscape", rShortEscape, rOctalEscape, rUnicodeEscape);

	private static Pattern escapePatt = Pattern.compile(rEscapeString);

	private static String rDoubleQuoteString = applyFormat("doubleQuotes", getRegex("nonStringEscape"),
			rPossibleEscapeString);

	private static Pattern doubleQuotePatt = Pattern.compile(rDoubleQuoteString);

	private static Pattern quotePatt = getCompiledRegex("unescapedQuote");

	private static Pattern intLitPattern = getCompiledRegex("intLiteral");

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
	public static List<String> removeDQuotedStrings(final String inp) {
		if (inp == null) throw new NullPointerException("inp must not be null");

		/*
		 * What we need for piece-by-piece string building
		 */
		StringBuffer work = new StringBuffer();
		final List<String> res = new LinkedList<>();

		/*
		 * Matcher for proper strings and single quotes.
		 */
		final Matcher mt = doubleQuotePatt.matcher(inp);
		final Matcher corr = quotePatt.matcher(inp);

		if (corr.find() && !corr.find()) {
			/*
			 * There's a unmatched opening quote with no strings.
			 */
			final String msg = String.format(
					"Unclosed string literal '%s'. Opening quote was at position %d", inp,
					inp.indexOf("\""));

			throw new IllegalArgumentException(msg);
		}

		while (mt.find()) {
			/*
			 * Remove the string until the quoted string.
			 */
			mt.appendReplacement(work, "");

			/*
			 * Add the string preceding the double-quoted string and
			 * the double-quoted string to the list.
			 */
			res.add(work.toString());
			res.add(mt.group(1));

			/*
			 * Renew the buffer.
			 */
			work = new StringBuffer();
		}

		/*
		 * Grab the remainder of the string.
		 */
		mt.appendTail(work);
		final String tail = work.toString();

		if (tail.contains("\"")) {
			/*
			 * There's a unmatched opening quote with at least one
			 * string.
			 */
			final String msg = String.format(
					"Unclosed string literal '%s'. Opening quote was at position %d", inp,
					inp.lastIndexOf("\""));

			throw new IllegalArgumentException(msg);
		}

		/*
		 * Only add an empty tail if the string was empty.
		 */
		if (!tail.equals("") || res.isEmpty()) {
			res.add(tail);
		}

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
	public static String descapeString(final String inp) {
		if (inp == null) throw new NullPointerException("inp must not be null");

		/*
		 * Prepare the buffer and escape finder.
		 */
		final StringBuffer work = new StringBuffer();
		final Matcher possibleEscapeFinder = possibleEscapePatt.matcher(inp);
		final Matcher escapeFinder = escapePatt.matcher(inp);

		while (possibleEscapeFinder.find()) {
			if (!escapeFinder.find()) {
				/*
				 * Found a possible escape that isn't actually an
				 * escape.
				 */
				final String msg = String.format("Illegal escape sequence '%s' at position %d",
						possibleEscapeFinder.group(), possibleEscapeFinder.start());

				throw new IllegalArgumentException(msg);
			}

			final String escapeSeq = escapeFinder.group();

			/*
			 * Convert the escape to a string.
			 */
			String escapeRep = "";
			switch (escapeSeq) {
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
				if (escapeSeq.startsWith("u")) {
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

	/*
	 * Handle a unicode codepoint.
	 */
	private static String handleUnicodeEscape(final String seq) {
		try {
			final int codepoint = Integer.parseInt(seq, 16);

			return new String(Character.toChars(codepoint));
		} catch (final IllegalArgumentException iaex) {
			final String msg = String.format("'%s' is not a valid Unicode escape sequence'", seq);

			final IllegalArgumentException reiaex = new IllegalArgumentException(msg);

			reiaex.initCause(iaex);

			throw reiaex;
		}
	}

	/*
	 * Handle a octal codepoint.
	 */
	private static String handleOctalEscape(final String seq) {
		try {
			final int codepoint = Integer.parseInt(seq, 8);

			if (codepoint > 255) {
				final String msg = String
						.format("'%d' is outside the range of octal escapes', codepoint");

				throw new IllegalArgumentException(msg);
			}

			return new String(Character.toChars(codepoint));
		} catch (final IllegalArgumentException iaex) {
			final String msg = String.format("'%s' is not a valid octal escape sequence'", seq);

			final IllegalArgumentException reiaex = new IllegalArgumentException(msg);

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
	public static boolean isDouble(final String inp) {
		return DoubleMatcher.doubleLiteral.matcher(inp).matches();
	}

	/**
	 * Check if a given string would be successfully converted to a integer
	 * by {@link Integer#parseInt(String)}.
	 *
	 * NOTE: This only checks syntax. Using values out of the range of
	 * integers will still cause errors.
	 *
	 * @param inp
	 *                The input to check.
	 * @return Whether the string is a valid integer or not.
	 */
	public static boolean isInt(final String inp) {
		try {
			Integer.parseInt(inp);
			return true;
		} catch (NumberFormatException nfex) {
			return false;
		}
	}
}