package bjc.utils.dice.ast;

public class LiteralDiceNode implements IDiceASTNode {
	private String data;

	public LiteralDiceNode(String data) {
		this.data = data;
	}

	@Override
	public boolean isOperator() {
		return false;
	}

	/**
	 * Get the data stored in this AST node
	 * 
	 * @return the data stored in this AST node
	 */
	public String getData() {
		return data;
	}
}
