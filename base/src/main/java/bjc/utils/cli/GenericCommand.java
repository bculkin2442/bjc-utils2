package bjc.utils.cli;

/**
 * Generic command implementation.
 *
 * @author ben
 */
public class GenericCommand implements Command {
	/* The behavior for invoking the command. */
	private final CommandHandler handler;

	/* The help for the command. */
	private CommandHelp help;

	/**
	 * Create a new generic command.
	 *
	 * @param handler
	 *                    The handler to use for the command.
	 *
	 * @param description
	 *                    The description of the command. May be null, in which case
	 *                    a default is provided.
	 *
	 * @param help
	 *                    The detailed help message for the command. May be null, in
	 *                    which case the description is repeated for the detailed
	 *                    help.
	 */
	public GenericCommand(final CommandHandler handler, final String description,
			final String help) {
		if (handler == null)
			throw new NullPointerException("Command handler must not be null");

		this.handler = handler;

		if (description == null) {
			this.help = new NullHelp();
		} else {
			this.help = new GenericHelp(description, help);
		}
	}

	@Override
	public Command aliased() {
		return new DelegatingCommand(this);
	}

	@Override
	public CommandHandler getHandler() {
		return handler;
	}

	@Override
	public CommandHelp getHelp() {
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
		return String.format("GenericCommand [help=%s]", help);
	}
}
