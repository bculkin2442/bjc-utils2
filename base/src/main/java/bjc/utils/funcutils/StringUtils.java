package bjc.utils.funcutils;

import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.BreakIterator;

/**
 * Utility methods for operations on strings
 *
 * @author ben
 *
 */
public class StringUtils {
	/**
	 * Check if a string consists only of one or more matches of a regular
	 * expression
	 *
	 * @param input
	 *                The string to check
	 * @param rRegex
	 *                The regex to see if the string only contains matches
	 *                of
	 * @return Whether or not the string consists only of multiple matches
	 *         of the provided regex
	 */
	public static boolean containsOnly(final String input, final String rRegex) {
		if (input == null)
			throw new NullPointerException("Input must not be null");
		else if (rRegex == null) throw new NullPointerException("Regex must not be null");

		/*
		 * This regular expression is fairly simple.
		 *
		 * First, we match the beginning of the string. Then, we start a
		 * non-capturing group whose contents are the passed in regex.
		 * That group is then matched one or more times and the pattern
		 * matches to the end of the string
		 */
		return input.matches("\\A(?:" + rRegex + ")+\\Z");
	}

	/**
	 * Indent the string being built in a StringBuilder n levels
	 *
	 * @param builder
	 *                The builder to indent in
	 * @param levels
	 *                The number of levels to indent
	 */
	public static void indentNLevels(final StringBuilder builder, final int levels) {
		for (int i = 0; i < levels; i++) {
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
	public static <ContainedType> String printDeque(final Deque<ContainedType> queue) {
		return queue.isEmpty() ? "(none)" : queue.toString();
	}

	/**
	 * Converts a sequence to an English list.
	 *
	 * @param objects
	 *                The sequence to convert to an English list.
	 * @param join
	 *                The string to use for separating the last element from
	 *                the rest.
	 * @param comma
	 *                The string to use as a comma
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final String join, final String comma) {
		if (objects == null) throw new NullPointerException("Sequence must not be null");

		final StringBuilder sb = new StringBuilder();

		final String joiner = join;
		final String coma = comma;

		switch (objects.length) {
		case 0:
			/*
			 * Empty list.
			 */
			break;
		case 1:
			/*
			 * One item.
			 */
			sb.append(objects[0].toString());
			break;
		case 2:
			/*
			 * Two items.
			 */
			sb.append(objects[0].toString());
			sb.append(" " + joiner + " ");
			sb.append(objects[1].toString());
			break;
		default:
			/*
			 * Three or more items.
			 */
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

	/**
	 * Converts a sequence to an English list.
	 *
	 * @param objects
	 *                The sequence to convert to an English list.
	 * @param join
	 *                The string to use for separating the last element from
	 *                the rest.
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final String join) {
		return toEnglishList(objects, join, ",");
	}

	/**
	 * Converts a sequence to an English list.
	 *
	 * @param objects
	 *                The sequence to convert to an English list.
	 * @param and
	 *                Whether to use 'and' or 'or'.
	 *
	 * @return The sequence as an English list.
	 */
	public static String toEnglishList(final Object[] objects, final boolean and) {
		if (and)
			return toEnglishList(objects, "and");
		else return toEnglishList(objects, "or");
	}

	/**
	 * Count the number of graphemes in a string.
	 *
	 * @param value
	 *                The string to check.
	 *
	 * @return The number of graphemes in the string.
	 */
	public static int graphemeCount(final String value) {
		final BreakIterator it = BreakIterator.getCharacterInstance();
		it.setText(value);

		int count = 0;
		while (it.next() != BreakIterator.DONE) {
			count++;
		}

		return count;
	}

	public static int countMatches(final String value, final String pattern) {
		Matcher mat = Pattern.compile(pattern).matcher(value);
		
		int num = 0;
		while(mat.find())
			num += 1;

		return num;
	}
}
