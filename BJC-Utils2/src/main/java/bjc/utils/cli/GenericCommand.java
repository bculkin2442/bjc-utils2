package bjc.utils.cli;

/**
 * Generic command implementation.
 *
 * @author ben
 *
 */
public class GenericCommand implements ICommand {
	/*
	 * The behavior for invoking the command.
	 */
	private ICommandHandler handler;

	/*
	 * The help for the command.
	 */
	private ICommandHelp help;

	/**
	 * Create a new generic command.
	 *
	 * @param handler
	 *                The handler to use for the command.
	 * @param description
	 *                The description of the command. May be null, in which
	 *                case a default is provided.
	 * @param help
	 *                The detailed help message for the command. May be
	 *                null, in which case the description is repeated for
	 *                the detailed help.
	 */
	public GenericCommand(ICommandHandler handler, String description, String help) {
		if(handler == null) throw new NullPointerException("Command handler must not be null");

		this.handler = handler;

		if(description == null) {
			this.help = new NullHelp();
		} else {
			this.help = new GenericHelp(description, help);
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenericCommand [");

		if(help != null) {
			builder.append("help=");
			builder.append(help);
		}

		builder.append("]");

		return builder.toString();
	}
}
