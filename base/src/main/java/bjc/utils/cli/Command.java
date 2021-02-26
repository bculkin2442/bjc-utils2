package bjc.utils.cli;

/**
 * Represents a command that can be invoked from a {@link CommandMode}
 *
 * @author ben
 */
public interface Command {
	/**
	 * Get the handler that executes this command
	 *
	 * @return The handler that executes this command
	 */
	CommandHandler getHandler();

	/**
	 * Get the help entry for this command
	 *
	 * @return The help entry for this command
	 */
	CommandHelp getHelp();
    
	/**
     * Create a command that serves as an alias to this one
     *
     * @return A command that serves as an alias to this one
     */
    default Command aliased() {
           return new DelegatingCommand(this);
    };
    
	/**
	 * Check if this command is an alias of another command
	 *
	 * @return Whether or not this command is an alias of another
	 */
	default boolean isAlias() {
		return false;
	}
	
	/**
	 * Create a new basic command.
	 * 
	 * @param summary The summary of the command. This is used as a short help
	 *                message displayed when listing commands.
	 * @param description The description of the command. This is what is shown
	 *                    when the detailed help for a command is asked for.
	 * @param handler The implementation for the command.
	 * 
	 * @return A command with the given implementation.
	 */
	static Command from(
	        String summary, String description, CommandHandler handler)
	{
	    return new GenericCommand(handler, summary, description);
	}
}
