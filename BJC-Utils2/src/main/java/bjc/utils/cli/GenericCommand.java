package bjc.utils.cli;

/**
 * Generic command implementation
 * 
 * @author ben
 *
 */
public class GenericCommand implements ICommand {
	private static class DelegatingCommand implements ICommand {
		private ICommand delegate;

		public DelegatingCommand(ICommand delegate) {
			this.delegate = delegate;
		}

		@Override
		public ICommandHandler getHandler() {
			return delegate.getHandler();
		}

		@Override
		public ICommandHelp getHelp() {
			return delegate.getHelp();
		}

		@Override
		public ICommand createAlias() {
			return new DelegatingCommand(delegate);
		}

		@Override
		public boolean isAlias() {
			return true;
		}
	}

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

	@Override
	public boolean isAlias() {
		return false;
	}

	/**
	 * Create a command that is an alias to this one
	 * 
	 * @return A command that is an alias to this one
	 */
	@Override
	public ICommand createAlias() {
		return new DelegatingCommand(this);
	}
}
