package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class PostfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	public PostfixCommand(int leftPower) {
		super(leftPower);
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException {
		return new Tree<>(operator, operand);
	}
}