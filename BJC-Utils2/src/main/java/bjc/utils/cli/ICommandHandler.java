package bjc.utils.cli;

import java.util.function.Function;

/**
 * A handler for a command
 * 
 * @author ben
 *
 */
@FunctionalInterface
public interface ICommandHandler extends Function<String[], ICommandMode> {
	/**
	 * Execute this command
	 * 
	 * @param args
	 *            The arguments for this command
	 * @return The command mode to switch to after this command, or null to
	 *         stop executing commands
	 */
	public default ICommandMode handle(String[] args) {
		return this.apply(args);
	}
}
