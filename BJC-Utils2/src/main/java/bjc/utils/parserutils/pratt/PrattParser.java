package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.funcutils.NumberUtils;
import bjc.utils.parserutils.ParserException;

import java.util.HashMap;
import java.util.Map;

/**
 * A configurable Pratt parser for expressions.
 * 
 * @author EVE
 * 
 * @param <K>
 *                The key type for the tokens.
 * 
 * @param <V>
 *                The value type for the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 * 
 *
 */
public class PrattParser<K, V, C> {
	private final LeftCommand<K, V, C>	DEFAULT_LEFT_COMMAND	= new DefaultLeftCommand<>();
	private final NullCommand<K, V, C>	DEFAULT_NULL_COMMAND	= new DefaultNullCommand<>();

	private Map<K, LeftCommand<K, V, C>>	leftCommands;
	private Map<K, NullCommand<K, V, C>>	nullCommands;

	/**
	 * Create a new Pratt parser.
	 * 
	 * @param terminal
	 *                The terminal symbol.
	 */
	public PrattParser() {
		leftCommands = new HashMap<>();
		nullCommands = new HashMap<>();
	}

	/**
	 * Parse an expression.
	 * 
	 * @param precedence
	 *                The initial precedence for the expression.
	 * 
	 * @param tokens
	 *                The tokens for the expression.
	 * 
	 * @param state
	 *                The state of the parser.
	 * 
	 * @return The expression as an AST.
	 * 
	 * @throws ParserException
	 *                 If something goes wrong during parsing.
	 */
	public ITree<Token<K, V>> parseExpression(int precedence, TokenStream<K, V> tokens, C state)
			throws ParserException {
		if(precedence < 0) {
			throw new IllegalArgumentException("Precedence must be greater than zero");
		}

		Token<K, V> initToken = tokens.current();
		tokens.next();

		ITree<Token<K, V>> ast = nullCommands.getOrDefault(initToken.getKey(), DEFAULT_NULL_COMMAND)
				.nullDenotation(initToken, new ParserContext<>(tokens, this, state));

		int rightPrec = Integer.MAX_VALUE;

		while(true) {
			Token<K, V> tok = tokens.current();

			K key = tok.getKey();

			LeftCommand<K, V, C> command = leftCommands.getOrDefault(key, DEFAULT_LEFT_COMMAND);
			int leftBind = command.leftBinding();

			if(NumberUtils.between(precedence, rightPrec, leftBind)) {
				tokens.next();

				ast = command.leftDenote(ast, tok, new ParserContext<>(tokens, this, state));
				rightPrec = command.nextBinding();
			} else {
				break;
			}
		}

		return ast;
	}

	/**
	 * Add a non-initial command to this parser.
	 * 
	 * @param marker
	 *                The key that marks the command.
	 * 
	 * @param comm
	 *                The command.
	 */
	public void addNonInitialCommand(K marker, LeftCommand<K, V, C> comm) {
		leftCommands.put(marker, comm);
	}

	/**
	 * Add a initial command to this parser.
	 * 
	 * @param marker
	 *                The key that marks the command.
	 * 
	 * @param comm
	 *                The command.
	 */
	public void addInitialCommand(K marker, NullCommand<K, V, C> comm) {
		nullCommands.put(marker, comm);
	}
}
