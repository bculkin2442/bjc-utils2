package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class LeafCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> denote(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {

		return new Tree<>(operator);
	}
}