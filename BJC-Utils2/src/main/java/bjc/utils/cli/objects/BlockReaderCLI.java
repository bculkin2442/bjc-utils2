package bjc.utils.cli.objects;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import bjc.utils.ioutils.Prompter;
import bjc.utils.ioutils.blocks.*;

public class BlockReaderCLI {
	private final Logger LOGGER = Logger.getLogger(BlockReaderCLI.class.getName());

	public static class BlockReaderState {
		public final Map<String, BlockReader> readers;
		public final Map<String, Reader>      sources;

		public BlockReaderState(Map<String, BlockReader> readers, Map<String, Reader> sources) {
			this.readers = readers;
			this.sources = sources;
		}
	}

	private BlockReaderState stat;

	/**
	 * Create a new CLI for configuring BlockReaders.
	 *
	 * @param srcs
	 * 	The container of initial I/O sources.
	 */
	public BlockReaderCLI(Map<String, Reader> srcs) {
		stat = new BlockReaderState(new HashMap<>(), srcs);
	}

	public static void main(String[] args) {
		/*
		 * Create/configure I/O sources.
		 */
		Map<String, Reader> sources = new HashMap<>();
		sources.put("stdio", new InputStreamReader(System.in));

		BlockReaderCLI reader = new BlockReaderCLI(sources);

		reader.run(new Scanner(System.in), "console", true);
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
				System.out.printf("reader-conf(%d)>", lno);

			String ln = input.nextLine();

			lno += 1;

			Command com = Command.fromString(ln, lno, ioSource);
			if(com == null) continue;

			handleCommand(com, interactive);
		}

