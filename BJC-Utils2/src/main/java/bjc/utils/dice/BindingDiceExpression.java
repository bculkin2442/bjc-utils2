package bjc.utils.dice;

import java.util.Map;

/**
 * A variable expression that represents binding a variable to a name in an
 * enviroment
 * 
 * @author ben
 *
 */
public class BindingDiceExpression implements IDiceExpression {
	private String			name;
	private IDiceExpression	exp;

	/**
	 * Create a new dice expression binder
	 * 
	 * @param name
	 *            The name of the variable to bind
	 * @param exp
	 *            The expression to bind to the variable
	 * @param env
	 *            The enviroment to bind it in
	 */
	public BindingDiceExpression(String name, IDiceExpression exp,
			Map<String, IDiceExpression> env) {
		this.name = name;
		this.exp = exp;

		env.put(name, exp);
	}

	public BindingDiceExpression(IDiceExpression left,
			IDiceExpression right, Map<String, IDiceExpression> env) {
		this(((ReferenceDiceExpression) left).getName(), right, env);
	}

	@Override
	public int roll() {
		return exp.roll();
	}

	@Override
	public String toString() {
		return "assign[n=" + name + ", exp=" + exp.toString() + "]";
	}
}
