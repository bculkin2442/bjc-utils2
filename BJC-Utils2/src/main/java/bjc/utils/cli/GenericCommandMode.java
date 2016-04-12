package bjc.utils.cli;

import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IFunctionalMap;

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
public class GenericCommandMode implements ICommandMode {
	private IFunctionalMap<String, ICommand>		commandHandlers;
	private IFunctionalMap<String, ICommand>		defaultHandlers;

	private IFunctionalMap<String, ICommandHelp>	helpTopics;

	private BiConsumer<String, String[]>			unknownCommandHandler;

	private Consumer<String>						errorOutput;
	private Consumer<String>						normalOutput;

	private String									modeName;

	private String									customPrompt;

	/**
	 * Create a new generic command mode
	 * 
	 * @param normalOutput
	 *            The function to use for normal output
	 * @param errorOutput
	 *            The function to use for error output
	 */
	public GenericCommandMode(Consumer<String> normalOutput,
			Consumer<String> errorOutput) {
		this.normalOutput = normalOutput;
		this.errorOutput = errorOutput;

		commandHandlers = new FunctionalMap<>(new TreeMap<>());
		defaultHandlers = new FunctionalMap<>(new TreeMap<>());
		helpTopics = new FunctionalMap<>(new TreeMap<>());

		defaultHandlers.put("list", new GenericCommand((args) -> {
			listCommands();

			return this;
		}, "list\tList available command",
				"Lists all of the commands available in this mode,"
						+ " as well as the commands that are valid in any mode."));

		defaultHandlers.put("alias", new GenericCommand((args) -> {
			aliasCommands(args);

			return this;
		}, "alias\tAlias one command to another",
				"alias gives a command another name it can be invoked by. It is invoked"
						+ " with two arguments, the name of the command to alias"
						+ ", and the alias to give that command."));

		defaultHandlers.put("help", new GenericCommand((args) -> {
			if (args == null || args.length == 0) {
				// Invoke general help
				helpSummary();
			} else {
				// Invoke help for a command
				helpCommand(args[0]);
			}

			return this;
		}, "help\tConsult the help system",
				"help consults the internal help system."
						+ " It can be invoked in two ways. Invoking it with no arguments"
						+ " causes it to print out all the topics you can ask for details on,"
						+ " while invoking it with the name of a topic will print the entry"
						+ " for that topic"));

		// Add commands handled in a upper layer
		defaultHandlers.put("clear", new GenericCommand((args) -> {
			errorOutput.accept(
					"ERROR: This is a bug. Please report to the developer");

			return this;
		}, "clear\tClear the screen",
				"clear clears the screen of all the text on it,"
						+ " and prepares a fresh prompt."));

		defaultHandlers.put("exit", new GenericCommand((args) -> {
			errorOutput.accept(
					"ERROR: This is a bug. Please report to the developer");

			return this;
		}, "exit\tExit the game",
				"exit first prompts the user to make sure they want to exit,"
						+ " and if they affirm it, it quits the game"));
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
	public void addCommandHandler(String command, ICommand handler) {
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

	/**
	 * Add a help topic to this command mode that isn't tied to a command
	 * 
	 * @param topicName
	 *            The name of the topic
	 * @param help
	 *            The contents of the topic
	 */
	public void addHelpTopic(String topicName, ICommandHelp help) {
		helpTopics.put(topicName, help);
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

	private void helpCommand(String commandName) {
		if (commandHandlers.containsKey(commandName)) {
			normalOutput.accept("\n" + commandHandlers.get(commandName)
					.getHelp().getDescription());
		} else if (defaultHandlers.containsKey(commandName)) {
			normalOutput.accept("\n" + defaultHandlers.get(commandName)
					.getHelp().getDescription());
		} else if (helpTopics.containsKey(commandName)) {
			normalOutput.accept(
					"\n" + helpTopics.get(commandName).getDescription());
		} else {
			errorOutput
					.accept("ERROR: I'm sorry, but there is no help available for '"
							+ commandName + "'");
		}
	}

	private void helpSummary() {
		normalOutput.accept(
				"Help topics for this command mode are as follows:\n");
		if (commandHandlers.getSize() > 0) {
			commandHandlers.forEachValue((command) -> {
				normalOutput.accept(
						"\t" + command.getHelp().getSummary() + "\n");
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}

		normalOutput.accept(
				"\nHelp topics available in all command modes are as follows\n");
		if (defaultHandlers.getSize() > 0) {
			defaultHandlers.forEachValue((command) -> {
				normalOutput.accept(
						"\t" + command.getHelp().getSummary() + "\n");
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}

		normalOutput.accept(
				"\nHelp topics not associated with a command are as follows\n");
		if (helpTopics.getSize() > 0) {
			helpTopics.forEachValue((topic) -> {
				normalOutput.accept("\t" + topic.getSummary() + "\n");
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}
	}

	private void listCommands() {
		normalOutput.accept(
				"The available commands for this mode are as follows:\n");

		commandHandlers.keyList().forEach((commandName) -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept(
				"\nThe following commands are available in all modes:\n");

		defaultHandlers.keyList().forEach((commandName) -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept("\n");
	}

	@Override
	public ICommandMode processCommand(String command, String[] args) {
		normalOutput.accept("\n");

		if (defaultHandlers.containsKey(command)) {
			return defaultHandlers.get(command).getHandler().handle(args);
		} else if (commandHandlers.containsKey(command)) {
			return commandHandlers.get(command).getHandler().handle(args);
		} else {
			if (args != null) {
				errorOutput.accept("ERROR: Unrecognized command " + command
						+ String.join(" ", args));
			} else {
				errorOutput
						.accept("ERROR: Unrecognized command " + command);
			}

			if (unknownCommandHandler == null) {
				throw new UnsupportedOperationException(
						"Command " + command + " is invalid.");
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