package bjc.utils.dice;

import java.util.Map;

/**
 * A dice expression that refers to a variable bound in a mutable
 * enviroment
 * 
 * @author ben
 *
 */
public class ReferenceDiceExpression implements IDiceExpression {
	/**
	 * The name of the bound variable
	 */
	private String							name;

	/**
	 * The enviroment to do variable dereferencing against
	 */
	private Map<String, IDiceExpression>	env;

	/**
	 * Create a new reference dice expression referring to the given name
	 * in an enviroment
	 * 
	 * @param name
	 *            The name of the bound variable
	 * @param env
	 *            The enviroment to resolve the variable against
	 */
	public ReferenceDiceExpression(String name,
			Map<String, IDiceExpression> env) {
		this.name = name;
		this.env = env;
	}

	@Override
	public int roll() {
		return env.get(name).roll();
	}

	@Override
	public String toString() {
		if (env.containsKey(name)) {
			return env.get(name).toString();
		} else {
			return name;
		}
	}

	/**
	 * Get the name of the referenced variable
	 * 
	 * @return the name of the referenced variable
	 */
	public String getName() {
		return name;
	}
}
