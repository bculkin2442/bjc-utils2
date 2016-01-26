package bjc.utils.dice;

import java.util.Scanner;

public class DiceExpressionParserTest {
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		
		System.out.print("Enter dice expression: ");
		
		String exp = scn.nextLine();
		
		System.out.print("Enter number of times to roll: ");
		
		int nTimes = Integer.parseInt(scn.nextLine());
		
		DiceExpressionParser dep = new DiceExpressionParser();
		
		DiceExpression dexp = dep.parse(exp);
		
		for(int i = 1; i <= nTimes; i++) {
			int roll = dexp.roll();
			
			System.out.println("Rolled " + roll);
		}
		
		scn.close();
	}
}
