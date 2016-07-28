package bjc.utils.cli;

/**
 * Generic command implementation
 * 
 * @author ben
 *
 */
public class GenericCommand implements ICommand {
	// The behavior for invoking the command
	private ICommandHandler	handler;
	// The help for the command
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
		if (handler == null) {
			throw new NullPointerException(
					"Command handler must not be null");
		}

		this.handler = handler;
		this.help = new GenericHelp(description, help);
	}

	@Override
	public ICommand aliased() {
		return new DelegatingCommand(this);
	}

	@Override
	public ICommandHandler getHandler() {
		return handler;
	}

	@Override
	public ICommandHelp getHelp() {
		return help;
	}

	@Override
	public boolean isAlias() {
		return false;
	}
}