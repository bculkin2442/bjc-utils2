package bjc.utils.cli;

/**
 * Interface for the help entry for a command
 * 
 * @author ben
 *
 */
public interface ICommandHelp {
	/**
	 * Get the description of a command.
	 * 
	 * @return The description of a command
	 */
	public String getDescription();

	/**
	 * Get the summary line for a command.
	 * 
	 * A summary line should consist of a string of the following format
	 * "<command-name>\t<command-summary>" where anything in angle brackets
	 * should be filled in.
	 * 
	 * @return The summary line line for a command
	 */
	public String getSummary();
}
