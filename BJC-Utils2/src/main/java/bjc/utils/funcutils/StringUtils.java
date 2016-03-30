package bjc.utils.funcutils;

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
		return input.matches("\\A(?:" + regex + ")+\\Z");
	}

}
