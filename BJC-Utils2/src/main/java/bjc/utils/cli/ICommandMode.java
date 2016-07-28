package bjc.utils.cli;

/**
 * A mode for determining the commands that are valid to enter, and then
 * handling those commands
 * 
 * @author ben
 *
 */
public interface ICommandMode {
	/**
	 * Check to see if this mode can handle the specified command
	 * 
	 * @param command
	 *            The command to check
	 * @return Whether or not this mode can handle the command. It is
	 *         assumed not by default
	 */
	public default boolean canHandle(String command) {
		return false;
	};

	/**
	 * Get the custom prompt for this mode
	 * 
	 * @return the custom prompt for this mode
	 * 
	 * @throws UnsupportedOperationException
	 *             if this mode doesn't support a custom prompt
	 */
	public default String getCustomPrompt() {
		throw new UnsupportedOperationException(
				"This mode doesn't support a custom prompt");
	}

	/**
	 * Get the name of this command mode
	 * 
	 * @return The name of this command mode, which is the empty string by
	 *         default
	 */
	public default String getName() {
		return "";
	}

	/**
	 * Check if this mode uses a custom prompt
	 * 
	 * @return Whether or not this mode uses a custom prompt
	 */
	public default boolean isCustomPromptEnabled() {
		return false;
	}

	/**
	 * Process a command in this mode
	 * 
	 * @param command
	 *            The command to process
	 * @param args
	 *            A list of arguments to the command
	 * @return The command mode to use for the next command. Defaults to
	 *         returning this, and doing nothing else
	 */
	public default ICommandMode process(String command, String[] args) {
		return this;
	}
}
