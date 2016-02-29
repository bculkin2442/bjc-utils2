package bjc.utils.gui;

import java.io.IOException;

/**
 * Represents the user failing to choose a file.
 * 
 * @author ben
 *
 */
public class FileNotChosenException extends IOException {
	private static final long serialVersionUID = -8753348705210831096L;

	public FileNotChosenException() {
		super();
	}
	
	/**
	 * Create a new exception with the given cause
	 * 
	 * @param cause
	 *            The cause of why the exception was thrown
	 */
	public FileNotChosenException(String cause) {
		super(cause);
	}
}
