package bjc.utils.cli;

/**
 * Represents a command that can be invoked from a {@link CommandMode}
 *
 * @author ben
 */
public interface Command {
	/**
	 * Create a command that serves as an alias to this one
	 *
	 * @return 
	 * 	A command that serves as an alias to this one
	 */
	Command aliased();

	/**
	 * Get the handler that executes this command
	 *
	 * @return
	 * 	The handler that executes this command
	 */
	CommandHandler getHandler();

	/**
	 * Get the help entry for this command
	 *
	 * @return
	 * 	The help entry for this command
	 */
	CommandHelp getHelp();

	/**
	 * Check if this command is an alias of another command
	 *
	 * @return
	 * 	Whether or not this command is an alias of another
	 */
	default boolean isAlias() {
		return false;
	}
}
