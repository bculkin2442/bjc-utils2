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
 */
public class CLICommander {
	/* The streams used for input and normal/error output. */
	private final InputStream input;
	private final OutputStream output;
	private final OutputStream error;

	/* The command mode to start execution in. */
	private CommandMode initialMode;

	/**
	 * Create a new CLI interface powered by streams.
	 *
	 * @param input
	 *               The stream to get user input from.
	 *
	 * @param output
	 *               The stream to send normal output to.
	 *
	 * @param error
	 *               The stream to send error output to.
	 */
	public CLICommander(final InputStream input, final OutputStream output,
			final OutputStream error) {
		if (input == null)
			throw new NullPointerException("Input stream must not be null");
		else if (output == null)
			throw new NullPointerException("Output stream must not be null");
		else if (error == null)
			throw new NullPointerException("Error stream must not be null");

		this.input = input;
		this.output = output;
		this.error = error;
	}

	/** Start handling commands from the given input stream. */
	public void runCommands() {
		/* Setup output streams. */
		final PrintStream normalOutput = new PrintStream(output);
		final PrintStream errorOutput = new PrintStream(error);

		/*
		 * Set up input streams.
		 *
		 * We're suppressing the warning about a potentially leaked resource because we
		 * might use the input stream multiple times.
		 */
		@SuppressWarnings("resource")
		final Scanner inputSource = new Scanner(input);

		/*
		 * The mode currently being used to handle commands.
		 *
		 * Used to preserve the initial mode, so that a mode can be invoked more than
		 * once.
		 */
		CommandMode currentMode = initialMode;

		/* The number of the command we are executing. */
		int comno = 1;
		/*
		 * Process commands until we're told to stop, by the mode being set to null.
		 */
		while (currentMode != null) {
			/*
			 * Print out the command prompt.
			 *
			 * Use a custom prompt if one is specified.
			 */
			if (currentMode.isCustomPromptEnabled()) {
				normalOutput.print(currentMode.getCustomPrompt());
			} else {
				normalOutput.printf("%s (%d)>> ", currentMode.getName(), comno);

				comno += 1;
			}

			/* Read in a command. */
			final String currentLine = inputSource.nextLine();

			/* Handle commands we can handle in this mode. */
			if (currentMode.canHandle(currentLine)) {
				final String[] commandTokens = currentLine.split(" ");
				String[] commandArgs = null;

				final int argCount = commandTokens.length;

				/* Parse args if they are present. */
				if (argCount > 1) {
					commandArgs = Arrays.copyOfRange(commandTokens, 1, argCount);
				}

				/* Process command. */
				currentMode = currentMode.process(commandTokens[0], commandArgs);
			} else {
				errorOutput.printf("Error: Unrecognized command '%s' (no. %d)\n",
						currentLine, comno);
			}
		}

		normalOutput.printf("Exiting now (ran %d commands).\n", comno);
	}

	/**
	 * Set the initial command mode to use.
	 *
	 * @param initialMode
	 *                    The initial command mode to use.
	 */
	public void setInitialCommandMode(final CommandMode initialMode) {
		if (initialMode == null)
			throw new NullPointerException("Initial mode must be non-null");

		this.initialMode = initialMode;
	}
}
