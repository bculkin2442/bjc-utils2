package bjc.utils.exceptions;

import java.util.InputMismatchException;

/**
 * The exception to throw whenever a pragma is used with invalid syntax.
 *
 * @author ben
 */
public class BadPragmaFormat extends InputMismatchException {
	/* Version ID for serialization. */
	private static final long serialVersionUID = 1288536477368021069L;

	/** Create a new exception */
	public BadPragmaFormat() {
		super();
	}

	/**
	 * Create a new exception with the given message.
	 *
	 * @param message
	 *                The message to explain why the exception was thrown.
	 */
	public BadPragmaFormat(final String message) {
		super(message);
	}
}
