package bjc.utils.cli;

/**
 * Interface for the help entry for a command
 *
 * @author ben
 *
 */
public interface CommandHelp {
	/**
	 * Get the description of a command.
	 *
	 * @return The description of a command
	 */
	String getDescription();

	/**
	 * Get the summary line for a command.
	 *
	 * A summary line should consist of a string of the following format
	 * <pre>"&lt;command-name>\t&lt;command-summary>"</pre> where anything in angle brackets
	 * should be filled in.
	 *
	 * @return The summary line line for a command
	 */
	String getSummary();
}
