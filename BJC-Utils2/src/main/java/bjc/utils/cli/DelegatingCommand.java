package bjc.utils.cli;

/**
 * A class for a command that delegates to another command.
 *
 * @author ben
 *
 */
class DelegatingCommand implements Command {
	/*
	 * The command to delegate to.
	 */
	private Command delegate;

	/**
	 * Create a new command that delegates to another command.
	 *
	 * @param delegate
	 *                The command to delegate to.
	 */
	public DelegatingCommand(Command delegate) {
		this.delegate = delegate;
	}

	@Override
	public Command aliased() {
		return new DelegatingCommand(delegate);
	}

	@Override
	public CommandHandler getHandler() {
		return delegate.getHandler();
	}

	@Override
	public CommandHelp getHelp() {
		return delegate.getHelp();
	}

	@Override
	public boolean isAlias() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DelegatingCommand [");

		if(delegate != null) {
			builder.append("delegate=");
			builder.append(delegate);
		}

		builder.append("]");

		return builder.toString();
	}
}
