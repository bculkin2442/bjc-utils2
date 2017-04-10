package bjc.utils.cli;

import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IMap;

/**
 * A general command mode, with a customizable set of commands
 *
 * There is a small set of commands which is handled by default. The first is
 * 'list', which lists all the commands the user can input. The second is
 * 'alias', which allows the user to bind a new name to a command
 *
 * @author ben
 *
 */
public class GenericCommandMode implements CommandMode {
	/*
	 * Contains the commands this mode handles
	 */
	private final IMap<String, Command>	commandHandlers;
	private final IMap<String, Command>	defaultHandlers;

	/*
	 * Contains help topics without an associated command
	 */
	private final IMap<String, CommandHelp> helpTopics;

	/*
	 * The action to execute upon encountering an unknown command
	 */
	private BiConsumer<String, String[]> unknownCommandHandler;

	/*
	 * The functions to use for input/output
	 */
	private final Consumer<String>	errorOutput;
	private final Consumer<String>	normalOutput;

	/*
	 * The name of this command mode, or null if it is unnamed
	 */
	private String modeName;

	/*
	 * The custom prompt to use, or null if none is specified
	 */
	private String customPrompt;

	/**
	 * Create a new generic command mode
	 *
	 * @param normalOutput
	 *                The function to use for normal output
	 * @param errorOutput
	 *                The function to use for error output
	 */
	public GenericCommandMode(final Consumer<String> normalOutput, final Consumer<String> errorOutput) {
		if (normalOutput == null)
			throw new NullPointerException("Normal output source must be non-null");
		else if (errorOutput == null) throw new NullPointerException("Error output source must be non-null");

		this.normalOutput = normalOutput;
		this.errorOutput = errorOutput;

		/*
		 * Initialize handler maps so that they sort in alphabetical
		 */
		/*
		 * order
		 */
		commandHandlers = new FunctionalMap<>(new TreeMap<>());
		defaultHandlers = new FunctionalMap<>(new TreeMap<>());
		helpTopics = new FunctionalMap<>(new TreeMap<>());

		setupDefaultCommands();
	}

	/**
	 * Add an alias to an existing command
	 *
	 * @param commandName
	 *                The name of the command to add an alias for
	 * @param aliasName
	 *                The new alias for the command
	 *
	 * @throws IllegalArgumentException
	 *                 if the specified command doesn't have a bound
	 *                 handler, or if the alias name already has a bound
	 *                 value
	 */
	public void addCommandAlias(final String commandName, final String aliasName) {
		if (commandName == null)
			throw new NullPointerException("Command name must not be null");
		else if (aliasName == null)
			throw new NullPointerException("Alias name must not be null");
		else if (!commandHandlers.containsKey(commandName) && !defaultHandlers.containsKey(commandName))
			throw new IllegalArgumentException("Cannot alias non-existant command '" + commandName + "'");
		else if (commandHandlers.containsKey(aliasName) || defaultHandlers.containsKey(aliasName))
			throw new IllegalArgumentException(
					"Cannot bind alias '" + aliasName + "' to a command with a bound handler");
		else {
			Command aliasedCommand;

			if (defaultHandlers.containsKey(commandName)) {
				aliasedCommand = defaultHandlers.get(commandName).aliased();
			} else {
				aliasedCommand = commandHandlers.get(commandName).aliased();
			}

			commandHandlers.put(aliasName, aliasedCommand);
		}
	}

	/**
	 * Add a command to this command mode
	 *
	 * @param command
	 *                The name of the command to add
	 * @param handler
	 *                The handler to use for the specified command
	 *
	 * @throws IllegalArgumentException
	 *                 if the specified command already has a handler
	 *                 registered
	 */
	public void addCommandHandler(final String command, final Command handler) {
		if (command == null)
			throw new NullPointerException("Command must not be null");
		else if (handler == null)
			throw new NullPointerException("Handler must not be null");
		else if (canHandle(command))
			throw new IllegalArgumentException("Command " + command + " already has a handler registered");
		else {
			commandHandlers.put(command, handler);
		}
	}

	/**
	 * Add a help topic to this command mode that isn't tied to a command
	 *
	 * @param topicName
	 *                The name of the topic
	 * @param topic
	 *                The contents of the topic
	 */
	public void addHelpTopic(final String topicName, final CommandHelp topic) {
		helpTopics.put(topicName, topic);
	}

