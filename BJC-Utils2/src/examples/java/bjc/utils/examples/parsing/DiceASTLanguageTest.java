package bjc.utils.examples.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.IDiceExpression;
import bjc.utils.dice.ast.DiceASTExpression;
import bjc.utils.dice.ast.DiceASTFreezer;
import bjc.utils.dice.ast.DiceASTParser;
import bjc.utils.dice.ast.IDiceASTNode;
import bjc.utils.parserutils.AST;

public class DiceASTLanguageTest {
	private static Map<String, BiConsumer<String, DiceASTLanguageState>> acts;

	static {
		acts = new HashMap<>();

		acts.put("roll", DiceASTLanguageTest::rollReference);
		acts.put("env", DiceASTLanguageTest::printEnv);
		acts.put("freeze", DiceASTLanguageTest::freezeVar);
	}

	private static void freezeVar(String ln, DiceASTLanguageState stat) {
		String[] strangs = ln.split(" ");

		System.out.println("Freezing references in " + strangs[1]);

		stat.doWith((dep, env) -> {
			env.put(strangs[1], new DiceASTExpression(
					DiceASTFreezer.freezeAST(env.get(strangs[1]), env),
					env));
		});
	}

	public static void printEnv(String ln, DiceASTLanguageState stat) {
		System.out.println("Printing enviroment for debugging purposes.");

		stat.doWith((dep, env) -> env.forEach((key, exp) -> System.out
				.println("\tKey: " + key + "\tExp: " + exp.toString())));
	}

	public static void rollReference(String ln,
			DiceASTLanguageState stat) {
		String[] strangs = ln.split(" ");

		System.out.println("\tRolling dice expression " + strangs[1] + " "
				+ strangs[2] + " times.");

		int nRolls = Integer.parseInt(strangs[2]);

		IDiceExpression dexp =
				stat.merge((dep, env) -> env.get(strangs[1]));

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

		DiceASTParser dap = new DiceASTParser();

		DiceExpressionParser dep = new DiceExpressionParser();
		Map<String, DiceASTExpression> env = new HashMap<>();
		DiceASTLanguageState state = new DiceASTLanguageState(dep, env);

		while (!ln.equalsIgnoreCase("quit")) {
			String header = ln.split(" ")[0];

			if (acts.containsKey(header)) {
				acts.get(header).accept(ln, state);
			} else {

				AST<IDiceASTNode> builtAST = dap.buildAST(ln);
				DiceASTExpression exp =
						new DiceASTExpression(builtAST, env);

				System.out.println("\tParsed: " + exp.toString());
				System.out.println("\tSample Roll: " + exp.roll());

				env.put("last", exp);
			}

			i++;

			System.out.print("dice-lang-" + i + "> ");
			ln = scn.nextLine();
		}

		System.out.println("Bye.");
		scn.close();
	}
}
