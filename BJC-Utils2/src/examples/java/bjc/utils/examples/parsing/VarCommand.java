package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.StringToken;
import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.commands.AbstractInitialCommand;

class VarCommand extends AbstractInitialCommand<String, String, TestContext> {

	@Override
	protected ITree<Token<String, String>> intNullDenotation(Token<String, String> operator,
			ParserContext<String, String, TestContext> ctx) throws ParserException {
		Token<String, String> name = ctx.tokens.current();

		switch(name.getKey()) {
		case "(literal)":
		case "(vref)":
			ctx.tokens.next();
			break;
		default:
			throw new ParserException("Variable name must be simple");
		}

		ctx.tokens.expect("=");

		ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ctx.state.scopes.top().putKey(name.getValue(), body);

		return new Tree<>(new StringToken("var-bind", "var-bind"), new Tree<>(name), body);
	}

}
