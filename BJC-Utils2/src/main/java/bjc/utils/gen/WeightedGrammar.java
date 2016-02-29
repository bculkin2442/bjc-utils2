package bjc.utils.gen;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import bjc.utils.data.Pair;
import bjc.utils.funcdata.FunctionalList;

/**
 * A random grammar, where certain rules will come up more often than
 * others.
 * 
 * @author ben
 *
 * @param <E>
 *            The values that make up sentances of this grammar.
 */
public class WeightedGrammar<E> {
	/**
	 * The initial rule of the grammar
	 */
	protected String									initRule;

	/**
	 * The rules currently in this grammar
	 */
	protected Map<E, WeightedRandom<FunctionalList<E>>>	rules;

	/**
	 * The random number generator used for random numbers
	 */
	private Random										sr;

	/**
	 * All of the subgrammars of this grammar
	 */
	protected Map<E, WeightedGrammar<E>>				subgrammars;

	/**
	 * Create a new weighted grammar.
	 */
	public WeightedGrammar() {
		rules = new Hashtable<>();
		subgrammars = new Hashtable<>();
	}

	/**
	 * Create a new weighted grammar that uses the specified source of
	 * randomness.
	 */
	public WeightedGrammar(Random src) {
		this();

		sr = src;
	}

	/**
	 * Add a case to an already existing rule.
	 * 
	 * @param rule
	 *            The rule to add a case to.
	 * @param prob
	 *            The probability for this rule to be chosen.
	 * @param cse
	 *            The case being added.
	 */
	public void addCase(E rule, int prob, FunctionalList<E> cse) {
		rules.get(rule).addProb(prob, cse);
	}

