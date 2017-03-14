package bjc.utils.cli;

/**
 * A class for a command that delegates to another command.
 *
 * @author ben
 *
 */
class DelegatingCommand implements ICommand {
	/*
	 *  The command to delegate to.
	 */
	private ICommand delegate;

	/**
	 * Create a new command that delegates to another command.
	 *
	 * @param delegate
	 *                The command to delegate to.
	 */
	public DelegatingCommand(ICommand delegate) {
		this.delegate = delegate;
	}

	@Override
	public ICommand aliased() {
		return new DelegatingCommand(delegate);
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
	public boolean isAlias() {
		return true;
	}
}
