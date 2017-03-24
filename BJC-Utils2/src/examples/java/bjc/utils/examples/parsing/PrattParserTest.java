package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.TransformIterator;
import bjc.utils.esodata.Directory;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.PrattParser;
import bjc.utils.parserutils.pratt.StringToken;
import bjc.utils.parserutils.pratt.StringTokenStream;
import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.splitter.TokenSplitter;
import bjc.utils.parserutils.splitter.TwoLevelSplitter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;

import static bjc.utils.parserutils.pratt.commands.NonInitialCommands.*;
import static bjc.utils.parserutils.pratt.commands.InitialCommands.*;

/**
 * Simple test for pratt parser.
 * 
 * @author EVE
 *
 */
public class PrattParserTest {
	private static final class BlockExit implements UnaryOperator<TestContext> {
		@Override
		public TestContext apply(TestContext state) {
			state.scopes.pop();

			state.blockCount.pop();

			return state;
		}
	}

	private static final class BlockEnter implements UnaryOperator<TestContext> {
		@Override
		public TestContext apply(TestContext state) {
			Directory<String, String> enclosing = state.scopes.top();
			int currBlockNumber = state.blockCount.pop();

			state.scopes.push(enclosing.newSubdirectory("block" + currBlockNumber));

			state.blockCount.push(currBlockNumber + 1);
			state.blockCount.push(0);

			return state;
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *                Unused CLI arguments.
	 */
	public static void main(String[] args) {
		/*
		 * Use a linked hash set to preserve insertion order.
		 */
		Set<String> ops = new LinkedHashSet<>();

		ops.add(":=");
		ops.addAll(Arrays.asList("||", "&&"));
		ops.addAll(Arrays.asList("<=", ">="));

		ops.add(".");
		ops.add(";");
		ops.addAll(Arrays.asList("=", "<", ">"));
		ops.addAll(Arrays.asList("+", "-", "*", "/"));
		ops.addAll(Arrays.asList("^", "!"));
		ops.addAll(Arrays.asList("(", ")"));
		ops.addAll(Arrays.asList("[", "]"));
		ops.addAll(Arrays.asList("{", "}"));

		/*
		 * Reserved words that represent themselves, not literals.
		 */
		Set<String> reserved = new LinkedHashSet<>();
		reserved.addAll(Arrays.asList("if", "then", "else"));
		reserved.addAll(Arrays.asList("and", "or"));
		reserved.addAll(Arrays.asList("begin", "end"));

		TwoLevelSplitter split = new TwoLevelSplitter();

		split.addCompoundDelim(":=");
		split.addCompoundDelim("||", "&&");
		split.addCompoundDelim("<=", ">=");

		split.addSimpleDelim(".");
		split.addSimpleDelim(";");
		split.addSimpleDelim("=", "<", ">");
		split.addSimpleDelim("+", "-", "*", "/");
		split.addSimpleDelim("^", "!");
		split.addSimpleMulti("\\(", "\\)");
		split.addSimpleMulti("\\[", "\\]");
		split.addSimpleMulti("\\{", "\\}");

		split.compile();

		PrattParser<String, String, TestContext> parser = createParser();

		TestContext ctx = new TestContext();

		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while(!ln.trim().equals("")) {
			Iterator<Token<String, String>> tokens = preprocessInput(ops, split, ln, reserved, ctx);

			try {
				StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, ctx, true);

				if(!tokenStream.current().getKey().equals("(end)")) {
					System.out.println("Multipe expressions on line");
				}

				System.out.println("Parsed expression:\n" + tree);
			} catch(ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("Enter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		System.out.println();
		System.out.println("Context is:\n" + ctx);

		scn.close();
	}

	private static Iterator<Token<String, String>> preprocessInput(Set<String> ops, TokenSplitter split, String ln,
			Set<String> reserved, TestContext ctx) {
		String[] rawTokens = ln.split("\\s+");

		List<String> splitTokens = new LinkedList<>();

		for(String raw : rawTokens) {
			String[] strangs = split.split(raw);

			splitTokens.addAll(Arrays.asList(strangs));
		}

		System.out.println("Split string: " + splitTokens);

		Iterator<String> source = splitTokens.iterator();

		Iterator<Token<String, String>> tokens = new TransformIterator<>(source, (String strang) -> {
			if(ops.contains(strang) || reserved.contains(strang)) {
				return new StringToken(strang, strang);
			} else {
				return new StringToken("(literal)", strang);
			}
		});
		return tokens;
	}

	private static PrattParser<String, String, TestContext> createParser() {
		/*
		 * Set of which relational operators chain with each other.
		 */
		HashSet<String> relChain = new HashSet<>();
		relChain.addAll(Arrays.asList("=", "<", ">", "<=", ">="));

		/*
		 * Token for marking chains.
		 */
		StringToken chainToken = new StringToken("and", "and");

		PrattParser<String, String, TestContext> parser = new PrattParser<>();

		parser.addNonInitialCommand("if", ternary(5, 0, "else", new StringToken("cond", "cond"), false));

		parser.addNonInitialCommand(":=", infixNon(10));

		parser.addNonInitialCommand("and", infixLeft(13));
		parser.addNonInitialCommand("or", infixLeft(13));

		parser.addNonInitialCommand("=", chain(15, relChain, chainToken));
		parser.addNonInitialCommand("<", chain(15, relChain, chainToken));
		parser.addNonInitialCommand(">", chain(15, relChain, chainToken));
		parser.addNonInitialCommand("<=", chain(15, relChain, chainToken));
		parser.addNonInitialCommand(">=", chain(15, relChain, chainToken));

		parser.addNonInitialCommand("&&", infixRight(17));
		parser.addNonInitialCommand("||", infixRight(17));

		parser.addNonInitialCommand("+", infixLeft(20));
		parser.addNonInitialCommand("-", infixLeft(20));

		parser.addNonInitialCommand("*", infixLeft(30));
		parser.addNonInitialCommand("/", infixLeft(30));

		parser.addNonInitialCommand("!", postfix(40));

		parser.addNonInitialCommand("^", infixRight(50));

		parser.addNonInitialCommand(".", infixLeft(60));

		parser.addNonInitialCommand("[", postCircumfix(60, 0, "]", new StringToken("idx", "idx")));

		parser.addInitialCommand("if",
				preTernary(0, 0, 0, "then", "else", new StringToken("ifelse", "ifelse")));

		parser.addInitialCommand("(", grouping(0, ")", new StringToken("parens", "parens")));

		parser.addInitialCommand("{", delimited(0, ";", "}", new StringToken("block", "block"),
				new BlockEnter(), (state) -> state, new BlockExit(), true));

		parser.addInitialCommand("-", unary(30));

		parser.addInitialCommand("(literal)", leaf());

		return parser;
	}
}
