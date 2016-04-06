package bjc.utils.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A general command mode, with a customizable set of commands
 * 
 * There is a small set of commands which is handled by default. The first
 * is 'list', which lists all the commands the user can input. The second
 * is 'alias', which allows the user to bind a new name to a command
 * 
 * @author ben
 *
 */
public class GeneralCommandMode implements ICommandMode {
	private Map<String, ICommandHandler>	commandHandlers;
	private String							customPrompt;

	private Map<String, ICommandHandler>	defaultHandlers;

	private Consumer<String>				errorOutput;
	private String							modeName;

	private Consumer<String>				normalOutput;

	private BiConsumer<String, String[]>	unknownCommandHandler;

	/**
	 * Create a new general command mode
	 * 
	 * @param normalOutput
	 *            The function to use for normal output
	 * @param errorOutput
	 *            The function to use for error output
	 */
	public GeneralCommandMode(Consumer<String> normalOutput,
			Consumer<String> errorOutput) {
		this.normalOutput = normalOutput;
		this.errorOutput = errorOutput;

		commandHandlers = new HashMap<>();
		defaultHandlers = new HashMap<>();

		defaultHandlers.put("list", (args) -> {
			listCommands();

			return this;
		});

		defaultHandlers.put("alias", (args) -> {
			aliasCommands(args);

			return this;
		});
	}

	/**
	 * Add an alias to an existing command
	 * 
	 * @param commandName
	 *            The name of the command to add an alias for
	 * @param aliasName
	 *            The new alias for the command
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified command doesn't have a bound handler,
	 *             or if the alias name already has a bound value
	 */
	public void addCommandAlias(String commandName, String aliasName) {
		if (commandName == null) {
			throw new NullPointerException(
					"Command name must not be null");
		} else if (aliasName == null) {
			throw new NullPointerException("Alias name must not be null");
		} else if (!commandHandlers.containsKey(commandName)) {
			throw new IllegalArgumentException(
					"Cannot alias non-existant command '" + commandName
							+ "'");
		} else if (commandHandlers.containsKey(aliasName)) {
			throw new IllegalArgumentException("Cannot bind alias '"
					+ aliasName + "' to a command with a bound handler");
		} else {
			commandHandlers.put(aliasName,
					commandHandlers.get(commandName));
		}
	}

	/**
	 * Add a command to this command mode
	 * 
	 * @param command
	 *            The command to add
	 * @param handler
	 *            The handler to use for the specified command
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified command already has a handler
	 *             registered
	 */
	public void addCommandHandler(String command,
			ICommandHandler handler) {
		if (command == null) {
			throw new NullPointerException("Command must not be null");
		} else if (handler == null) {
			throw new NullPointerException("Handler must not be null");
		} else if (canHandleCommand(command)) {
			throw new IllegalArgumentException("Command " + command
					+ " already has a handler registered");
		} else {
			commandHandlers.put(command, handler);
		}
	}

	private void aliasCommands(String[] args) {
		if (args.length != 2) {
			errorOutput.accept("ERROR: Alias requires two arguments. "
					+ "The command name, and the alias for that command");
		} else {
			String commandName = args[0];
			String aliasName = args[1];

			if (!canHandleCommand(commandName)) {
				errorOutput.accept("ERROR: '" + commandName
						+ "' is not a valid command.");
			} else if (canHandleCommand(aliasName)) {
				errorOutput.accept("ERROR: Cannot overwrite command '"
						+ aliasName + "'");
			} else {
				addCommandAlias(commandName, aliasName);
			}
		}
	}

	@Override
	public boolean canHandleCommand(String command) {
		return commandHandlers.containsKey(command)
				|| defaultHandlers.containsKey(command);
	}

	@Override
	public String getCustomPrompt() {
		if (customPrompt != null) {
			return customPrompt;
		}

		return ICommandMode.super.getCustomPrompt();
	}

	@Override
	public String getName() {
		if (modeName != null) {
			return modeName;
		}

		return ICommandMode.super.getName();
	}

	private void listCommands() {
		normalOutput.accept(
				"The available commands for this mode are as follows:\n");

		commandHandlers.keySet().forEach((commandName) -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept(
				"\nThe following commands are available in all modes:\n");

		defaultHandlers.keySet().forEach((commandName) -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept("\n");
	}

	@Override
	public ICommandMode processCommand(String command, String[] args) {
		normalOutput.accept("\n");

		if (defaultHandlers.containsKey(command)) {
			return defaultHandlers.get(command).handle(args);
		} else if (commandHandlers.containsKey(command)) {
			return commandHandlers.get(command).handle(args);
		} else {
			if (unknownCommandHandler == null) {
				throw new UnsupportedOperationException(
						"Command " + command + " is invalid.");
			} else if (args != null) {
				errorOutput.accept("ERROR: Unrecognized command " + command
						+ String.join(" ", args));
			} else {
				errorOutput
						.accept("ERROR: Unrecognized command " + command);
			}

			unknownCommandHandler.accept(command, args);
		}

		return this;
	}

	/**
	 * Set the custom prompt for this mode
	 * 
	 * @param prompt
	 *            The custom prompt for this mode, or null to disable the
	 *            custom prompt
	 */
	public void setCustomPrompt(String prompt) {
		customPrompt = prompt;
	}

	/**
	 * Set the name of this mode
	 * 
	 * @param name
	 *            The desired name of this mode, or null to use the default
	 *            name
	 */
	public void setModeName(String name) {
		modeName = name;
	}

	/**
	 * Set the handler to use for unknown commands
	 * 
	 * @param handler
	 *            The handler to use for unknown commands
	 */
	public void setUnknownCommandHandler(
			BiConsumer<String, String[]> handler) {
		if (handler == null) {
			throw new NullPointerException("Handler must not be null");
		}

		unknownCommandHandler = handler;
	}

	@Override
	public boolean useCustomPrompt() {
		return customPrompt != null;
	}
}