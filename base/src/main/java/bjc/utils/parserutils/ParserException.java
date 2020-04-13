package bjc.utils.parserutils;

/**
 * General superclass for exceptions thrown during parsing.
 *
 * @author EVE
 *
 */
public class ParserException extends Exception {
	private static final long serialVersionUID = 631298568113373233L;

	/**
	 * Create a new exception with the provided message.
	 *
	 * @param msg
	 *            The message for the exception.
	 */
	public ParserException(final String msg) {
		super(msg);
	}

	/**
	 * Create a new exception with the provided message and cause.
	 *
	 * @param msg
	 *              The message for the exception.
	 * @param cause
	 *              The cause of the exception.
	 */
	public ParserException(final String msg, final Exception cause) {
		super(msg, cause);
	}
}
