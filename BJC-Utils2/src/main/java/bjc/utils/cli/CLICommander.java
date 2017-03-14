package bjc.utils.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Runs a CLI interface from the provided set of streams.
 *
 * @author ben
 *
 */
public class CLICommander {
	/*
	 * The streams used for input and normal/error output
	 */
	private InputStream	input;
	private OutputStream	output;
	private OutputStream	error;

	/*
	 * The command mode to start execution in
	 */
	private ICommandMode initialMode;

	/**
	 * Create a new CLI interface powered by streams.
	 *
	 * @param input
	 *                The stream to get user input from.
	 * @param output
	 *                The stream to send normal output to.
	 * @param error
	 *                The stream to send error output to.
	 */
	public CLICommander(InputStream input, OutputStream output, OutputStream error) {
		if(input == null)
			throw new NullPointerException("Input stream must not be null");
		else if(output == null)
			throw new NullPointerException("Output stream must not be null");
		else if(error == null) throw new NullPointerException("Error stream must not be null");

		this.input = input;
		this.output = output;
		this.error = error;
	}

	/**
	 * Start handling commands from the given input stream.
	 */
	public void runCommands() {
		// Setup output streams
		PrintStream normalOutput = new PrintStream(output);
		PrintStream errorOutput = new PrintStream(error);

		/*
		 * Set up input streams.
		 *
		 * We're suppressing the warning because we might use the input
		 * stream multiple times
		 */
		@SuppressWarnings("resource")
		Scanner inputSource = new Scanner(input);

		/*
		 * The mode currently being used to handle commands.
		 *
		 * Used to preserve the initial mode
		 */
		ICommandMode currentMode = initialMode;

		// Process commands until we're told to stop
		while(currentMode != null) {
			/*
			 * Print out the command prompt, using a custom prompt
			 * if one is specified
			 */
			if(currentMode.isCustomPromptEnabled()) {
				normalOutput.print(currentMode.getCustomPrompt());
			} else {
				normalOutput.print(currentMode.getName() + ">> ");
			}

			// Read in a command
			String currentLine = inputSource.nextLine();

			// Handle commands we can handle
			if(currentMode.canHandle(currentLine)) {
				String[] commandTokens = currentLine.split(" ");
				String[] commandArgs = null;

				// Parse args if they are present
				if(commandTokens.length > 1) {
					commandArgs = Arrays.copyOfRange(commandTokens, 1, commandTokens.length);
				}

				// Process command
				currentMode = currentMode.process(commandTokens[0], commandArgs);
			} else {
				errorOutput.print("Error: Unrecognized command " + currentLine);
			}
		}

		normalOutput.print("Exiting now.");
	}

	/**
	 * Set the initial command mode to use
	 *
	 * @param initialMode
	 *                The initial command mode to use
	 */
	public void setInitialCommandMode(ICommandMode initialMode) {
		if(initialMode == null) throw new NullPointerException("Initial mode must be non-zero");

		this.initialMode = initialMode;
	}
}
