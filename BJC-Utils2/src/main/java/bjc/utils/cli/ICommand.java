package bjc.utils.cli;

/**
 * Represents a command that can be invoked from a {@link ICommandMode}
 * 
 * @author ben
 *
 */
public interface ICommand {
	/**
	 * Get the handler that executes this command
	 * 
	 * @return The handler that executes this command
	 */
	public ICommandHandler getHandler();

	/**
	 * Get the help entry for this command
	 * 
	 * @return The help entry for this command
	 */
	public ICommandHelp getHelp();

	/**
	 * Create a command that serves as an alias to this one
	 * 
	 * @return A command that serves as an alias to this one
	 */
	public ICommand createAlias();

	/**
	 * Check if this command is an alias of another command
	 * 
	 * @return Whether or not this command is an alias of another
	 */
	public default boolean isAlias() {
		return false;
	}
}
