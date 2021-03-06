package bjc.utils.exceptions;

import java.io.IOException;

/**
 * Represents the user failing to choose a file.
 *
 * @author ben
 */
public class NoFileChosen extends IOException {
	/* Version ID for serialization. */
	private static final long serialVersionUID = -8753348705210831096L;

	/** Create a new exception. */
	public NoFileChosen() {
		super();
	}

	/**
	 * Create a new exception with the given cause.
	 *
	 * @param cause
	 *              The cause of why the exception was thrown.
	 */
	public NoFileChosen(final String cause) {
		super(cause);
	}
}
