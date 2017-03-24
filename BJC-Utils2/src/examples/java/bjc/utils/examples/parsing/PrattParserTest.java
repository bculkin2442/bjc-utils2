package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.TransformIterator;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.TokenSplitter;
import bjc.utils.parserutils.pratt.PrattParser;
import bjc.utils.parserutils.pratt.StringToken;
import bjc.utils.parserutils.pratt.StringTokenStream;
import bjc.utils.parserutils.pratt.Token;

import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

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
	 *                Unused CLI args.
	 */
	public static void main(String[] args) {
		TokenSplitter split = new TokenSplitter();
		split.addDelimiter("+", "-", "*", "/");
		split.addDelimiter("^", "!");
		split.addDelimiter("(", ")");
		split.compile();

		PrattParser<String, String, Object> parser = new PrattParser<>();

		parser.addNonInitialCommand("+", infixLeft(20));
		parser.addNonInitialCommand("-", infixLeft(20));

		parser.addNonInitialCommand("*", infixLeft(30));
		parser.addNonInitialCommand("/", infixLeft(30));

		parser.addNonInitialCommand("!", postfix(40));

		parser.addNonInitialCommand("^", infixRight(50));

		parser.addInitialCommand("(", grouping(0, ")", new StringToken("()", "()")));
		parser.addInitialCommand("(literal)", leaf());

		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while(!ln.trim().equals("")) {
			String[] strangs = split.split(ln);

			System.out.println("Split string: " + Arrays.toString(strangs));

			Iterator<String> source = Iterators.forArray(strangs);

			Iterator<Token<String, String>> tokens = new TransformIterator<>(source, (strang) -> {
				String type;

				switch(strang) {
				case "+":
				case "-":
				case "*":
				case "/":
				case "(":
				case ")":
					type = strang;
					break;
				default:
					type = "(literal)";
				}

				return new StringToken(type, strang);
			});

			try {
				StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, null);

				System.out.println("Parsed expression:\n" + tree);
			} catch(ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("Enter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		scn.close();
	}
}
