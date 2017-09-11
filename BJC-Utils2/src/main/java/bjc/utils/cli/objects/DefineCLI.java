package bjc.utils.cli.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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

	private DefineState state;

	public DefineCLI() {
		state = new DefineState();
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
		case "":
		default:
			LOGGER.severe(com.error("Unknown command %s\n", com.nameCommand));
			break;
		}
	}
}
