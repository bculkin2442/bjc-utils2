package bjc.utils.funcutils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.BreakIterator;

import bjc.utils.ioutils.LevelSplitter;

/** Utility methods for operations on strings.
 *
 * @author ben */
public class StringUtils {
	/**Check if a string consists only of one or more matches of a regular
	 * expression.
	 *
	 * @param input The string to check.
	 * @param rRegex The regex to see if the string only contains matches of.
	 *
	 * @return Whether or not the string consists only of multiple matches of the
	 *         provided regex.
	 */
	public static boolean containsOnly(final String input, final String rRegex) {
		if (input == null)       throw new NullPointerException("Input must not be null");
		else if (rRegex == null) throw new NullPointerException("Regex must not be null");

		/* This regular expression is fairly simple.
		 *
		 * First, we match the beginning of the string. Then, we start a non-capturing
		 * group whose contents are the passed in regex. That group is then matched one
		 * or more times and the pattern matches to the end of the string. */
		return input.matches("\\A(?:" + rRegex + ")+\\Z");
	}

	/** Indent the string being built in a StringBuilder n levels.
	 *
	 * @param builder The builder to indent in.
	 * @param levels The number of levels to indent.
	 */
	public static void indentNLevels(final StringBuilder builder, final int levels) {
		for (int i = 0; i < levels; i++) builder.append("\t");
	}

	/** Print out a deque with a special case for easily showing a deque is empty.
	 *
	 * @param <ContainedType> The type in the deque.
	 *
	 * @param queue The deque to print.
	 * 
	 * @return A string version of the deque, with allowance for an empty deque.
	 */
	public static <ContainedType> String printDeque(final Deque<ContainedType> queue) {
		return queue.isEmpty() ? "(none)" : queue.toString();
	}

	/**
	 * Converts a sequence to an English list.
	 *
	 * @param objects The sequence to convert to an English list.
	 * @param join
	 *     The string to use for separating the last element from the rest.
	 * @param comma The string to use as a comma
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final String join,
			final String comma) {
		if (objects == null)
			throw new NullPointerException("Sequence must not be null");

		final StringBuilder sb = new StringBuilder();

		final String joiner = join;
		final String coma = comma;

		switch (objects.length) {
		case 0:
			/* Empty list. */
			break;
		case 1:
			/* One item. */
			sb.append(objects[0].toString());
			break;
		case 2:
			/* Two items. */
			sb.append(objects[0].toString());
			sb.append(" " + joiner + " ");
			sb.append(objects[1].toString());
			break;
		default:
			/* Three or more items. */
			for (int i = 0; i < objects.length - 1; i++) {
				sb.append(objects[i].toString());
				sb.append(coma + " ");
			}

