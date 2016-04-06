package bjc.utils.cli;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Runs a CLI interface from the provided set of streams
 * 
 * @author ben
 *
 */
public class CLICommander {
	private InputStream		input;
	private OutputStream	output;
	private OutputStream	error;
	private ICommandMode	initialMode;

	/**
	 * Create a new CLI interface powered by streams
	 * 
	 * @param input
	 *            The stream to get user input from
	 * @param output
	 *            The stream to send user output to
	 * @param error
	 *            The stream to send error messages to
	 */
	public CLICommander(InputStream input, OutputStream output,
			OutputStream error) {
		if (input == null) {
			throw new NullPointerException(
					"Input stream must not be null");
		} else if (output == null) {
			throw new NullPointerException(
					"Output stream must not be null");
		} else if (error == null) {
			throw new NullPointerException(
					"Error stream must not be null");
		}

		this.input = input;
		this.output = output;
		this.error = error;
	}

	/**
	 * Set the initial command mode to use
	 * 
	 * @param initialMode
	 *            The initial command mode to use
	 */
	public void setInitialCommandMode(ICommandMode initialMode) {
		if (initialMode == null) {
			throw new NullPointerException(
					"Initial mode must be non-zero");
		}

		this.initialMode = initialMode;
	}
}
