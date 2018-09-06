package bjc.utils.cli.objects;

import bjc.utils.funcutils.StringUtils;
import bjc.utils.ioutils.format.CLFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static bjc.utils.cli.objects.Command.CommandStatus;
import static bjc.utils.cli.objects.Command.CommandStatus.*;

/*
 * @TODO 10/09/17 :DefineCLIFinish
 * 	This got left off about halfway through due to getting distracted
 * 	implementing CL-style format strings. It needs to be finished.
 */
/**
 * Command-line interface for building defines.
 *
 * @author Ben Culkin
 */
public class DefineCLI {
	private final Logger LOGGER = Logger.getLogger(DefineCLI.class.getName());

	static class DefineState {
		public final Map<String, UnaryOperator<String>> defines;

		public final Map<String, String> strings;
		public final Map<String, String> formats;

		public final Map<String, Pattern> patterns;

		public DefineState() {
			this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
		}

		public DefineState(Map<String, UnaryOperator<String>> defines, Map<String, String> strings,
				Map<String, String> formats, Map<String, Pattern> patterns) {
			this.defines = defines;

			this.strings = strings;
			this.formats = formats;

			this.patterns = patterns;
		}
	}

	private DefineState stat;

	/**
	 * Create a new Define CLI
	 */
	public DefineCLI() {
		stat = new DefineState();
	}

	/**
	 * Main method
	 * 
	 * @param args
	 *        CLI args
	 */
	public static void main(String[] args) {
		DefineCLI defin = new DefineCLI();

		try(Scanner scn = new Scanner(System.in)) {
			defin.run(scn, "console", true);
		}
	}

	/**
	 * Run the CLI on an input source.
	 *
	 * @param input
	 *        The place to read input from.
	 * @param ioSource
	 *        The name of the place to read input from.
	 * @param interactive
	 *        Whether or not the source is interactive
	 */
	public void run(Scanner input, String ioSource, boolean interactive) {
		int lno = 0;
		while(input.hasNextLine()) {
			if(interactive) System.out.printf("define-conf(%d)>", lno);

			String ln = input.nextLine();

			lno += 1;

			Command com = Command.fromString(ln, lno, ioSource);
			if(com == null) continue;

			handleCommand(com, interactive);
		}

		input.close();
	}

	/**
	 * Handle a command
	 * 
	 * @param com
	 *        The command to handle
	 * @param interactive
	 *        Whether or not our I/O stream is interactive
	 * @return The status of the executed command.
	 */
	public CommandStatus handleCommand(Command com, boolean interactive) {
		switch(com.nameCommand) {
		case "def-string":
			return defString(com);
		case "def-format":
			return defFormat(com);
		case "bind-format":
			return bindFormat(com);
		default:
			LOGGER.severe(com.error("Unknown command %s\n", com.nameCommand));
			return FAIL;
		}
	}

	private CommandStatus defString(Command com) {
		List<String> arguments = StringUtils.processArguments(com.remnCommand);

		if(arguments.size() < 1) {
			LOGGER.severe(com.error(
					"def-string expects at least one argument: the name of the string to bind"));
			return FAIL;
		}

		String name = arguments.get(0);
		String strang;

		if(arguments.size() < 2) {
			LOGGER.warning(com.warn("Binding empty string to name '%s'\n", name));
			strang = "";
		} else {
			strang = arguments.get(1);
		}

		if(stat.strings.containsKey(name)) {
			LOGGER.warning(com.warn("Shadowing string '%s'\n", name));
		}

		stat.strings.put(name, strang);

		return SUCCESS;
	}

	private CommandStatus defFormat(Command com) {
		List<String> arguments = StringUtils.processArguments(com.remnCommand);

		if(arguments.size() < 1) {
			LOGGER.severe(com.error(
					"def-format expects at least one argument: the name of the format to bind"));
			return FAIL;
		}

		String name = arguments.get(0);
		String fmt;

		if(arguments.size() < 2) {
			LOGGER.warning(com.warn("Binding empty format to name '%s'\n", name));
			fmt = "";
		} else {
			fmt = arguments.get(1);
		}

		if(stat.formats.containsKey(name)) {
			LOGGER.warning(com.warn("Shadowing format '%s'\n", name));
		}

		stat.formats.put(name, fmt);

		return SUCCESS;
	}

	private CommandStatus bindFormat(Command com) {
		List<String> strings = StringUtils.processArguments(com.remnCommand);

		if(strings.size() < 2) {
			LOGGER.severe(com.error(
					"Binding a format requires at least two arguments: the format to bind, and the name to bind it to."));
			return FAIL;
		}

		String formatName = strings.get(0);
		if(!stat.formats.containsKey(formatName)) {
			LOGGER.severe(com.error("Unknown format %s", formatName));
			return FAIL;
		}

		String bindName = strings.get(1);
		if(stat.strings.containsKey(bindName)) {
			LOGGER.warning(com.warn("Shadowing string '%s'", bindName));
		}

		List<String> fillIns = new ArrayList<>(strings.size() - 1);

		Iterator<String> itr = fillIns.iterator();
		/* Skip the format name and bind var. */
		itr.next();
		itr.next();

		while(itr.hasNext()) {
			String name = itr.next();

			if(name.startsWith("$")) {
				String varName = name.substring(1);

				if(stat.strings.containsKey(varName)) {
					fillIns.add(stat.strings.get(varName));
				} else {
					LOGGER.severe(com.error("Unknown string '%s'", varName));
					return FAIL;
				}
			} else {
				fillIns.add(name);
			}
		}

		CLFormatter fmt = new CLFormatter();

		String formatted = "";
		try {
			formatted = fmt.formatString(stat.formats.get(formatName), fillIns);
		} catch (IOException ioex) {
			LOGGER.severe(com.error("IOException formatting string: %s", ioex.getMessage()));
			return FAIL;
		}

		stat.strings.put(bindName, formatted);

		return SUCCESS;
	}
}
