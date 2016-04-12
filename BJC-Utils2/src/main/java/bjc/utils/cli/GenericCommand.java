package bjc.utils.cli;

/**
 * Generic command implementation
 * 
 * @author ben
 *
 */
public class GenericCommand implements ICommand {
	private ICommandHandler	handler;
	private ICommandHelp	help;

	/**
	 * Create a new generic command
	 * 
	 * @param handler
	 *            The handler to use for the command
	 * @param description
	 *            The description of the command
	 * @param help
	 *            The detailed help message for the command
	 */
	public GenericCommand(ICommandHandler handler, String description,
			String help) {
		this.handler = handler;
		this.help = new GenericHelp(description, help);
	}

	@Override
	public ICommandHandler getHandler() {
		return handler;
	}

	@Override
	public ICommandHelp getHelp() {
		return help;
	}
}
