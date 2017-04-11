package bjc.utils.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import bjc.utils.data.ITree;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.funcutils.StringUtils;
import bjc.utils.parserutils.delims.DelimiterException;
import bjc.utils.parserutils.delims.DelimiterGroup;
import bjc.utils.parserutils.delims.RegexCloser;
import bjc.utils.parserutils.delims.RegexOpener;
import bjc.utils.parserutils.delims.SequenceDelimiter;
import bjc.utils.parserutils.delims.StringDelimiter;
import bjc.utils.parserutils.splitter.ConfigurableTokenSplitter;

/**
 * Test for {@link SequenceDelimiter} as well as
 * {@link ConfigurableTokenSplitter}
 *
 * @author EVE
 *
 */
public class DelimSplitterTest {
	private ConfigurableTokenSplitter split;

	private StringDelimiter dlm;

	private Map<String, String> mirrored;

	private Map<String, DelimiterGroup<String>> groups;

	boolean verbose;

	/*
	 * Create a new tester.
	 */
	private DelimSplitterTest() {
		loadMirrorDB();

		groups = new HashMap<>();

		split = new ConfigurableTokenSplitter(true);

		dlm = new StringDelimiter();

		verbose = true;
	}

	private void loadMirrorDB() {
		mirrored = new HashMap<>();

		final InputStream stream = getClass().getResourceAsStream("/BidiMirrorDB.txt");

		try (Scanner scn = new Scanner(stream)) {
			String ln = "";

			while (scn.hasNextLine()) {
				ln = scn.nextLine();

				if (ln.equals("")) {
					continue;
				}
				if (ln.startsWith("#")) {
					continue;
				}

				final int cp1 = Integer.parseInt(ln.substring(0, 4), 16);
				final int cp2 = Integer.parseInt(ln.substring(6, 10), 16);

				final char[] cpa1 = Character.toChars(cp1);
				final char[] cpa2 = Character.toChars(cp2);

				final String cps1 = new String(cpa1);
				final String cps2 = new String(cpa2);

				mirrored.put(cps1, cps2);
			}
		}
	}

	/*
	 * Run the tester interface.
	 */
	private void runLoop() {
		final Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to quit): ");
		String inp = scn.nextLine().trim();
		System.out.println();

		while (!inp.equals("")) {
			handleCommand(inp, scn, true);

			System.out.println();

			System.out.print("Enter a command (blank line to quit): ");
			inp = scn.nextLine();

			System.out.println();
		}

