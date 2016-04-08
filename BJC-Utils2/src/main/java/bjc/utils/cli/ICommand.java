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
}
