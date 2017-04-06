package bjc.utils.ioutils;

import bjc.utils.functypes.ID;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Editor methods for strings based off the command language for the Sam editor.
 * 
 * @author EVE
 *
 */
public class RegexStringEditor {
	/**
	 * Replace every occurrence of the pattern with the result of applying
	 * the action to the string matched by the pattern.
	 * 
	 * @param input
	 *                The input string to process.
	 * 
	 * @param patt
	 *                The pattern to match the string against.
	 * 
	 * @param action
	 *                The action to transform matches with.
	 * 
	 * @return The string, with matches replaced with the action.
	 */
	public static String onOccurances(String input, Pattern patt, UnaryOperator<String> action) {
		return occurances(input, patt, ID.id(), action);
	}

	/**
	 * Replace every occurrence between the patterns with the result of
	 * applying the action to the strings between the patterns.
	 * 
	 * @param input
	 *                The input string to process.
	 * 
	 * @param patt
	 *                The pattern to match the string against.
	 * 
	 * @param action
	 *                The action to transform matches with.
	 * 
	 * @return The string, with strings between the matches replaced with
	 *         the action.
	 */
	public static String betweenOccurances(String input, Pattern patt, UnaryOperator<String> action) {
		return occurances(input, patt, action, ID.id());
	}

	/**
	 * Execute actions between and on matches of a regular expression.
	 * 
	 * @param input
	 *                The input string.
	 * 
	 * @param patt
	 *                The pattern to match against the string.
	 * 
	 * @param betweenAction
	 *                The function to execute between matches of the string.
	 * 
	 * @param onAction
	 *                The function to execute on matches of the string.
	 * 
	 * @return The string, with both actions applied.
	 */
	public static String occurances(String input, Pattern patt, UnaryOperator<String> betweenAction,
			UnaryOperator<String> onAction) {
		Matcher matcher = patt.matcher(input);

		StringBuffer res = new StringBuffer();

		StringBuffer work = new StringBuffer();

		while(matcher.find()) {
			String match = matcher.group();

			matcher.appendReplacement(work, "");

			res.append(betweenAction.apply(matcher.toString()));
			res.append(onAction.apply(match));

			work = new StringBuffer();
		}

		matcher.appendTail(work);

		res.append(betweenAction.apply(work.toString()));

		return res.toString();
	}

	/**
	 * Apply an operation to a string if it matches a regular expression.
	 * 
	 * @param input
	 *                The input string.
	 * 
	 * @param patt
	 *                The pattern to match against it.
	 * 
	 * @param action
	 *                The action to execute if it matches.
	 * 
	 * @return The string, modified by the action if the pattern matched.
	 */
	public static String ifMatches(String input, Pattern patt, UnaryOperator<String> action) {
		Matcher matcher = patt.matcher(input);

		if(matcher.matches()) {
			return action.apply(input);
		} else {
			return input;
		}
	}

	/**
	 * Apply an operation to a string if it matches a regular expression.
	 * 
	 * @param input
	 *                The input string.
	 * 
	 * @param patt
	 *                The pattern to match against it.
	 * 
	 * @param action
	 *                The action to execute if it doesn't match.
	 * 
	 * @return The string, modified by the action if the pattern didn't
	 *         match.
	 */
	public static String ifNotMatches(String input, Pattern patt, UnaryOperator<String> action) {
		Matcher matcher = patt.matcher(input);

		if(matcher.matches()) {
			return input;
		} else {
			return action.apply(input);
		}
	}
}