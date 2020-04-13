package bjc.utils.exceptions;

/**
 * Exception thrown when an invalid token is found.
 * 
 * @author Ben Culkin
 *
 */
public class InvalidToken extends RuntimeException {
	private static final long serialVersionUID = -5077165766341244689L;

	/**
	 * Create an invalid token exception.
	 * 
	 * @param tok
	 *            The token that was invalid.
	 */
	public InvalidToken(String tok) {
		super(String.format("Did not recognize token '%s' as a valid token", tok));
	}
}