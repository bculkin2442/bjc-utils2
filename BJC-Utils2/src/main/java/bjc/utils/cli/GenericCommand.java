package bjc.utils.cli;

import org.eclipse.jdt.annotation.Nullable;

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
	 *            The description of the command. May be null
	 * @param help
	 *            The detailed help message for the command. May be null
	 */
	public GenericCommand(ICommandHandler handler, @Nullable String description,
			@Nullable String help) {
		if (handler == null) {
			throw new NullPointerException(
					"Command handler must not be null");
		}

		this.handler = handler;

		if (description == null) {
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
}