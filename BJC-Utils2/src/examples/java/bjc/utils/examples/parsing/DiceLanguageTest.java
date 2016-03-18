package bjc.utils.examples.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.IDiceExpression;

public class DiceLanguageTest {
	private static Map<String, BiConsumer<String, DiceLanguageState>> acts;

	static {
		acts = new HashMap<>();

		acts.put("roll", DiceLanguageTest::rollReference);
		acts.put("env", DiceLanguageTest::printEnv);
	}

	public static void printEnv(String ln, DiceLanguageState stat) {
		System.out.println("Printing enviroment for debugging purposes.");

		stat.doWith((dep, env) -> env.forEach((key, exp) -> System.out
				.println("\tKey: " + key + "\tExp: " + exp.toString())));
	}

	public static void rollReference(String ln, DiceLanguageState stat) {
		String[] strangs = ln.split(" ");

		System.out.println("\tRolling dice expression " + strangs[1] + " "
				+ strangs[2] + " times.");

		int nRolls = Integer.parseInt(strangs[2]);

		IDiceExpression dexp = stat
				.merge((dep, env) -> env.get(strangs[1]));

		for (int i = 1; i <= nRolls; i++) {
			int roll = dexp.roll();

			System.out.println("\tRolled " + roll);
		}
	}

	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		int i = 0;

		System.out.print("dice-lang-" + i + "> ");
		String ln = scn.nextLine();

		DiceExpressionParser dep = new DiceExpressionParser();
		Map<String, IDiceExpression> env = new HashMap<>();
		DiceLanguageState state = new DiceLanguageState(dep, env);

		while (!ln.equalsIgnoreCase("quit")) {
			String header = ln.split(" ")[0];

			if (acts.containsKey(header)) {
				acts.get(header).accept(ln, state);
			} else {
				IDiceExpression exp = dep.parse(ln, env);

				System.out.println("\tParsed: " + exp.toString());
				System.out.println("\tSample Roll: " + exp.roll());
			}

			i++;

			System.out.print("dice-lang-" + i + "> ");
			ln = scn.nextLine();
		}

		System.out.println("Bye.");
		scn.close();
	}
}
