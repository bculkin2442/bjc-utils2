package bjc.utils.examples;

import bjc.utils.data.ITree;
import bjc.utils.funcutils.SequenceDelimiter;
import bjc.utils.funcutils.SequenceDelimiter.DelimiterException;
import bjc.utils.funcutils.TokenSplitter;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Test for {@link SequenceDelimiter} as well as {@link TokenSplitter}
 * 
 * @author EVE
 *
 */
public class DelimSplitterTest {
	/**
	 * Main method
	 * 
	 * @param args
	 *                Unused CLI args.
	 */
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		TokenSplitter split = new TokenSplitter();
		split.addDelimiter("(", ")");
		split.addDelimiter("[", "]");
		split.addDelimiter("{", "}");
		split.addDelimiter("+", "-", "*", "/");
		split.compile();

		SequenceDelimiter dlm = new SequenceDelimiter();
		dlm.addGroup(new String[] { "(" }, "parens", ")");
		dlm.addGroup(new String[] { "[" }, "brackets", "]");
		dlm.addGroup(new String[] { "{" }, "braces", "}");

		System.out.print("Enter a sequence to delimit (blank line to quit): ");
		String inp = scn.nextLine();
		System.out.println();
		
		while(!inp.equals("")) {
			String[] strings = split.split(inp);

			System.out.println("Split tokens: " + Arrays.deepToString(strings));

			try {
				ITree<String> delim = dlm.delimitSequence(strings);

				System.out.println("Delimited tokens:\n" + delim.toString());
			} catch(DelimiterException dex) {
				System.out.println("Expression isn't properly delimited.");
				System.out.println("Cause: " + dex.getMessage());
			}

			System.out.println();
			System.out.print("Enter a sequence to delimit (blank line to quit): ");
			inp = scn.nextLine();
			System.out.println();
		}

		scn.close();
	}
}
