package bjc.utils.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

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
	 * Run a set of commands through this commander
	 */
	public void runCommands() {
		PrintStream normalOutput = new PrintStream(output);
		PrintStream errorOutput = new PrintStream(error);

		@SuppressWarnings("resource")
		// We might use this stream multiple times. Don't close it
		Scanner inputSource = new Scanner(input);

		ICommandMode currentMode = initialMode;

		while (currentMode != null) {
			if (currentMode.useCustomPrompt()) {
				normalOutput.print(currentMode.getCustomPrompt());
			} else {
				normalOutput.print(currentMode.getName() + ">> ");
			}

			String currentLine = inputSource.nextLine();

			if (currentMode.canHandleCommand(currentLine)) {
				String[] commandTokens = currentLine.split(" ");

				String[] commandArgs;

				if (commandTokens.length > 1) {
					commandArgs = Arrays.copyOfRange(commandTokens, 1,
							commandTokens.length);
				} else {
					commandArgs = null;
				}

				currentMode = currentMode.processCommand(commandTokens[0],
						commandArgs);
			} else {
				errorOutput.print(
						"Error: Unrecognized command " + currentLine);
			}
		}

		normalOutput.print("Exiting now.");
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