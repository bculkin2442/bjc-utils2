package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;

class DefaultLeftCommand<K, V, C> extends LeftCommand<K, V, C> {

	@Override
	public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx) {
		throw new UnsupportedOperationException("Default command has no left denotation");
	}

	@Override
	public int leftBinding() {
		return -1;
	}

}
