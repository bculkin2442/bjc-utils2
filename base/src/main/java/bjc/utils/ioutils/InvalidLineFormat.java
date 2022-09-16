package bjc.utils.ioutils;

/**
 * Exception thrown when a line is formattted incorrectly.
 * @author Ben Culkin
 *
 */
public class InvalidLineFormat extends RuntimeException {
	private static final long serialVersionUID = 5332131472090792841L;

	/**
	 * Create a new exception for an incorrectly formatted line.
	 * @param lne The line that was incorrectly formatted.
	 */
	public InvalidLineFormat(String lne) {
		super(String.format(
				"Line '%s' is improperly formatted.\n\tExpected format is a string key, followed by a single space, followed by the value",
				""));
	}
}