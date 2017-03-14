package bjc.utils.examples.parsing;

import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.ShuntingYard;

import java.util.Scanner;

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
	public static void main(String[] args) {
		Scanner inputSource = new Scanner(System.in);

		System.out.print("Enter a expression to shunt: ");
		String line = inputSource.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>(true);

		IList<String> preTokens = new FunctionalStringTokenizer(line).toList(strang -> strang);
		IList<String> shuntedTokens = yard.postfix(preTokens, strang -> strang);

		System.out.println(shuntedTokens.toString());

		inputSource.close();
	}
}
