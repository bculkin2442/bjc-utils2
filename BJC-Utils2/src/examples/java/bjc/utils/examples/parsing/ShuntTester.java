package bjc.utils.examples.parsing;

import java.util.Scanner;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.ShuntingYard;

/**
 * Test of shunting yard
 * 
 * @author ben
 *
 */
public class ShuntTester {
	/**
	 * Main method
	 * 
	 * @param args
	 *            Unused CLI args
	 */
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a expression to shunt: ");
		String ln = scn.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>();

		FunctionalList<String> ls = yard.postfix(
				new FunctionalStringTokenizer(ln).toList((s) -> s),
				(s) -> s);

		System.out.println(ls.toString());

		scn.close();
	}
}
