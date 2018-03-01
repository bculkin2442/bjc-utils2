package bjc.utils.ioutils;

import java.io.PrintStream;

import bjc.utils.ioutils.blocks.TriggeredBlockReader;

/**
 * A runnable for use with {@link TriggeredBlockReader} to prompt the user for
 * input.
 *
 * @author bjculkin
 *
 */
public final class Prompter implements Runnable {
	private String promt;
	private final PrintStream printer;

	/**
	 * Create a new prompter using the specified prompt.
	 *
	 * @param prompt
	 *        The prompt to present.
	 *
	 * @param output
	 *        The stream to print the prompt on.
	 */
	public Prompter(final String prompt, final PrintStream output) {
		promt = prompt;

		printer = output;
	}

	/**
	 * Set the prompt this prompter uses.
	 *
	 * @param prompt
	 *        The prompt this prompter uses.
	 */
	public void setPrompt(final String prompt) {
		promt = prompt;
	}

	@Override
	public void run() {
		printer.print(promt);
	}
}