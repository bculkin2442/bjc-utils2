package bjc.utils.examples;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import bjc.esodata.AbbrevMap2;
import bjc.utils.funcutils.StringUtils;

/**
 * Test for abbreviation map.
 *
 * @author EVE
 *
 */
public class AbbrevMapTest {
	/**
	 * Main method.
	 *
	 * @param args
	 *             Unused CLI args.
	 */
	public static void main(final String[] args) {
		final Scanner scn = new Scanner(System.in);

		final AbbrevMap2 map = new AbbrevMap2();

		System.out.print("Enter a command (blank line to quit): ");
		String ln = scn.nextLine().trim();

		while (!ln.equals("")) {
			final List<String> commParts = StringUtils.processArguments(ln);

			switch (commParts.get(0)) {
			case "add":
				map.add(commParts.get(1));
				break;
			case "remove":
				map.removeWords(commParts.get(1));
				break;
			case "check": {
				String[] strings
						= map.deabbrevAll(commParts.get(1)).toArray(new String[0]);

				final String list = StringUtils.toEnglishList(strings, false);

				System.out.println(list);
				break;
			}
			case "debug":
				System.out.println(map.toString());
				break;
			case "help":
				if (commParts.size() > 1) {
					help(commParts.get(1));
				} else {
					help();
				}
				break;
			default:
				System.out.println("Unknown command: " + ln);
			}

			System.out.print("Enter a command (blank line to quit): ");
			ln = scn.nextLine();
		}

		scn.close();
	}

	private static void help() {
		PrintStream strm = System.out;

		strm.println("Abbreviation Map Testing Commands:");
		strm.println("\tadd    <word>\tAdd a word to the abbreviation map");
		strm.println("\tremove <word>\tRemove a word from the abbreviation map");
		strm.println(
				"\tcheck  <word>\tCheck all of the possible things a word could be an abbreviation for");
		strm.println("\tdebug        \tPrint out the abbreviation map");
		strm.println("\thelp         \tList commands, or get help on a command\n");
	}

	private static void help(String com) {
		switch (com) {
		default:
			System.out.printf("\tNo help available for command: %s\n", com);
		}
	}
}
