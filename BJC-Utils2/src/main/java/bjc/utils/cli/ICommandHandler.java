package bjc.utils.cli;

import java.util.function.Function;

/**
 * A handler for a command
 * 
 * @author ben
 *
 */
public interface ICommandHandler extends Function<String[], ICommandMode> {
	/**
	 * Handle the command this handler handles
	 * 
	 * @param args
	 *            The arguments for this command
	 * @return The command mode to go to after this command
	 */
	public default ICommandMode handle(String[] args) {
		return this.apply(args);
	}
}