	/*
	 * Default command builders
	 */

	private GenericCommand buildAliasCommand() {
		final String aliasShortHelp = "alias\tAlias one command to another";
		final String aliasLongHelp = "Gives a command another name it can be invoked by."
				+ " Invoke with two arguments: the name of the command to alias"
				+ "followed by the name of the alias to give that command.";

		return new GenericCommand((args) -> {
			doAliasCommands(args);

			return this;
		}, aliasShortHelp, aliasLongHelp);
	}

	private GenericCommand buildClearCommands() {
		final String clearShortHelp = "clear\tClear the screen";
		final String clearLongHelp = "Clears the screen of all the text on it," + " and prints a new prompt.";

		return new GenericCommand((args) -> {
			errorOutput.accept("ERROR: This console doesn't support screen clearing");

			return this;
		}, clearShortHelp, clearLongHelp);
	}

	private GenericCommand buildExitCommand() {
		final String exitShortHelp = "exit\tExit the console";
		final String exitLongHelp = "First prompts the user to make sure they want to"
				+ " exit, then quits if they say they do";

		return new GenericCommand((args) -> {
			errorOutput.accept("ERROR: This console doesn't support auto-exiting");

			return this;
		}, exitShortHelp, exitLongHelp);
	}

	private GenericCommand buildHelpCommand() {
		final String helpShortHelp = "help\tConsult the help system";
		final String helpLongHelp = "Consults the internal help system."
				+ " Invoked in two different ways. Invoking with no arguments"
				+ " causes all the topics you can ask for details on to be list,"
				+ " while invoking with the name of a topic will print the entry" + " for that topic";

		return new GenericCommand((args) -> {
			if (args == null || args.length == 0) {
				/*
				 * Invoke general help
				 */
				doHelpSummary();
			} else {
				/*
				 * Invoke help for a command
				 */
				doHelpCommand(args[0]);
			}

			return this;
		}, helpShortHelp, helpLongHelp);
	}

	private GenericCommand buildListCommand() {
		final String listShortHelp = "list\tList available commands";
		final String listLongHelp = "Lists all of the commands available in this mode,"
				+ " as well as commands available in any mode";

		return new GenericCommand((args) -> {
			doListCommands();

			return this;
		}, listShortHelp, listLongHelp);
	}

	@Override
	public boolean canHandle(final String command) {
		return commandHandlers.containsKey(command) || defaultHandlers.containsKey(command);
	}

	/*
	 * Implement default commands
	 */

	private void doAliasCommands(final String[] args) {
		if (args.length != 2) {
			errorOutput.accept("ERROR: Alias requires two arguments."
					+ " The command name, and the alias for that command");
		} else {
			final String commandName = args[0];
			final String aliasName = args[1];

			if (!canHandle(commandName)) {
				errorOutput.accept("ERROR: '" + commandName + "' is not a valid command.");
			} else if (canHandle(aliasName)) {
				errorOutput.accept("ERROR: Cannot overwrite command '" + aliasName + "'");
			} else {
				addCommandAlias(commandName, aliasName);
			}
		}
	}

	private void doHelpCommand(final String commandName) {
		if (commandHandlers.containsKey(commandName)) {
			final String desc = commandHandlers.get(commandName).getHelp().getDescription();

			normalOutput.accept("\n" + desc);
		} else if (defaultHandlers.containsKey(commandName)) {
			final String desc = defaultHandlers.get(commandName).getHelp().getDescription();

			normalOutput.accept("\n" + desc);
		} else if (helpTopics.containsKey(commandName)) {
			normalOutput.accept("\n" + helpTopics.get(commandName).getDescription());
		} else {
			errorOutput.accept(
					"ERROR: I'm sorry, but there is no help available for '" + commandName + "'");
		}
	}