	/**
	 * Add a alias for an existing subgrammar
	 * 
	 * @param name
	 *            The name of the subgrammar to alias
	 * @param alias
	 *            The alias of the subgrammar
	 * @return Whether the alias was succesfully created
	 */
	public boolean addGrammarAlias(E name, E alias) {
		if (subgrammars.containsKey(alias)) {
			return false;
		} else {
			if (subgrammars.containsKey(name)) {
				subgrammars.put(alias, subgrammars.get(name));
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Add a new rule with no cases.
	 * 
	 * @param name
	 *            The name of the rule to add.
	 * @return Whether or not the rule was succesfully added.
	 */
	public boolean addRule(E name) {
		if (sr == null) {
			sr = new Random();
		}
		return addRule(name, new WeightedRandom<>(sr));
	}

	/**
	 * Add a new rule with a set of cases.
	 * 
	 * @param name
	 *            The name of the rule to add.
	 * @param rnd
	 *            The set of cases for the rule.
	 * @return Whether or not the rule was succesfully added.
	 */
	public boolean addRule(E name, WeightedRandom<FunctionalList<E>> rnd) {
		if (rules.containsKey(name)) {
			return false;
		} else {
			rules.put(name, rnd);
			return true;
		}
	}

	/**
	 * Add a subgrammar.
	 * 
	 * @param name
	 *            The name of the subgrammar.
	 * @param subG
	 *            The subgrammar to add.
	 * @return Whether or not the subgrammar was succesfully added.
	 */
	public boolean addSubGrammar(E name, WeightedGrammar<E> subG) {
		if (subgrammars.containsKey(name)) {
			return false;
		} else {
			subgrammars.put(name, subG);
			return true;
		}
	}

	/**
	 * Generate a set of debug sentences for the specified rule. Only
	 * generates sentances one layer deep.
	 * 
	 * @param rl
	 *            The rule to test.
	 * @return A set of sentances generated by the specified rule.
	 */
	public FunctionalList<FunctionalList<E>> debugVals(E rl) {
		FunctionalList<FunctionalList<E>> fl = new FunctionalList<>();

		WeightedRandom<FunctionalList<E>> random = rules.get(rl);

		for (int i = 0; i < 10; i++) {
			fl.add(random.genVal());
		}

		return fl;
	}

	/**
	 * Generate a generic sentance from a initial rule.
	 * 
	 * @param initRule
	 *            The initial rule to start with.
	 * @param f
	 *            The function to transform grammar output into something.
	 * @param spacer
	 *            The spacer element to add in between output tokens.
	 * @return A randomly generated sentance from the specified initial
	 *         rule.
	 */
	public <T> FunctionalList<T> genGeneric(E initRule, Function<E, T> f,
			T spacer) {
		FunctionalList<T> r = new FunctionalList<>();

		if (subgrammars.containsKey(initRule)) {
			subgrammars.get(initRule).genGeneric(initRule, f, spacer)
					.forEach(rp -> {
						r.add(rp);
						r.add(spacer);
					});
		} else if (rules.containsKey(initRule)) {
			rules.get(initRule).genVal().forEach(
					rp -> genGeneric(rp, f, spacer).forEach(rp2 -> {
						r.add(rp2);
						r.add(spacer);
					}));
		} else {
			r.add(f.apply(initRule));
			r.add(spacer);
		}

		return r;
	}

	/**
	 * Generate a random list of grammar elements from a given initial
	 * rule.
	 * 
	 * @param initRule
	 *            The initial rule to start with.
	 * @param spacer
	 *            The item to use to space the list.
	 * @return A list of random grammar elements generated by the specified
	 *         rule.
	 */
	public FunctionalList<E> genList(E initRule, E spacer) {
		return genGeneric(initRule, s -> s, spacer);
	}

	public String getInitRule() {
		return initRule;
	}

	/**
	 * Get the subgrammar with the specified name.
	 * 
	 * @param name
	 *            The name of the subgrammar to get.
	 * @return The subgrammar with the specified name.
	 */
	public WeightedGrammar<E> getSubGrammar(E name) {
		return subgrammars.get(name);
	}

	/**
	 * Check if this grammar has an initial rule
	 * 
	 * @return Whether or not this grammar has an initial rule
	 */
	public boolean hasInitRule() {
		return initRule != null && !initRule.equalsIgnoreCase("");
	}

	/**
	 * Prefix a given rule with a token multiple times
	 * 
	 * @param rName
	 *            The name of the rule to prefix
	 * @param prefixToken
	 *            The token to prefix to the rules
	 * @param addProb
	 *            The additional probability of the tokens
	 * @param nTimes
	 *            The number of times to prefix the token
	 */
	public void multiPrefixRule(E rName, E prefixToken, int addProb,
			int nTimes) {
		WeightedRandom<FunctionalList<E>> rule = rules.get(rName);

		FunctionalList<Pair<Integer, FunctionalList<E>>> newResults = new FunctionalList<>();

		rule.getValues().forEach((par) -> {
			FunctionalList<FunctionalList<E>> nls = new FunctionalList<>();

			// TODO bugtest this. if it works, write multiSuffixWith
			for (int i = 1; i <= nTimes; i++) {
				FunctionalList<E> nl = par
						.merge((left, right) -> right.clone());

				for (int j = 1; j <= i; j++) {
					nl.prepend(prefixToken);
				}

				nls.add(nl);
			}

			nls.forEach((ls) -> newResults.add(new Pair<>(
					par.merge((left, right) -> left) + addProb, ls)));
		});

		newResults.forEach((par) -> par
				.doWith((left, right) -> addCase(rName, left, right)));
	}

	/**
	 * Create a series of alternatives for a rule by prefixing them with a
	 * given token
	 * 
	 * @param addProb
	 *            The amount to adjust the probability by
	 * @param rName
	 *            The name of the rule to prefix
	 * @param prefixToken
	 *            The token to prefix to the rule
	 */
	public void prefixRule(E rName, E prefixToken, int addProb) {
		WeightedRandom<FunctionalList<E>> rule = rules.get(rName);

		FunctionalList<Pair<Integer, FunctionalList<E>>> newResults = new FunctionalList<>();

		rule.getValues().forEach((par) -> {
			FunctionalList<E> nl = par
					.merge((left, right) -> right.clone());
			nl.prepend(prefixToken);

			newResults.add(new Pair<>(
					par.merge((left, right) -> left) + addProb, nl));
		});

		newResults.forEach((par) -> par
				.doWith((left, right) -> addCase(rName, left, right)));
	}

	/**
	 * Remove a rule with the specified name.
	 * 
	 * @param name
	 *            The name of the rule to remove.
	 */
	public void removeRule(E name) {
		rules.remove(name);
	}

	/**
	 * Remove a subgrammar with the specified name.
	 * 
	 * @param name
	 *            The name of the subgrammar to remove.
	 */
	public void removeSubgrammar(E name) {
		subgrammars.remove(name);
	}

	/**
	 * Returns the number of rules in this grammar
	 * 
	 * @return The number of rules in this grammar
	 */
	public int ruleCount() {
		return rules.size();
	}

	/**
	 * Returns a set containing all of the rules in this grammar
	 * 
	 * @return The set of all rule names in this grammar
	 */
	public Set<E> ruleNames() {
		return rules.keySet();
	}

	/**
	 * Set the initial rule of the graphic
	 * 
	 * @param initRule
	 */
	public void setInitRule(String initRule) {
		this.initRule = initRule;
	}

	/**
	 * Suffix a token to a rule
	 * 
	 * @param rName
	 *            The rule to suffix
	 * @param prefixToken
	 *            The token to prefix to the rule
	 * @param addProb
	 *            Additional probability of the prefixed rule
	 */
	public void suffixRule(E rName, E prefixToken, int addProb) {
		WeightedRandom<FunctionalList<E>> rule = rules.get(rName);

		FunctionalList<Pair<Integer, FunctionalList<E>>> newResults = new FunctionalList<>();

		rule.getValues().forEach((par) -> {
			FunctionalList<E> nl = par
					.merge((left, right) -> right.clone());
			nl.add(prefixToken);

			newResults.add(new Pair<>(
					par.merge((left, right) -> left) + addProb, nl));
		});

		newResults.forEach((par) -> par
				.doWith((left, right) -> addCase(rName, left, right)));
	}
}
