package bjc.utils.examples.parsing;

import bjc.utils.data.IPair;
import bjc.utils.data.ITree;
import bjc.utils.data.Pair;
import bjc.utils.data.Tree;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;
import bjc.utils.funcutils.ListUtils;
import bjc.utils.funcutils.StringUtils;
import bjc.utils.parserutils.ShuntingYard;
import bjc.utils.parserutils.TreeConstructor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Test of tree constructor
 *
 * @author ben
 *
 */
public class TreeConstructTest {
	private static final class OperatorPicker implements Predicate<String> {
		@Override
		public boolean test(String token) {
			if(StringUtils.containsOnly(token, "\\["))      return true;
			else if(StringUtils.containsOnly(token, "\\]")) return true;

			switch(token) {
			case "+":
			case "-":
			case "*":
			case "/":
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * Main method
	 *
	 * @param args
	 *                Unused CLI args
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String[] args) {
		Scanner inputSource = new Scanner(System.in);

		System.out.print("Enter a expression to parse: ");
		String line = inputSource.nextLine();

		IList<String> tokens = new FunctionalStringTokenizer(line).toList();

		ShuntingYard<String> yard = new ShuntingYard<>(true);

		Deque<IPair<String, String>> ops = new LinkedList<>();

		ops.add(new Pair<>("+", "\\+"));
		ops.add(new Pair<>("-", "-"));
		ops.add(new Pair<>("*", "\\*"));
		ops.add(new Pair<>("/", "/"));
		ops.add(new Pair<>(":=", ":="));
		ops.add(new Pair<>("=>", "=>"));

		IList<String> semiExpandedTokens = ListUtils.splitTokens(tokens, ops);

		ops = new LinkedList<>();
		ops.add(new Pair<>("(", "\\("));
		ops.add(new Pair<>(")", "\\)"));
		ops.add(new Pair<>("[", "\\["));
		ops.add(new Pair<>("]", "\\]"));

		IList<String> fullyExpandedTokens = ListUtils.deAffixTokens(semiExpandedTokens, ops);
		fullyExpandedTokens.removeIf((strang) -> strang.equals(""));

		IList<String> shuntedTokens = yard.postfix(fullyExpandedTokens, (token) -> token);

		System.out.println("Shunted: " + shuntedTokens.toString());

		Predicate<String> specialPicker = (operator) -> {
			if(StringUtils.containsOnly(operator, "\\["))      return true;
			else if(StringUtils.containsOnly(operator, "\\]")) return true;

			return false;
		};

		IMap<String, Function<Deque<ITree<String>>, ITree<String>>> operators
			= new FunctionalMap<>();

		operators.put("[", (queuedTrees) -> {
			return null;
		});

		operators.put("[", (queuedTrees) -> {
			Tree<String> openTree = new Tree<>("[");

			queuedTrees.push(openTree);

			return openTree;
		});

		operators.put("]", (queuedTrees) -> {
			ITree<String> arrayTree = new Tree<>("[]");

			while(!queuedTrees.peek().getHead().equals("[")) {
				arrayTree.addChild(queuedTrees.pop());
			}

			queuedTrees.push(arrayTree);

			return arrayTree;
		});

		ITree<String> constructedTree = TreeConstructor.constructTree(shuntedTokens,
				new OperatorPicker(), specialPicker, operators::get);

		System.out.println("AST: " + constructedTree.toString());

		inputSource.close();
	}
}