	private void doHelpSummary() {
		normalOutput.accept("Help topics for this command mode are as follows:\n");

		if (commandHandlers.size() > 0) {
			commandHandlers.forEachValue(command -> {
				if (!command.isAlias()) {
					normalOutput.accept("\t" + command.getHelp().getSummary() + "\n");
				}
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}

		normalOutput.accept("\nHelp topics available in all command modes are as follows\n");
		if (defaultHandlers.size() > 0) {
			defaultHandlers.forEachValue(command -> {
				if (!command.isAlias()) {
					normalOutput.accept("\t" + command.getHelp().getSummary() + "\n");
				}
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}

		normalOutput.accept("\nHelp topics not associated with a command are as follows\n");
		if (helpTopics.size() > 0) {
			helpTopics.forEachValue(topic -> {
				normalOutput.accept("\t" + topic.getSummary() + "\n");
			});
		} else {
			normalOutput.accept("\tNone available\n");
		}
	}

	private void doListCommands() {
		normalOutput.accept("The available commands for this mode are as follows:\n");

		commandHandlers.keyList().forEach(commandName -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept("\nThe following commands are available in all modes:\n");
		defaultHandlers.keyList().forEach(commandName -> {
			normalOutput.accept("\t" + commandName);
		});

		normalOutput.accept("\n");
	}

	@Override
	public String getCustomPrompt() {
		if (customPrompt != null) return customPrompt;

		return CommandMode.super.getCustomPrompt();
	}

	@Override
	public String getName() {
		if (modeName != null) return modeName;

		return CommandMode.super.getName();
	}

	@Override
	public boolean isCustomPromptEnabled() {
		return customPrompt != null;
	}

	@Override
	public CommandMode process(final String command, final String[] args) {
		normalOutput.accept("\n");

		if (defaultHandlers.containsKey(command))
			return defaultHandlers.get(command).getHandler().handle(args);
		else if (commandHandlers.containsKey(command))
			return commandHandlers.get(command).getHandler().handle(args);
		else {
			if (args != null) {
				errorOutput.accept("ERROR: Unrecognized command " + command + String.join(" ", args));
			} else {
				errorOutput.accept("ERROR: Unrecognized command " + command);
			}

			if (unknownCommandHandler == null)
				throw new UnsupportedOperationException("Command " + command + " is invalid.");

			unknownCommandHandler.accept(command, args);
		}

		return this;
	}

	/**
	 * Set the custom prompt for this mode
	 *
	 * @param prompt
	 *                The custom prompt for this mode, or null to disable
	 *                the custom prompt
	 */
	public void setCustomPrompt(final String prompt) {
		customPrompt = prompt;
	}

	/**
	 * Set the name of this mode
	 *
	 * @param name
	 *                The desired name of this mode, or null to use the
	 *                default name
	 */
	public void setModeName(final String name) {
		modeName = name;
	}

	/**
	 * Set the handler to use for unknown commands
	 *
	 * @param handler
	 *                The handler to use for unknown commands, or null to
	 *                throw on unknown commands
	 */
	public void setUnknownCommandHandler(final BiConsumer<String, String[]> handler) {
		if (handler == null) throw new NullPointerException("Handler must not be null");

		unknownCommandHandler = handler;
	}

	private void setupDefaultCommands() {
		defaultHandlers.put("list", buildListCommand());
		defaultHandlers.put("alias", buildAliasCommand());
		defaultHandlers.put("help", buildHelpCommand());

		addCommandAlias("help", "man");

		/*
		 * Add commands handled in a upper layer.
		 */

		/*
		 * @TODO figure out a place to put commands that apply across
		 */
		/*
		 * all
		 */
		/*
		 * modes, but only apply to a specific application
		 */
		defaultHandlers.put("clear", buildClearCommands());
		defaultHandlers.put("exit", buildExitCommand());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GenericCommandMode [");

		if (commandHandlers != null) {
			builder.append("commandHandlers=");
			builder.append(commandHandlers);
		}

		if (defaultHandlers != null) {
			builder.append(", ");
			builder.append("defaultHandlers=");
			builder.append(defaultHandlers);
		}

		if (helpTopics != null) {
			builder.append(", ");
			builder.append("helpTopics=");
			builder.append(helpTopics);
		}

		if (modeName != null) {
			builder.append(", ");
			builder.append("modeName=");
			builder.append(modeName);
		}

		if (customPrompt != null) {
			builder.append(", ");
			builder.append("customPrompt=");
			builder.append(customPrompt);
		}

		builder.append("]");

		return builder.toString();
	}

}
