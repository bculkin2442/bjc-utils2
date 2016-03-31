package bjc.utils.exceptions;

import java.util.InputMismatchException;

/**
 * Represents a error from encountering a unknown pragma
 * 
 * @author ben
 *
 */
public class UnknownPragmaException extends InputMismatchException {
	// Version ID for serialization
	private static final long serialVersionUID = -4277573484926638662L;

	/**
	 * Create a new exception with the given cause
	 * 
	 * @param cause
	 *            The cause for throwing this exception
	 */
	public UnknownPragmaException(String cause) {
		super(cause);
	}

}
