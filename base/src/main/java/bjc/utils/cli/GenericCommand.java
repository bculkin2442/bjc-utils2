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

	/** Create a new generic command.
	 *
	 * @param handler The handler to use for the command.
	 * @param summary The summary of the command. May be null, in which case a
	 *                default is provided.
	 * @param description The detailed help message for the command. May be null,
	 *                    in which case the summary is repeated for the 
	 *                    detailed help. */
	public GenericCommand(final CommandHandler handler, final String summary,
			final String description) {
		if (handler == null)
			throw new NullPointerException("Command handler must not be null");

		this.handler = handler;

		if (summary == null) this.help = new NullHelp();
        else                     this.help = new GenericHelp(summary, description);
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
	public String toString() {
		return String.format("GenericCommand [help=%s]", help);
	}
}
