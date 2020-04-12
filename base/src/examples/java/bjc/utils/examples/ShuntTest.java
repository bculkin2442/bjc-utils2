package bjc.utils.examples;

import java.util.Scanner;

import bjc.funcdata.FunctionalStringTokenizer;
import bjc.funcdata.IList;
import bjc.utils.parserutils.ShuntingYard;

/**
 * Test of shunting yard
 *
 * @author ben
 *
 */
public class ShuntTest {
	/**
	 * Main method
	 *
	 * @param args
	 *                Unused CLI args
	 */
	public static void main(final String[] args) {
		final Scanner inputSource = new Scanner(System.in);

		System.out.print("Enter a expression to shunt: ");
		final String line = inputSource.nextLine();

		final ShuntingYard<String> yard = new ShuntingYard<>(true);

		final IList<String> preTokens = new FunctionalStringTokenizer(line).toList(strang -> strang);
		final IList<String> shuntedTokens = yard.postfix(preTokens, strang -> strang);

		System.out.println(shuntedTokens.toString());

		inputSource.close();
	}
}
