package bjc.utils.examples;

import java.util.Scanner;

import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.IDiceExpression;

/**
 * Driver class for testing expression parser
 * 
 * @author ben
 *
 */
public class DiceExpressionParserTest {
	/**
	 * Run the parser test
	 * 
	 * @param args
	 *            Unused CLI arguments
	 */
	public static void main(String[] args) {
		/*
		 * Get a scanner for input
		 */
		Scanner scn = new Scanner(System.in);

		/*
		 * Ask to enter a expression
		 */
		System.out.print("Enter dice expression: ");

		String exp = scn.nextLine();

		/*
		 * Enter amount of times to roll an expression
		 */
		System.out.print("Enter number of times to roll: ");

		int nTimes = Integer.parseInt(scn.nextLine());

		/*
		 * Parse the string expression into a dice expression
		 */
		DiceExpressionParser dep = new DiceExpressionParser();

		IDiceExpression dexp = dep.parse(exp);

		/*
		 * Roll the dice a specified amount of times
		 */
		for (int i = 1; i <= nTimes; i++) {
			int roll = dexp.roll();

			System.out.println("Rolled " + roll);
		}

		/*
		 * Clean up after ourselves
		 */
		scn.close();
	}
}