			/*
			 * Uncomment this to remove serial commas.
			 *
			 * int lc = sb.length() - 1;
			 *
			 * sb.delete(lc - coma.length(), lc);
			 */
			sb.append(joiner + " ");
			sb.append(objects[objects.length - 1].toString());
		}

		return sb.toString();
	}

	/** Converts a sequence to an English list.
	 *
	 * @param objects The sequence to convert to an English list.
	 * @param join
	 *     The string to use for separating the last element from the rest.
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final String join) {
		return toEnglishList(objects, join, ",");
	}

	/** Converts a sequence to an English list.
	 *
	 * @param objects The sequence to convert to an English list.
	 * @param and Whether to use 'and' or 'or'.
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final boolean and) {
		return and ? toEnglishList(objects, "and") : toEnglishList(objects, "or");
	}

	/** Count the number of graphemes in a string.
	 *
	 * @param value The string to check.
	 *
	 * @return The number of graphemes in the string.
	 */
	public static int graphemeCount(final String value) {
		final BreakIterator it = BreakIterator.getCharacterInstance();
		it.setText(value);

		int count = 0;
		while (it.next() != BreakIterator.DONE) count++;

		return count;
	}

	/** Count the number of times a pattern matches in a given string.
	 *
	 * @param value The string to count occurrences in.
	 * @param pattern The pattern to count occurrences of.
	 * 
	 * @return The number of times the pattern matches.
	 */
	public static int countMatches(final String value, final String pattern) {
		Matcher mat = Pattern.compile(pattern).matcher(value);

		int num = 0;
		while (mat.find()) num += 1;

		return num;
	}

	/** Get a substring until a specified string.
	 *
	 * @param strang The string to substring.
	 * @param until The place to substring until.
	 *
	 * @return The specified substring.
	 */
	public static String substringTo(String strang, String until) {
		return substringTo(strang, until, true);
	}

	/**
	 * Get a substring until a specified string.
	 *
	 * @param strang The string to substring.
	 * @param until The place to substring until.
	 * @param allowFail Whether or not to allow failure.
	 *
	 * @return The specified substring, or null if the specified place to substring
	 *         to was not found, and we were not allowed to fail.
	 */
	public static String substringTo(String strang, String until, boolean allowFail) {
		int idx = strang.indexOf(until);

		if (idx == -1) {
			return allowFail ? strang : null;
		}

		return strang.substring(0, strang.indexOf(until));
	}

	private static class LineIterator implements Iterator<String> {
		private Scanner scn;

		public boolean processComments;
		public String commentInd;

		public boolean skipBlanks;

		public LineIterator(Scanner scn) {
			this.scn = scn;
		}

		@Override
		public boolean hasNext() {
			return scn.hasNextLine();
		}

		@Override
		public String next() {
			StringBuilder sb = new StringBuilder();

			String tmp;
			boolean doLoop = true;

			do {
				if (!scn.hasNextLine()) break;

				tmp = scn.nextLine().trim();

				// Skip blank lines
				if (skipBlanks && tmp.equals(""))                  continue;
				if (processComments && tmp.startsWith(commentInd)) continue;

				doLoop = tmp.endsWith("\\") && !tmp.endsWith("\\\\");

				if (doLoop || tmp.endsWith("\\\\")) {
					tmp = tmp.substring(0, tmp.length() - 1);
				}

				sb.append(tmp);
			} while (doLoop);

			return sb.toString();
		}
	}

	/** Read a series of lines from an input source.
	 *
	 * @param scn The source to read the lines from.
	 *
	 * @return An iterator over the lines from the input source.
	 */
	public static Iterator<String> readLines(Scanner scn) {
		return readLines(scn, false, "", false);
	}

	/**
	 * Read a series of lines from an input source.
	 *
	 * @param scn The source to read the lines from.
	 * @param processComments Whether or not to skip comment lines.
	 * @param commentInd Indicator for starting comment lines.
	 * @param skipBlanks Whether or not to skip blank lines.
	 *
	 * @return An iterator over the lines from the input source.
	 */
	public static Iterator<String> readLines(Scanner scn, boolean processComments,
			String commentInd, boolean skipBlanks) {
		LineIterator itr = new LineIterator(scn);

		itr.processComments = processComments;
		itr.commentInd = commentInd;

		itr.skipBlanks = skipBlanks;

		return itr;
	}

	/** Check if a string contains any one of a specified number of things,
	 * respecting groups.
	 *
	 * @param haystack The string to look in.
	 * @param needles The strings to look for.
	 * 
	 * @return Whether or not any of the strings were contained outside of groups.
	 */
	public static boolean levelContains(String haystack, String... needles) {
		return LevelSplitter.def.levelContains(haystack, needles);
	}

	/** Split a string, respecting groups.
	 *
	 * @param phrase The string to split.
	 * @param splits The strings to split on.
	 * 
	 * @return A list of split strings. If keepDelims is true, it also includes the
	 *         delimiters in between the split strings.
	 */
	public static List<String> levelSplit(String phrase, String... splits) {
		return LevelSplitter.def.levelSplit(phrase, false, splits);
	}

	/** Split a string, respecting groups.
	 *
	 * @param phrase The string to split.
	 * @param keepDelims Whether or not to include the delimiters in the results.
	 * @param splits The strings to split on.
	 * 
	 * @return A list of split strings. If keepDelims is true, it also includes the
	 *         delimiters in between the split strings.
	 */
	public static List<String> levelSplit(String phrase, boolean keepDelims,
			String... splits) {
		return LevelSplitter.def.levelSplit(phrase, keepDelims, splits);
	}
	
	/**
	 * Convert a string into a pseudorandom anagram.
	 * 
	 * Works by swapping each character in the string with a random one.
	 * 
	 * @param s The string to convert.
	 * 
	 * @return A pseudo-random anagram
	 */
	public static String strfry(String s) {
		char[] chars = s.toCharArray();
		
		int strlen = chars.length;
		Random rng = new Random();

		for (int i = 0; i < strlen; i++) {
			int randIdx = rng.nextInt(strlen);

			char source = chars[i];
			char dest = chars[randIdx];
			
			chars[i] = dest;
			chars[randIdx] = source;
		}
		
		return String.valueOf(chars);
	}
}