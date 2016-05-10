package bjc.utils.examples.parsing;

import java.util.Scanner;

import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IList;
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
		Scanner inputSource = new Scanner(System.in);

		System.out.print("Enter a expression to shunt: ");
		String line = inputSource.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>(true);

		IList<String> shuntedTokens = yard
				.postfix(new FunctionalStringTokenizer(line)
						.toList((strang) -> strang), (strang) -> strang);

		System.out.println(shuntedTokens.toString());

		inputSource.close();
	}
}