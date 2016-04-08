package bjc.utils.examples.parsing;

import java.util.Scanner;
import java.util.function.Predicate;

import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IFunctionalList;
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
		Scanner inputSource = new Scanner(System.in);

		System.out.print("Enter a expression to parse: ");
		String line = inputSource.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>();

		IFunctionalList<String> shuntedTokens =
				yard.postfix(new FunctionalStringTokenizer(line)
						.toList((strang) -> strang), (s) -> s);

		System.out.println("Shunted: " + shuntedTokens.toString());

		AST<String> constructedTree = TreeConstructor
				.constructTree(shuntedTokens, new Predicate<String>() {
					@Override
					public boolean test(String token) {
						switch (token) {
							case "+":
							case "-":
							case "*":
							case "/":
								return true;
							default:
								return false;
						}
					}
				}, (operator) -> false, null);

		System.out.println("AST: " + constructedTree.toString());

		inputSource.close();
	}
}