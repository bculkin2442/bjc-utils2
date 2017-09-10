package bjc.utils.cli.objects;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import bjc.utils.ioutils.Prompter;
import bjc.utils.ioutils.blocks.*;

public class BlockReaderCLI {
	/*
	 * All of the block readers.
	 */
	private Map<String, BlockReader> readers;

	/*
	 * All of the I/O sources.
	 */
	private Map<String, Reader> sources;

	/**
	 * Create a new CLI for configuring BlockReaders.
	 *
	 * @param srcs
	 * 	The container of initial I/O sources.
	 */
	public BlockReaderCLI(Map<String, Reader> srcs) {
		readers = new HashMap();

		sources = srcs;
	}

	public static void main(String[] args) {
		/*
		 * Create/configure I/O sources.
		 */
		Map<String, Reader> sources = new HashMap<>();
		sources.put("stdio", new InputStreamReader(System.in));

		BlockReaderCLI reader = new BlockReaderCLI(sources);

		reader.run(new Scanner(System.in), "console");
	}

	/**
	 * Run the CLI on an input source.
	 *
	 * @param input
	 * 	The place to read input from.
	 * @param ioSource
	 * 	The name of the place to read input from.
	 */
	public void run(Scanner input, String ioSource) {
		int lno = 0;
		while(input.hasNextLine()) {
			System.out.printf("reader-conf(%d)>", lno);
			String ln = input.nextLine();

			lno += 1;

			Command com = Command.fromString(ln, lno, ioSource);
			if(com == null) continue;

			handleCommand(com);
		}

		input.close();
	}

	/*
	 * Handle a command.
	 */
	public void handleCommand(Command com) {
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
		case "exit":
		case "quit":
			System.out.printf("Exiting reader-conf, %d readers configured in %d commands\n",
					readers.size(), com.lineNo);
			break;
		default:
			System.err.print(com.error("Unknown command '%s'\n", com.nameCommand));
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
			System.err.print(com.error("No name argument for def-filtered.\n"));
		}
		String blockName = remn.substring(0, idx).trim();
		remn             = remn.substring(idx).trim();

		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(readers.containsKey(blockName)) {
			System.err.print(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get the reader name.
		 */
		idx = remn.indexOf(' ');
		if(idx == -1) {
			System.err.print("No reader-name argument for def-filtered.\n");	
		}
		String readerName = remn.substring(0, idx).trim();
		remn              = remn.substring(idx).trim();

		/*
		 * Check there is a reader bound to that name.
		 */
		if(!readers.containsKey(readerName)) {
			System.err.print(com.error("No source named %s\n", readerName));
			return;
		}

		/*
		 * Get the pattern.
		 */
		if(remn.equals("")) {
			System.err.print("No filter argument for def-filtered\n");
		}

		String filter = remn;

		try {
			Pattern pat = Pattern.compile(filter);

			Predicate<Block> pred = (block) -> {
				Matcher mat = pat.matcher(block.contents);

				return mat.matches();
			};

			BlockReader reader = new FilteredBlockReader(readers.get(readerName), pred);

			readers.put(blockName, reader);
		} catch (PatternSyntaxException psex) {
			System.err.print(com.error("Invalid regular expression '%s' for filter. (%s)\n", filter, psex.getMessage()));
		}
	}

	private void defPushback(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length != 2) {
			System.err.print(com.error("Incorrect number of arguments to def-pushback. Requires a block name and a reader name\n"));
			return;
		}

		String blockName = parts[0];
		if(readers.containsKey(blockName)) {
			System.err.print(com.warn("Shadowing existing reader %s\n", blockName));
		}

		String readerName = parts[1];
		if(!readers.containsKey(readerName)) {
			System.err.print(com.error("No reader named %s\n", readerName));
			return;
		}

		BlockReader reader = new PushbackBlockReader(readers.get(readerName));
		readers.put(blockName, reader);
	}

	private void defLayered(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length != 3) {
			System.err.print(com.error("Incorrect number of arguments to def-layered. Requires a block name and two reader names\n"));
			return;
		}

		/*
		 * Get the block name.
		 */
		String blockName = parts[0];
		if(readers.containsKey(blockName)) {
			System.err.print(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Make sure the component readers exist.
		 */
		if(!readers.containsKey(parts[1])) {
			System.err.print(com.error("No reader named %s\n", parts[1]));
			return;
		}

		if(!readers.containsKey(parts[2])) {
			System.err.print(com.error("No reader named %s\n", parts[2]));
			return;
		}

		BlockReader reader = new LayeredBlockReader(readers.get(parts[1]), readers.get(parts[2]));
		readers.put(blockName, reader);
	}

	private void defSerial(Command com) {
		String[] parts = com.remnCommand.split(" ");

		if(parts.length < 2) {
			System.err.print(com.error("Not enough arguments to def-serial. Requires at least a block name and at least one reader name\n"));
			return;
		}

		/*
		 * Get the name for this BlockReader.
		 */
		String blockName = parts[0];
		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(readers.containsKey(blockName)) {
			System.err.print(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get all of the component readers.
		 */
		BlockReader[] readerArr = new BlockReader[parts.length - 1];
		for(int i = 1; i < parts.length; i++) {
			String readerName = parts[i];

			/*
			 * Check there is a source bound to that name.
			 */
			if(!readers.containsKey(readerName)) {
				System.err.print(com.error("No reader named %s\n", readerName));
				return;
			}

			readerArr[i] = readers.get(readerName);
		}

		BlockReader reader = new SerialBlockReader(readerArr);

		readers.put(blockName, reader);
	}

	private void defSimple(Command com) {
		String remn = com.remnCommand;

		/*
		 * Get the block name.
		 */
		int idx = remn.indexOf(' ');
		if(idx == -1) {
			System.err.print(com.error("No name argument for def-simple.\n"));
		}
		String blockName = remn.substring(0, idx).trim();
		remn             = remn.substring(idx).trim();

		/*
		 * Check there isn't a reader already bound to this name.
		 */
		if(readers.containsKey(blockName)) {
			System.err.print(com.warn("Shadowing existing reader named %s\n", blockName));
		}

		/*
		 * Get the source name.
		 */
		idx = remn.indexOf(' ');
		if(idx == -1) {
			System.err.print("No source-name argument for def-simple.\n");	
		}
		String sourceName = remn.substring(0, idx).trim();
		remn              = remn.substring(idx).trim();

		/*
		 * Check there is a source bound to that name.
		 */
		if(!sources.containsKey(sourceName)) {
			System.err.print(com.error("No source named %s\n", sourceName));
			return;
		}

		/*
		 * Get the pattern.
		 */
		if(remn.equals("")) {
			System.err.print("No delimiter argument for def-simple\n");
		}

		String delim = remn;

		try {
			BlockReader reader = new SimpleBlockReader(delim, sources.get(sourceName));

			readers.put(blockName, reader);
		} catch (PatternSyntaxException psex) {
			System.err.print(com.error("Invalid regular expression '%s' for delimiter. (%s)\n", delim, psex.getMessage()));
		}
	}
}
