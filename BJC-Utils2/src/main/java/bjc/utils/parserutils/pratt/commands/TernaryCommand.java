package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * A ternary command, like C's ?:
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 * 
 * @param <V>
 *                The value type of the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class TernaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private ParseBlock<K, V, C> innerBlck;

	private Token<K, V> mark;

	private boolean nonassoc;

	/**
	 * Create a new ternary command.
	 * 
	 * @param precedence
	 *                The precedence of this operator.
	 * 
	 * @param innerBlock
	 *                The representation of the inner block of the
	 *                expression.
	 * 
	 * @param marker
	 *                The token to use as the root of the AST node.
	 * 
	 * @param isNonassoc
	 *                Whether or not the conditional is associative.
	 */
	public TernaryCommand(int precedence, ParseBlock<K, V, C> innerBlock, Token<K, V> marker, boolean isNonassoc) {
		super(precedence);

		if (innerBlock == null)
			throw new NullPointerException("Inner block must not be null");
		else if (marker == null) throw new NullPointerException("Marker must not be null");

		innerBlck = innerBlock;
		mark = marker;
		nonassoc = isNonassoc;
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> inner = innerBlck.parse(ctx);

		ITree<Token<K, V>> outer = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state, false);

		return new Tree<>(mark, inner, operand, outer);
	}

	@Override
	public int nextBinding() {
		if (nonassoc) {
			return leftBinding() - 1;
		} else {
			return leftBinding();
		}
	}
}