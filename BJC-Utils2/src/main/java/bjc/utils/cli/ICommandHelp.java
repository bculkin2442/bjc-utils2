package bjc.utils.cli;

/**
 * Interface for the help entry for a command
 * 
 * @author ben
 *
 */
public interface ICommandHelp {
	/**
	 * Get the description of a command
	 * 
	 * @return The description of a command
	 */
	public String getDescription();

	/**
	 * Get the summary line for a command
	 * 
	 * Used for 'help commands' which gives the user a brief idea what all
	 * the commands do
	 * 
	 * @return The summary line line for a command
	 */
	public String getSummary();
}
