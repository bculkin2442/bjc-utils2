package bjc.utils.cli;

/**
 * Represents a command that can be invoked from a {@link ICommandMode}
 *
 * @author ben
 *
 */
public interface ICommand {
	/**
	 * Create a command that serves as an alias to this one
	 *
	 * @return A command that serves as an alias to this one
	 */
	ICommand aliased();

	/**
	 * Get the handler that executes this command
	 *
	 * @return The handler that executes this command
	 */
	ICommandHandler getHandler();

	/**
	 * Get the help entry for this command
	 *
	 * @return The help entry for this command
	 */
	ICommandHelp getHelp();

	/**
	 * Check if this command is an alias of another command
	 *
	 * @return Whether or not this command is an alias of another
	 */
	default boolean isAlias() {
		return false;
	}
}
