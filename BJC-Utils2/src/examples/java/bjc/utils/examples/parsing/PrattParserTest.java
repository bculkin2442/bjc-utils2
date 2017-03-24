package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.TransformIterator;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.TokenSplitter;
import bjc.utils.parserutils.pratt.PrattParser;
import bjc.utils.parserutils.pratt.StringToken;
import bjc.utils.parserutils.pratt.StringTokenStream;
import bjc.utils.parserutils.pratt.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static bjc.utils.parserutils.pratt.LeftCommands.*;
import static bjc.utils.parserutils.pratt.NullCommands.*;

/**
 * Simple test for pratt parser.
 * 
 * @author EVE
 *
 */
public class PrattParserTest {
	/**
	 * Main method.
	 * 
	 * @param args
	 *            Unused CLI arguments.
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
		ops.addAll(Arrays.asList("=", "<", ">"));
		ops.addAll(Arrays.asList("+", "-", "*", "/"));
		ops.addAll(Arrays.asList("^", "!"));
		ops.addAll(Arrays.asList("(", ")"));
		ops.addAll(Arrays.asList("[", "]"));

		/*
		 * Reserved words that represent themselves, not literals.
		 */
		Set<String> reserved = new LinkedHashSet<>();
		reserved.addAll(Arrays.asList("if", "then", "else"));
		reserved.addAll(Arrays.asList("and", "or"));
		
		TokenSplitter split = new TokenSplitter();
		ops.forEach(split::addDelimiter);

		split.addNonMatcher("<=", ">=");

		split.compile();

		PrattParser<String, String, Object> parser = createParser();

		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while (!ln.trim().equals("")) {
			Iterator<Token<String, String>> tokens = preprocessInput(ops, split, ln, reserved);

			try {
				StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, null, true);

				if (!tokenStream.current().getKey().equals("(end)")) {
					System.out.println("Multipe expressions on line");
				}

				System.out.println("Parsed expression:\n" + tree);
			} catch (ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("Enter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		scn.close();
	}

	private static Iterator<Token<String, String>> preprocessInput(Set<String> ops, TokenSplitter split, String ln,
			Set<String> reserved) {
		String[] rawTokens = ln.split("\\s+");

		List<String> splitTokens = new LinkedList<>();

		for (String raw : rawTokens) {
			String[] strangs = split.split(raw);

			splitTokens.addAll(Arrays.asList(strangs));
		}

		System.out.println("Split string: " + splitTokens);

		Iterator<String> source = splitTokens.iterator();

		Iterator<Token<String, String>> tokens = new TransformIterator<>(source, (String strang) -> {
			if (ops.contains(strang) || reserved.contains(strang)) {
				return new StringToken(strang, strang);
			} else {
				return new StringToken("(literal)", strang);
			}
		});
		return tokens;
	}

	private static PrattParser<String, String, Object> createParser() {
		/*
		 * Set of which relational operators chain with each other.
		 */
		HashSet<String> relChain = new HashSet<>();
		relChain.addAll(Arrays.asList("=", "<", ">", "<=", ">="));

		/*
		 * Token for marking chains.
		 */
		StringToken chainToken = new StringToken("and", "and");

		PrattParser<String, String, Object> parser = new PrattParser<>();

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

		parser.addInitialCommand("if", preTernary(0, 0, 0, "then", "else", new StringToken("ifelse", "ifelse")));

		parser.addInitialCommand("(", grouping(0, ")", new StringToken("()", "()")));

		parser.addInitialCommand("-", unary(30));

		parser.addInitialCommand("(literal)", leaf());

		return parser;
	}
}
