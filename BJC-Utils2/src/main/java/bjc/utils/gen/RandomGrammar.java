package bjc.utils.gen;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IFunctionalList;

/**
 * A weighted grammar where all the rules have a equal chance of occuring.
 * 
 * @author ben
 *
 * @param <E>
 *            The type of grammar elements to use.
 */
public class RandomGrammar<E> extends WeightedGrammar<E> {

	/**
	 * Create a new random grammar.
	 */
	public RandomGrammar() {
		rules = new FunctionalMap<>();
	}

	/**
	 * Add cases to a specified rule.
	 * 
	 * @param rule
	 *            The name of the rule to add cases to.
	 * @param cases
	 *            The cases to add for this rule.
	 */
	@SafeVarargs
	public final void addCases(E rule, IFunctionalList<E>... cases) {
		for (IFunctionalList<E> currentCase : cases) {
			super.addCase(rule, 1, currentCase);
		}
	}

	/**
	 * Create a rule with the specified name and cases.
	 * 
	 * @param rule
	 *            The name of the rule to add.
	 * @param cases
	 *            The cases to add for this rule.
	 */
	@SafeVarargs
	public final void makeRule(E rule, IFunctionalList<E>... cases) {
		super.addRule(rule);

		for (IFunctionalList<E> currentCase : cases) {
			super.addCase(rule, 1, currentCase);
		}
	}

	/**
	 * Create a rule with the specified name and cases.
	 * 
	 * @param rule
	 *            The name of the rule to add.
	 * @param cases
	 *            The cases to add for this rule.
	 */
	public void makeRule(E rule,
			IFunctionalList<IFunctionalList<E>> cases) {
		if (cases == null) {
			throw new NullPointerException("Cases must not be null");
		}

		super.addRule(rule);

		cases.forEach(currentCase -> super.addCase(rule, 1, currentCase));
	}
}