		input.close();
	}

	/*
	 * Handle a command.
	 */
	public void handleCommand(Command com, boolean interactive) {
		switch(com.nameCommand) {
		case "def-filtered":
			defFiltered(com);
			break;
		case "def-layered":
			defLayered(com);
			break;
		case "def-pushback":
			defPushback(com);
			break;
		case "def-simple":
			defSimple(com);
			break;
		case "def-serial":
			defSerial(com);
			break;
		case "def-toggled":
			defToggled(com);
			break;
		case "}":
		case "end":
		case "exit":
		case "quit":
			if(interactive)
				System.out.printf("Exiting reader-conf, %d readers configured in %d commands\n",
						stat.readers.size(), com.lineNo);
			break;
		default:
			LOGGER.severe(com.error("Unknown command '%s'\n", com.nameCommand));
			break;
		}
	}

	private void defFiltered(Command com) {
		String remn = com.remnCommand;

		/*
		 * Get the block name.
		 */
		int idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.severe(com.error("No name argument for def-filtered.\n"));
		}
		String blockName = remn.substring(0, idx).trim();
		remn             = remn.substring(idx).trim();

		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get the reader name.
		 */
		idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.severe(com.error("No reader-name argument for def-filtered.\n"));	
		}
		String readerName = remn.substring(0, idx).trim();
		remn              = remn.substring(idx).trim();

		/*
		 * Check there is a reader bound to that name.
		 */
		if(!stat.readers.containsKey(readerName)) {
			LOGGER.severe(com.error("No source named %s\n", readerName));
			return;
		}

		/*
		 * Get the pattern.
		 */
		if(remn.equals("")) {
			LOGGER.severe(com.error("No filter argument for def-filtered\n"));
		}

		String filter = remn;

		try {
			Pattern pat = Pattern.compile(filter);

			Predicate<Block> pred = (block) -> {
				Matcher mat = pat.matcher(block.contents);

				return mat.matches();
			};

			BlockReader reader = new FilteredBlockReader(stat.readers.get(readerName), pred);

			stat.readers.put(blockName, reader);
		} catch (PatternSyntaxException psex) {
			LOGGER.severe(com.error("Invalid regular expression '%s' for filter. (%s)\n", filter, psex.getMessage()));
		}
	}

	private void defPushback(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length != 2) {
			LOGGER.severe(com.error("Incorrect number of arguments to def-pushback. Requires a block name and a reader name\n"));
			return;
		}

		String blockName = parts[0];
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader %s\n", blockName));
		}

		String readerName = parts[1];
		if(!stat.readers.containsKey(readerName)) {
			LOGGER.severe(com.error("No reader named %s\n", readerName));
			return;
		}

		BlockReader reader = new PushbackBlockReader(stat.readers.get(readerName));
		stat.readers.put(blockName, reader);
	}

	private void defToggled(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length != 3) {
			LOGGER.severe(com.error("Incorrect number of arguments to def-toggled. Requires a block name and two reader names\n"));
			return;
		}

		/*
		 * Get the block name.
		 */
		String blockName = parts[0];
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Make sure the component readers exist.
		 */
		if(!stat.readers.containsKey(parts[1])) {
			LOGGER.severe(com.error("No reader named %s\n", parts[1]));
			return;
		}

		if(!stat.readers.containsKey(parts[2])) {
			LOGGER.severe(com.error("No reader named %s\n", parts[2]));
			return;
		}

		BlockReader reader = new ToggledBlockReader(stat.readers.get(parts[1]), stat.readers.get(parts[2]));
		stat.readers.put(blockName, reader);
	}

	private void defLayered(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length != 3) {
			LOGGER.severe(com.error("Incorrect number of arguments to def-layered. Requires a block name and two reader names\n"));
			return;
		}

		/*
		 * Get the block name.
		 */
		String blockName = parts[0];
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Make sure the component readers exist.
		 */
		if(!stat.readers.containsKey(parts[1])) {
			LOGGER.severe(com.error("No reader named %s\n", parts[1]));
			return;
		}

		if(!stat.readers.containsKey(parts[2])) {
			LOGGER.severe(com.error("No reader named %s\n", parts[2]));
			return;
		}

		BlockReader reader = new LayeredBlockReader(stat.readers.get(parts[1]), stat.readers.get(parts[2]));
		stat.readers.put(blockName, reader);
	}

	private void defSerial(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length < 2) {
			LOGGER.severe(com.error("Not enough arguments to def-serial. Requires at least a block name and at least one reader name\n"));
			return;
		}

		/*
		 * Get the name for this BlockReader.
		 */
		String blockName = parts[0];
		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get all of the component readers.
		 */
		BlockReader[] readerArr = new BlockReader[parts.length - 1];
		for(int i = 1; i < parts.length; i++) {
			String readerName = parts[i];

			/*
			 * Check there is a reader bound to that name.
			 */
			if(!stat.readers.containsKey(readerName)) {
				LOGGER.severe(com.error("No reader named %s\n", readerName));
				return;
			}

			readerArr[i] = stat.readers.get(readerName);
		}

		BlockReader reader = new SerialBlockReader(readerArr);

		stat.readers.put(blockName, reader);
	}

	private void defSimple(Command com) {
		String remn = com.remnCommand;

		/*
		 * Get the block name.
		 */
		int idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.severe(com.error("No name argument for def-simple.\n"));
		}
		String blockName = remn.substring(0, idx).trim();
		remn             = remn.substring(idx).trim();

		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(stat.readers.containsKey(blockName)) {
			LOGGER.warning(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get the source name.
		 */
		idx = remn.indexOf(' ');
		if(idx == -1) {
			LOGGER.severe(com.error("No source-name argument for def-simple.\n"));	
		}
		String sourceName = remn.substring(0, idx).trim();
		remn              = remn.substring(idx).trim();

		/*
		 * Check there is a source bound to that name.
		 */
		if(!stat.sources.containsKey(sourceName)) {
			LOGGER.severe(com.error("No source named %s\n", sourceName));
			return;
		}

		/*
		 * Get the pattern.
		 */
		if(remn.equals("")) {
			LOGGER.severe(com.error("No delimiter argument for def-simple\n"));
		}

		String delim = remn;

		try {
			BlockReader reader = new SimpleBlockReader(delim, stat.sources.get(sourceName));

			stat.readers.put(blockName, reader);
		} catch (PatternSyntaxException psex) {
			LOGGER.severe(com.error("Invalid regular expression '%s' for delimiter. (%s)\n", delim, psex.getMessage()));
		}
	}
}
