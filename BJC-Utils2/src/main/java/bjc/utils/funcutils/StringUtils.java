package bjc.utils.funcutils;

import java.util.Deque;

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
	 *            The string to check
	 * @param regex
	 *            The regex to see if the string only contains matches of
	 * @return Whether or not the string consists only of multiple matches
	 *         of the provided regex
	 */
	public static boolean containsOnly(String input, String regex) {
		/*
		 * This regular expression is fairly simple.
		 * 
		 * First, we match the beginning of the string. Then, we start a
		 * non-capturing group whose contents are the passed in regex. That
		 * group is then matched one or more times and the pattern matches
		 * to the end of the string
		 */
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (regex == null) {
			throw new NullPointerException("Regex must not be null");
		}

		return input.matches("\\A(?:" + regex + ")+\\Z");
	}

	/**
	 * Checks if the given expression contains the specified operator in a
	 * situation that indicates its use as an infix operator.
	 * 
	 * @param expression
	 *            The expression to check
	 * @param operator
	 *            The operator to see if it is contained
	 * @return Whether or not the given expression contains the specified
	 *         operator as a infix operator
	 */
	public static boolean containsInfixOperator(String expression,
			String operator) {
		// Bit annoying to have to use a full class name, but what are you
		// going to do?
		return org.apache.commons.lang3.StringUtils
				.countMatches(expression, operator) == 1
				&& !expression.equalsIgnoreCase(operator)
				&& !expression.startsWith(operator);
	}

	/**
	 * Indent the string being built in a StringBuilder n levels
	 * 
	 * @param builder
	 *            The builder to indent in
	 * @param levels
	 *            The number of levels to indent
	 */
	public static void indentNLevels(StringBuilder builder, int levels) {
		for (int i = 0; i < levels; i++) {
			builder.append("\t");
		}
	}

	public static <ContainedType> String printDeque(Deque<ContainedType> queuedTrees) {
		return queuedTrees.isEmpty() ? "(none)" : queuedTrees.toString();
	}
}
