package bjc.utils.examples;

import java.util.Scanner;

import bjc.funcdata.FunctionalStringTokenizer;
import bjc.funcdata.ListEx;
import bjc.utils.parserutils.ShuntingYard;

/**
 * Test of shunting yard
 *
 * @author ben
 *
 */
public class ShuntTest
{
	/**
	 * Main method
	 *
	 * @param args
	 *             Unused CLI args
	 */
	public static void main(final String[] args) {
		try (Scanner inputSource = new Scanner(System.in)) {
			System.out.print("Enter a expression to shunt: ");
			String line = inputSource.nextLine();

			ShuntingYard<String> yard = new ShuntingYard<>(true);

			FunctionalStringTokenizer tokenizer = new FunctionalStringTokenizer(line);
			ListEx<String> preTokens = tokenizer.toList(strang -> strang);
			ListEx<String> shuntedTokens = yard.postfix(preTokens, strang -> strang);

			System.out.println(shuntedTokens.toString());
		}
	}
}
