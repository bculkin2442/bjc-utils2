package bjc.utils.dice.ast;

public class VariableDiceNode implements IDiceASTNode {
	private String var;

	public VariableDiceNode(String data) {
		this.var = data;
	}

	@Override
	public boolean isOperator() {
		return false;
	}

	/**
	 * Get the variable referenced by this AST node
	 * 
	 * @return the variable referenced by this AST node
	 */
	public String getVariable() {
		return var;
	}
	
	@Override
	public String toString() {
		return var;
	}
}