		scn.close();
	}

	/*
	 * Handle a input command.
	 */
	private void handleCommand(final String inp, final Scanner scn, final boolean isInteractive) {
		if (inp.equals("")) return;

		int idx = inp.indexOf(' ');

		if (idx == -1) {
			idx = inp.length();
		}

		final String command = inp.substring(0, idx);

		final String args = inp.substring(idx).trim();
		final String[] argArray = args.split(" ");

		switch (command) {
		case "test":
			handleTest(args, false);
			break;
		case "test-ws":
			handleTest(args, true);
			break;
		case "splitter-split":
			handleSplit(argArray);
			break;
		case "splitter-compile":
			split.compile();
			if (verbose) {
				System.out.println("Compiled splitter");
			}
			break;
		case "splitter-add":
			split.addSimpleDelimiters(argArray);
			if (verbose) {
				System.out.println("Added delimiters " + StringUtils.toEnglishList(argArray, true));
			}
			break;
		case "splitter-addmulti":
			split.addMultiDelimiters(argArray);
			if (verbose) {
				System.out.println(
						"Added multi-delimiters " + StringUtils.toEnglishList(argArray, true));
			}
			break;
		case "splitter-addmatch":
			for (final String arg : argArray) {
				split.addSimpleDelimiters(arg, mirrored.get(arg));
			}
			if (verbose) {
				System.out.println("Added matched delimiters "
						+ StringUtils.toEnglishList(argArray, true));
			}
			break;
		case "splitter-debug":
			System.out.println(split.toString());
			break;
		case "splitter-reset":
			split = new ConfigurableTokenSplitter(true);
			if (verbose) {
				System.out.println("Reset splitter");
			}
			break;

		case "delims-addgroup":
			for (final String arg : argArray) {
				dlm.addGroup(groups.get(arg));
			}
			if (verbose) {
				System.out.println("Added groups " + StringUtils.toEnglishList(argArray, true));
			}
			break;
		case "delims-setinitial":
			dlm.setInitialGroup(groups.get(argArray[0]));
			if (verbose) {
				System.out.println("Set initial group");
			}
			break;
		case "delims-debug":
			System.out.println(dlm.toString());
			break;
		case "delims-test":
			handleDelim(args);
			break;
		case "delims-reset":
			dlm = new StringDelimiter();
			if (verbose) {
				System.out.println("Reset delimiter");
			}
			break;
		case "delimgroups-new":
			for (final String arg : argArray) {
				groups.put(arg, new DelimiterGroup<>(arg));
			}
			if (verbose) {
				System.out.println("Created groups " + StringUtils.toEnglishList(argArray, true));
			}
			break;
		case "delimgroups-edit":
			for (final String arg : argArray) {
				handleEditGroup(arg, scn, isInteractive);
			}
			break;
		case "delimgroups-debug":
			for (final DelimiterGroup<String> group : groups.values()) {
				System.out.println(group.toString());
			}
			break;
		case "delimgroups-reset":
			dlm = new StringDelimiter();
			groups = new HashMap<>();
			if (verbose) {
				System.out.println("Reset delimiter groups + delimiter");
			}
			break;
		case "load-file":
			handleLoadFile(args);
			break;
		default:
			System.out.println("Unknown command ");
		}

	}

	/*
	 * Load script commands from a file.
	 */
	private void handleLoadFile(final String args) {
		String pth = args;

		if (args.startsWith("\"")) {
			pth = args.substring(1, args.length() - 1);
		}

		try (FileInputStream fis = new FileInputStream(pth)) {
			final Scanner scn = new Scanner(fis);

			while (scn.hasNextLine()) {
				final String ln = scn.nextLine().trim();

				if (ln.equals("")) {
					continue;
				}
				if (ln.startsWith("#")) {
					continue;
				}

				if (verbose) {
					System.out.println("\nRead command '" + ln + "' from file\n");
				}
				handleCommand(ln, scn, false);
			}

			scn.close();
		} catch (final FileNotFoundException fnfex) {
			System.out.println("Couldn't find file '" + args + "'");
		} catch (final IOException ioex) {
			System.out.println("I/O error with file '" + args + "'\nCause: " + ioex.getMessage());
		}
	}

	/*
	 * Handle editing a group.
	 */
	private void handleEditGroup(final String arg, final Scanner scn, final boolean isInteractive) {
		if (!groups.containsKey(arg)) {
			System.out.println("No group named '" + arg + "'");
			return;
		}

		final DelimiterGroup<String> group = groups.get(arg);

		if (verbose) {
			System.out.println("Editing group '" + arg + "'");
		}
		if (isInteractive) {
			System.out.println("Enter command (blank line to stop editing): ");
		}

		String ln = scn.nextLine().trim();

		while (!ln.equals("")) {
			int idx = ln.indexOf(' ');

			if (idx == -1) {
				idx = ln.length();
			}

			final String command = ln.substring(0, idx);

			final String args = ln.substring(idx).trim();
			final String[] argArray = args.split(" ");

			switch (command) {
			case "add-closing":
				group.addClosing(argArray);
				if (verbose) {
					System.out.println(
							"Added closers " + StringUtils.toEnglishList(argArray, true));
				}
				break;
			case "add-tlexclude":
				group.addTopLevelForbid(argArray);
				if (verbose) {
					System.out.println("Added top-level exclusions "
							+ StringUtils.toEnglishList(argArray, true));
				}
				break;
			case "add-exclude":
				group.addTopLevelForbid(argArray);
				if (verbose) {
					System.out.println("Added nested exclusions "
							+ StringUtils.toEnglishList(argArray, true));
				}
				break;
			case "add-subgroup":
				group.addSubgroup(argArray[0], Integer.parseInt(argArray[1]));
				if (verbose) {
					System.out.printf("Added subgroup %s with priority %s\n", argArray[0],
							argArray[1]);
				}
				break;
			case "add-implied-subgroup":
				group.implySubgroup(argArray[0], argArray[1]);
				if (verbose) {
					System.out.printf("Made closer '%s' imply a '%s' subgroup\n", argArray[0],
							argArray[1]);
				}
				break;
			case "add-opener":
				group.addOpener(argArray[0], argArray[1]);
				if (verbose) {
					System.out.printf("Added opener '%s' for group '%s'\n", argArray[0],
							argArray[1]);
				}
				break;
			case "add-reopener":
				group.addPredOpener(new RegexOpener(argArray[0], argArray[1]));
				if (verbose) {
					System.out.printf("Added regex '%s' as opener for '%s'\n", argArray[1],
							argArray[0]);
				}
				break;
			case "add-recloser":
				group.addPredCloser(new RegexCloser(argArray[0]));
				if (verbose) {
					System.out.printf("Added parameterized string '%s' as closer\n", argArray[0]);
				}
				break;
			case "debug":
				System.out.println(group.toString());
				break;
			default:
				System.out.println("Unknown command " + command);
			}

			if (isInteractive) {
				System.out.println("Enter command (blank line to stop editing): ");
			}

			ln = scn.nextLine().trim();
		}

		if (verbose) {
			System.out.println("Finished editing group '" + arg + "'");
		}
	}

	private void handleDelim(final String args) {
		try {
			final ITree<String> res = dlm.delimitSequence(args.split(" "));

			printDelimSeq(res);
		} catch (final DelimiterException dex) {
			System.out.println("Expression '" + args + "' isn't properly delimited.\n\tCause: "
					+ dex.getMessage());
		}
	}

	private void handleSplit(final String[] argArray) {
		for (int i = 0; i < argArray.length; i++) {
			final String arg = argArray[i];

			final IList<String> strangs = split.split(arg);

			System.out.printf("%d '%s' %s\n", i, arg, strangs);
		}
	}

	private void handleTest(final String inp, final boolean splitWS) {
		IList<String> strings;

		try {
			strings = split.split(inp);
		} catch (final IllegalStateException isex) {
			System.out.println("Splitter must be compiled at least once before use.");
			return;
		}

		System.out.println("Split tokens: " + strings);

		if (splitWS) {
			final List<String> tks = new LinkedList<>();

			for (final String strang : strings) {
				tks.addAll(Arrays.asList(strang.split(" ")));
			}

			strings = new FunctionalList<>(tks);
		}
		try {
			final ITree<String> delim = dlm.delimitSequence(strings.toArray(new String[0]));

			printDelimSeq(delim);
		} catch (final DelimiterException dex) {
			System.out.println("Expression isn't properly delimited.");
			System.out.println("Cause: " + dex.getMessage());
		}
	}

	private void printDelimSeq(final ITree<String> delim) {
		System.out.println("Delimited tokens:\n" + delim.getChild(1).toString());
		System.out.print("Delimited expr: ");
		printDelimTree(delim);
		System.out.println();
		System.out.println();

		System.out.println();
	}

	private void printDelimTree(final ITree<String> tree) {
		final StringBuilder sb = new StringBuilder();

		intPrintDelimTree(tree.getChild(1), sb);

		System.out.println(sb.toString().replaceAll("\\s+", " "));
	}

	private void intPrintDelimTree(final ITree<String> tree, final StringBuilder sb) {
		tree.doForChildren((child) -> {
			intPrintDelimNode(child, sb);
		});
	}

	private void intPrintDelimNode(final ITree<String> tree, final StringBuilder sb) {
		if (tree.getHead().equals("contents")) {
			intPrintDelimTree(tree, sb);
			return;
		}

		switch (tree.getChildrenCount()) {
		case 0:
			sb.append(tree.getHead());
			sb.append(" ");

			break;
		case 1:
			intPrintDelimTree(tree.getChild(0), sb);

			break;
		case 2:
			intPrintDelimTree(tree.getChild(0).getChild(0), sb);
			intPrintDelimNode(tree.getChild(1), sb);

			break;
		case 3:
			intPrintDelimNode(tree.getChild(0), sb);

			final ITree<String> contents = tree.getChild(1);

			intPrintDelimTree(contents.getChild(0), sb);
			intPrintDelimNode(tree.getChild(2), sb);

			break;
		}
	}

	/**
	 * Main method
	 *
	 * @param args
	 *                Unused CLI args.
	 */
	public static void main(final String[] args) {
		final DelimSplitterTest tst = new DelimSplitterTest();

		tst.runLoop();
	}
}
