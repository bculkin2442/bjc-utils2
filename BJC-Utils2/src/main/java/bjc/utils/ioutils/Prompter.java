package bjc.utils.ioutils;

import java.io.PrintStream;

/**
 * A runnable for use with {@link TriggeredBlockReader} to prompt the user for
 * input.
 * 
 * @author bjculkin
 *
 */
public final class Prompter implements Runnable {
	private String		promt;
	private PrintStream	printer;

	/**
	 * Create a new prompter using the specified prompt.
	 * 
	 * @param prompt
	 *                The prompt to present.
	 * 
	 * @param output
	 *                The stream to print the prompt on.
	 */
	public Prompter(String prompt, PrintStream output) {
		promt = prompt;

		printer = output;
	}

	/**
	 * Set the prompt this prompter uses.
	 * 
	 * @param prompt
	 *                The prompt this prompter uses.
	 */
	public void setPrompt(String prompt) {
		promt = prompt;
	}

	@Override
	public void run() {
		printer.print(promt);
	}
}