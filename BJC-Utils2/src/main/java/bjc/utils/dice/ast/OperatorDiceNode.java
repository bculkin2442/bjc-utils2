package bjc.utils.dice.ast;

public enum OperatorDiceNode implements IDiceASTNode {
	ASSIGN, ADD, SUBTRACT, MULTIPLY, DIVIDE, GROUP, COMPOUND;

	@Override
	public boolean isOperator() {
		return true;
	}
	
	public static OperatorDiceNode fromString(String s) {
		switch(s) {
			case ":=":
				return ASSIGN;
			case "+":
				return ADD;
			case "-":
				return SUBTRACT;
			case "*":
				return MULTIPLY;
			case "/":
				return DIVIDE;
			case "d":
				return GROUP;
			case "c":
				return COMPOUND;
			default:
				throw new IllegalArgumentException(s + " is not a valid operator node");
		}
	}
}
