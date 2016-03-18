package bjc.utils.dice;

/*
 * Enumeration for basic dice expression operators
 */
public enum DiceExpressionType {
	ADD, DIVIDE, MULTIPLY, SUBTRACT;

	public String toString() {
		switch (this) {
			case ADD:
				return "+";
			case DIVIDE:
				return "/";
			case MULTIPLY:
				return "*";
			case SUBTRACT:
				return "-";
			default:
				throw new IllegalArgumentException(
						"Got passed  a invalid ScalarExpressionType "
								+ this + ". WAT");
		}
	};
}