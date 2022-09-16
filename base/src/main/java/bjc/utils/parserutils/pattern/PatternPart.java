package bjc.utils.parserutils.pattern;

import java.util.function.Supplier;
import java.util.regex.*;

/**
 * Builder interface for regex patterns.
 * 
 * Note that you may need to add explicit non-grouping to get things to work
 * right, based on the precedence of your regex operators.
 * 
 * @author bjculkin
 *
 */
public interface PatternPart {
	/**
	 * Convert this pattern part into a regex.
	 * 
	 * @return The regex this part represents.
	 */
	public String toRegex();

	/**
	 * Can this regex be optimized?
	 * 
	 * @return Whether or not this regex can be optimized to a string
	 */
	public boolean canOptimize();

	/**
	 * Create a new pattern part from component bits.
	 * 
	 * @param canOptimize Whether this part can be optimized
	 * @param func        The function that provides the regex text
	 * 
	 * @return A pattern part w/ the given body
	 */
	static PatternPart part(boolean canOptimize, Supplier<String> func) {
		return new FunctionalPatternPart(func, canOptimize);
	}

	/**
	 * Create a new variable pattern part.
	 * 
	 * @param source The function which supplies the text.
	 * 
	 * @return A part that retrieves its bits from the given source
	 */
	static PatternPart var(Supplier<String> source) {
		return part(false, source);
	}

	/**
	 * Create a 'raw' pattern part, which just echoes the given string.
	 * 
	 * @param str The regex to include
	 * 
	 * @return A pattern part which converts to the given string.
	 */
	static PatternPart raw(String str) {
		return part(true, () -> str);
	}

	/**
	 * Create a pattern composed of other patterns, interspersed with the given
	 * string.
	 * 
	 * @param joiner The string to use as a joiner.
	 * @param parts  The composed pattern parts.
	 * 
	 * @return The given patterns composed by the parts, joined by `joiner`.
	 */
	static PatternPart joining(String joiner, PatternPart... parts) {
		return new JoinerPatternPart(parts, joiner);
	}

	/**
	 * Create a pattern part which matches the given string.
	 * 
	 * @param str The string to match
	 * 
	 * @return A pattern which matches the given string.
	 */
	static PatternPart literal(String str) {
		return part(true, () -> Pattern.quote(str));
	}

	/**
	 * Create a pattern part which matches a single digit.
	 * 
	 * @return A pattern that matches a digit.
	 */
	static PatternPart digit() {
		return raw("\\d");
	}

	/**
	 * Create a character class pattern
	 * 
	 * @param chars The characters that make up the class.
	 * 
	 * @return A pattern representing the character class.
	 */
	static PatternPart cclass(char... chars) {
		return part(true, () -> {
			StringBuilder sb = new StringBuilder("[");
			for (char ch : chars)
				sb.append(ch);
			sb.append("]");
			return sb.toString();
		});
	}

	/**
	 * Represents an inverted character class.
	 * 
	 * @param chars The characters for the class not to include.
	 * 
	 * @return A pattern representing a class that doesn't match the characters.
	 */
	static PatternPart notCClass(char... chars) {
		return part(true, () -> {
			StringBuilder sb = new StringBuilder("[^");
			for (char ch : chars)
				sb.append(ch);
			sb.append("]");
			return sb.toString();
		});
	}

	/**
	 * Represents a pattern that matches any non-space character.
	 * 
	 * @return A pattern that matches any non-space character.
	 */
	static PatternPart nonspace() {
		return raw("\\S");
	}

	/**
	 * Concatenate a series of pattern parts with whitespace.
	 * 
	 * @param parts The parts to join
	 * 
	 * @return A pattern that matches each of the given parts, separated by
	 *         whitespace.
	 */
	static PatternPart concat(PatternPart... parts) {
		return joining(" ", parts);
	}

	/**
	 * Create a pattern which matches one of the given patterns.
	 * 
	 * @param parts The possible patterns to match.
	 * 
	 * @return A pattern which matches one of the given patterns.
	 */
	static PatternPart alternate(PatternPart... parts) {
		return joining("|", parts);
	}

	/**
	 * Create a pattern which matches the given pattern zero or more times.
	 * 
	 * @param part The pattern to repeat
	 * 
	 * @return A pattern which matches the given one zero or more times.
	 */
	static PatternPart repeat(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "*");
	}

	/**
	 * Create a pattern which matches the given one zero or more times.
	 * 
	 * @param part The pattern to be optional.
	 * 
	 * @return A pattern where the part is optional
	 */
	static PatternPart optional(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "?");
	}

	/**
	 * Create a pattern which matches the given pattern one or more times.
	 * 
	 * @param part The pattern to repeat.
	 * 
	 * @return A pattern which matches the given one one or more times.
	 */
	static PatternPart repeatAtLeastOnce(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "*");
	}

	/**
	 * Surround the given pattern with strings.
	 * 
	 * @param lhs The left-hand side of the pattern.
	 * @param rhs The right-hand side of the pattern.
	 * @param part The pattern to match.
	 * 
	 * @return A pattern surrounded by the given strings.
	 */
	static PatternPart surround(String lhs, String rhs, PatternPart part) {
		return part(part.canOptimize(), () -> lhs + part.toRegex() + rhs);
	}

	/**
	 * Wrap the given pattern in a capturing group.
	 * 
	 * @param part The pattern to wrap.
	 * 
	 * @return The pattern, wrapped in a capturing group
	 */
	static PatternPart group(PatternPart part) {
		return surround("(", ")", part);
	}

	/**
	 * Wrap the given pattern in a named-capturing group.
	 * 
	 * @param groupName The name of the group
	 * @param part The pattern to wrap.
	 * 
	 * @return A pattern wrap in a named-capturing group.
	 */
	static PatternPart namedGroup(String groupName, PatternPart part) {
		return surround("(<" + groupName + ">", ")", part);
	}

	/**
	 * Wrap the given pattern in a non-capturing group.
	 * 
	 * @param part The pattern to wrap.
	 * 
	 * @return A pattern wrap in a non-capturing group.
	 */
	static PatternPart nonCaptureGroup(PatternPart part) {
		return surround("(?:", ")", part);
	}
}
