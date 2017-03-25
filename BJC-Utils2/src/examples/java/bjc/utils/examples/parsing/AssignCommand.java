package bjc.utils.examples.parsing;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.commands.NonBinaryCommand;
import bjc.utils.parserutils.pratt.tokens.StringToken;

class AssignCommand extends NonBinaryCommand<String, String, TestContext> {
	public AssignCommand() {
		super(10);
	}

	@Override
	public ITree<Token<String, String>> denote(ITree<Token<String, String>> operand, Token<String, String> operator,
			ParserContext<String, String, TestContext> ctx) throws ParserException {
		Token<String, String> name = operand.getHead();

		switch(name.getKey()) {
		case "(literal)":
		case "(vref)":
			break;
		default:
			throw new ParserException("Variable name must be simple");
		}

		ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ctx.state.scopes.top().putKey(name.getValue(), body);

		return new Tree<>(new StringToken("assign", "assign"), operand, body);
	}
}
