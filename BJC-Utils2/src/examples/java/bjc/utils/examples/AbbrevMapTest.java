package bjc.utils.examples;

import bjc.utils.esodata.AbbrevMap;
import bjc.utils.funcutils.StringUtils;

import java.util.Scanner;

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
	 *                Unused CLI args.
	 */
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		AbbrevMap map = new AbbrevMap();

		System.out.print("Enter a command (blank line to quit): ");
		String ln = scn.nextLine();

		while(!ln.equals("")) {
			String[] commParts = ln.split(" ");

			switch(commParts[0]) {
			case "add":
				map.addWords(commParts[1]);
				break;
			case "remove":
				map.removeWords(commParts[1]);
				break;
			case "recalc":
				map.recalculate();
				break;
			case "check":
				String list = StringUtils.toEnglishList(map.deabbrev(commParts[1]), false);
				System.out.println(list);
				break;
			case "debug":
				System.out.println(map.toString());
				break;
			default:
				System.out.println("Unknown command: " + ln);
			}
			
			System.out.print("Enter a command (blank line to quit): ");
			ln = scn.nextLine();
		}

		scn.close();
	}
}
