package bjc.utils.parserutils.pratt.commands;

/**
 * The specifier for what to do for each element of a command.
 * 
 * @author EVE
 *
 */
public class InterleaveSpecifier {
	/**
	 * The type of this specifier.
	 * 
	 * @author EVE
	 *
	 */
	public static enum Type {
		/**
		 * Parse an expression with the given priority.
		 */
		EXPRESSION,
		/**
		 * Parse a statement with the given priority.
		 */
		STATEMENT,
		/**
		 * Expect a token of a certain type to be present.
		 */
		EXPECT, LITERAL;
	}
}