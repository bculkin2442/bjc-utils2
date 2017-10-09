package bjc.utils.cli.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static bjc.utils.cli.objects.Command.CommandStatus;
import static bjc.utils.cli.objects.Command.CommandStatus.*;

public class DefineCLI {
	private final Logger LOGGER = Logger.getLogger(DefineCLI.class.getName());

	public static class DefineState {
		public final Map<String, UnaryOperator<String>> defines;

		public final Map<String, String> strings;
		public final Map<String, String> formats;

		public final Map<String, Pattern> patterns;

		public DefineState() {
			this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
		}

		public DefineState(Map<String, UnaryOperator<String>> defines,
				Map<String, String> strings, Map<String, String> formats,
				Map<String, Pattern> patterns) {
			this.defines = defines;

			this.strings = strings;
			this.formats = formats;

			this.patterns = patterns;
		}
	}

	private DefineState stat;

	public DefineCLI() {
		stat = new DefineState();
	}

	public static void main(String[] args) {
		DefineCLI defin = new DefineCLI();
	}

	/**
	 * Run the CLI on an input source.
	 *
	 * @param input
	 * 	The place to read input from.
	 * @param ioSource
	 * 	The name of the place to read input from.
	 * @param interactive
	 *	Whether or not the source is interactive
	 */
	public void run(Scanner input, String ioSource, boolean interactive) {
		int lno = 0;
		while(input.hasNextLine()) {
			if(interactive)
				System.out.printf("define-conf(%d)>", lno);

			String ln = input.nextLine();

			lno += 1;

			Command com = Command.fromString(ln, lno, ioSource);
			if(com == null) continue;

			handleCommand(com, interactive);
		}

		input.close();
	}

	public void handleCommand(Command com, boolean interactive) {
		switch(com.nameCommand) {
		case "def-string":
		default:
			LOGGER.severe(com.error("Unknown command %s\n", com.nameCommand));
			break;
		}
	}

	private CommandStatus defString(Command com) {
		String remn = com.remnCommand;

		int idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.warning(com.warn("Binding empty string to name '%s'\n", remn));
			idx = remn.length();
		}
		String name = remn.substring(0, idx);
		String strang = remn.substring(idx);

		if(stat.strings.containsKey(name)) {
			LOGGER.warning(com.warn("Shadowing string '%s'\n", name));
		}

		stat.strings.put(name, strang);

		return SUCCESS;
	}

	private CommandStatus defFormat(Command com) {
		String remn = com.remnCommand;

		int idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.warning(com.warn("Binding empty format to name '%s'\n", remn));
			idx = remn.length();
		}
		String name = remn.substring(0, idx);
		String fmt = remn.substring(idx);

		if(stat.formats.containsKey(name)) {
			LOGGER.warning(com.warn("Shadowing format '%s'\n", name));
		}

		stat.formats.put(name, fmt);

		return SUCCESS;
	}

	private CommandStatus bindFormat(Command com) {
		String[] parts = com.remnCommand.split(" ");

		return SUCCESS;
	}
}
