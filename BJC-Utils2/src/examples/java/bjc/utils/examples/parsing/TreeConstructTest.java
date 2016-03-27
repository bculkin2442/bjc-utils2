package bjc.utils.examples.parsing;

import java.util.Scanner;
import java.util.function.Predicate;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.AST;
import bjc.utils.parserutils.ShuntingYard;
import bjc.utils.parserutils.TreeConstructor;

/**
 * Test of tree constructor
 * 
 * @author ben
 *
 */
public class TreeConstructTest {
	/**
	 * Main method
	 * 
	 * @param args
	 *            Unused CLI args
	 */
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a expression to parse: ");
		String ln = scn.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>();

		FunctionalList<String> ls = yard.postfix(
				new FunctionalStringTokenizer(ln).toList((s) -> s),
				(s) -> s);

		System.out.println("Shunted: " + ls.toString());

		AST<String> ast =
				TreeConstructor.constructTree(ls, new Predicate<String>() {
					@Override
					public boolean test(String tok) {
						switch (tok) {
							case "+":
							case "-":
							case "*":
							case "/":
								return true;
							default:
								return false;
						}
					}
				}, (op) -> false, null);

		System.out.println("AST: " + ast.toString());

		scn.close();
	}
}
