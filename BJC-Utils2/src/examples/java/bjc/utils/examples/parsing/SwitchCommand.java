package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.tokens.StringToken;

class SwitchCommand implements InitialCommand<String, String, TestContext> {
	@Override
	public ITree<Token<String, String>> denote(Token<String, String> operator,
			ParserContext<String, String, TestContext> ctx) throws ParserException {
		ITree<Token<String, String>> object = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		return new Tree<>(new StringToken("switch", "switch"), object, body);
	}
}
