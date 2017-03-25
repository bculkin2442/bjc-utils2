package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * A prefix ternary operator, like an if/then/else group.
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
public class PreTernaryCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private Token<K, V> term;

	private ParseBlock<K, V, C> condBlock;

	private ParseBlock<K, V, C>	opblock1;
	private ParseBlock<K, V, C>	opblock2;

	/**
	 * Create a new ternary statement.
	 * 
	 * @param cond
	 *                The block for handling the condition.
	 * 
	 * @param op1
	 *                The block for handling the first operator.
	 * 
	 * @param op2
	 *                The block for handling the second operator.
	 * 
	 * @param term
	 *                The token to use as the node for the AST.
	 */
	public PreTernaryCommand(ParseBlock<K, V, C> cond, ParseBlock<K, V, C> op1, ParseBlock<K, V, C> op2,
			Token<K, V> term) {
		super();

		if (cond == null)
			throw new NullPointerException("Cond block must not be null");
		else if (op1 == null)
			throw new NullPointerException("Op block #1 must not be null");
		else if (op2 == null) throw new NullPointerException("Op block #2 must not be null");

		this.condBlock = cond;
		this.opblock1 = op1;
		this.opblock2 = op2;

		this.term = term;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> cond = condBlock.parse(ctx);

		ITree<Token<K, V>> op1 = opblock1.parse(ctx);

		ITree<Token<K, V>> op2 = opblock2.parse(ctx);

		return new Tree<>(term, cond, op1, op2);
	}
}