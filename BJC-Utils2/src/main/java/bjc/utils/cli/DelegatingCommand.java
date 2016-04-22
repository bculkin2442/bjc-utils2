package bjc.utils.cli;

class DelegatingCommand implements ICommand {
	private ICommand delegate;

	public DelegatingCommand(ICommand delegate) {
		this.delegate = delegate;
	}

	@Override
	public ICommand createAlias() {
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