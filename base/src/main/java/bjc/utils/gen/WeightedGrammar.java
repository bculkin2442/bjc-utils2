package bjc.utils.gen;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import bjc.data.IPair;
import bjc.data.Pair;
import bjc.funcdata.FunctionalList;
import bjc.funcdata.FunctionalMap;
import bjc.funcdata.IList;
import bjc.funcdata.IMap;

/**
 * A random grammar, where certain rules will come up more often than others.
 *
 * @author ben
 *
 * @param <E>
 *        The values that make up sentences of this grammar.
 */
public class WeightedGrammar<E> {
	/** The initial rule of the grammar */
	protected String initialRule;

	/** The rules currently in this grammar */
	protected IMap<E, WeightedRandom<IList<E>>> rules;

	/** The random number generator used for random numbers */
	private Random rng;

	/** All of the subgrammars of this grammar */
	protected IMap<E, WeightedGrammar<E>> subgrammars;

	/** Rules that require special handling */
	private IMap<E, Supplier<IList<E>>> specialRules;

	/** Predicate for marking special tokens */
	private Predicate<E> specialMarker;

	/** Action for special tokens */
	private BiFunction<E, WeightedGrammar<E>, IList<E>> specialAction;

	/** Create a new weighted grammar. */
	public WeightedGrammar() {
		rules = new FunctionalMap<>();
		subgrammars = new FunctionalMap<>();
		specialRules = new FunctionalMap<>();
	}

	/**
	 * Create a new weighted grammar that uses the specified source of
	 * randomness.
	 *
	 * @param source
	 *        The source of randomness to use
	 */
	public WeightedGrammar(final Random source) {
		this();

		if(source == null) throw new NullPointerException("Source of randomness must be non-null");

		rng = source;
	}

	/**
	 * Configure the action to perform on special tokens.
	 *
	 * @param marker
	 *        The marker to find special tokens.
	 *
	 * @param action
	 *        The action to take on those tokens.
	 */
	public void configureSpecial(final Predicate<E> marker,
			final BiFunction<E, WeightedGrammar<E>, IList<E>> action) {
		specialMarker = marker;
		specialAction = action;
	}

	/**
	 * Adds a special rule to the grammar.
	 *
	 * @param ruleName
	 *        The name of the special rule.
	 *
	 * @param cse
	 *        The case for the rule.
	 */
	public void addSpecialRule(final E ruleName, final Supplier<IList<E>> cse) {
		if(ruleName == null) {
			throw new NullPointerException("Rule name must not be null");
		} else if(cse == null) {
			throw new NullPointerException("Case must not be null");
		}

		specialRules.put(ruleName, cse);
	}

	/**
	 * Add a case to an already existing rule.
	 *
	 * @param ruleName
	 *        The rule to add a case to.
	 *
	 * @param probability
	 *        The probability for this rule to be chosen.
	 *
	 * @param cse
	 *        The case being added.
	 */
	public void addCase(final E ruleName, final int probability, final IList<E> cse) {
		if(ruleName == null) {
			throw new NullPointerException("Rule name must be not null");
		} else if(cse == null) {
			throw new NullPointerException("Case body must not be null");
		}

		rules.get(ruleName).addProbability(probability, cse);
	}

	/**
	 * Add a alias for an existing subgrammar.
	 *
	 * @param name
	 *        The name of the subgrammar to alias.
	 *
	 * @param alias
	 *        The alias of the subgrammar.
	 *
	 * @return Whether the alias was succesfully created.
	 */
	public boolean addGrammarAlias(final E name, final E alias) {
		if(name == null) {
			throw new NullPointerException("Subgrammar name must not be null");
		} else if(alias == null) {
			throw new NullPointerException("Subgrammar alias must not be null");
		}

		if(subgrammars.containsKey(alias)) return false;

		if(subgrammars.containsKey(name)) {
			subgrammars.put(alias, subgrammars.get(name));
			return true;
		}

		return false;
	}

	/**
	 * Add a new rule with no cases.
	 *
	 * @param name
	 *        The name of the rule to add.
	 *
	 * @return Whether or not the rule was successfully added.
	 */
	public boolean addRule(final E name) {
		if(rng == null) {
			rng = new Random();
		}

		if(name == null) throw new NullPointerException("Rule name must not be null");

		return addRule(name, new WeightedRandom<>(rng));
	}

	/**
	 * Add a new rule with a set of cases.
	 *
	 * @param name
	 *        The name of the rule to add.
	 *
	 * @param cases
	 *        The set of cases for the rule.
	 *
	 * @return Whether or not the rule was succesfully added.
	 */
	public boolean addRule(final E name, final WeightedRandom<IList<E>> cases) {
		if(name == null) {
			throw new NullPointerException("Name must not be null");
		} else if(cases == null) {
			throw new NullPointerException("Cases must not be null");
		}

		if(rules.containsKey(name)) return false;

		rules.put(name, cases);
		return true;
	}

	/**
	 * Add a subgrammar.
	 *
	 * @param name
	 *        The name of the subgrammar.
	 *
	 * @param subgrammar
	 *        The subgrammar to add.
	 *
	 * @return Whether or not the subgrammar was succesfully added.
	 */
	public boolean addSubgrammar(final E name, final WeightedGrammar<E> subgrammar) {
		if(name == null) {
			throw new NullPointerException("Subgrammar name must not be null");
		} else if(subgrammar == null) {
			throw new NullPointerException("Subgrammar must not be null");
		}

		if(subgrammars.containsKey(name)) return false;

		subgrammars.put(name, subgrammar);
		return true;
	}

	/**
	 * Remove a rule with the specified name.
	 *
	 * @param name
	 *        The name of the rule to remove.
	 */
	public void deleteRule(final E name) {
		if(name == null) throw new NullPointerException("Rule name must not be null");

		rules.remove(name);
	}

	/**
	 * Remove a subgrammar with the specified name.
	 *
	 * @param name
	 *        The name of the subgrammar to remove.
	 */
	public void deleteSubgrammar(final E name) {
		if(name == null) throw new NullPointerException("Rule name must not be null");

		subgrammars.remove(name);
	}

	/**
	 * Generate a set of debug sentences for the specified rule.
	 *
	 * Only generates sentences one layer deep.
	 *
	 * @param ruleName
	 *        The rule to test.
	 *
	 * @return A set of sentences generated by the specified rule.
	 */
	public IList<IList<E>> generateDebugValues(final E ruleName) {
		if(ruleName == null) throw new NullPointerException("Rule name must not be null");

		final IList<IList<E>> returnedList = new FunctionalList<>();

		final WeightedRandom<IList<E>> ruleGenerator = rules.get(ruleName);

		for(int i = 0; i < 10; i++) {
			returnedList.add(ruleGenerator.generateValue());
		}

		return returnedList;
	}

	/**
	 * Generate a generic sentence from a initial rule.
	 *
	 * @param <T>
	 *        The type of the transformed output
	 *
	 * @param initRules
	 *        The initial rule to start with.
	 *
	 * @param tokenTransformer
	 *        The function to transform grammar output into something.
	 *
	 * @param spacer
	 *        The spacer element to add in between output tokens.
	 *
	 * @return A randomly generated sentence from the specified initial
	 *         rule.
	 */
	public <T> IList<T> generateGenericValues(final E initRules, final Function<E, T> tokenTransformer,
			final T spacer) {
		if(initRules == null) {
			throw new NullPointerException("Initial rule must not be null");
		} else if(tokenTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		} else if(spacer == null) {
			throw new NullPointerException("Spacer must not be null");
		}

		final IList<T> returnedList = new FunctionalList<>();

		IList<E> genRules = new FunctionalList<>(initRules);

		if(specialMarker != null) {
			if(specialMarker.test(initRules)) {
				genRules = specialAction.apply(initRules, this);
			}
		}

		/*
		 * @NOTE Can this loop be simplified in some way?
		 */
		for(final E initRule : genRules.toIterable()) {
			if(specialRules.containsKey(initRule)) {
				for(final E rulePart : specialRules.get(initRule).get().toIterable()) {
					final Iterable<T> generatedRuleParts = generateGenericValues(rulePart,
							tokenTransformer, spacer).toIterable();

					for(final T generatedRulePart : generatedRuleParts) {
						returnedList.add(generatedRulePart);
						returnedList.add(spacer);
					}
				}
			} else if(subgrammars.containsKey(initRule)) {
				final Iterable<T> ruleParts = subgrammars.get(initRule)
						.generateGenericValues(initRule, tokenTransformer, spacer).toIterable();

				for(final T rulePart : ruleParts) {
					returnedList.add(rulePart);
					returnedList.add(spacer);
				}
			} else if(rules.containsKey(initRule)) {
				final Iterable<E> ruleParts = rules.get(initRule).generateValue().toIterable();

				for(final E rulePart : ruleParts) {
					final Iterable<T> generatedRuleParts = generateGenericValues(rulePart,
							tokenTransformer, spacer).toIterable();

					for(final T generatedRulePart : generatedRuleParts) {
						returnedList.add(generatedRulePart);
						returnedList.add(spacer);
					}
				}
			} else {
				final T transformedToken = tokenTransformer.apply(initRule);

				if(transformedToken == null)
					throw new NullPointerException("Transformer created null token");

				returnedList.add(transformedToken);
				returnedList.add(spacer);
			}
		}

		return returnedList;
	}

	/**
	 * Generate a random list of grammar elements from a given initial rule.
	 *
	 * @param initRule
	 *        The initial rule to start with.
	 *
	 * @param spacer
	 *        The item to use to space the list.
	 *
	 * @return A list of random grammar elements generated by the specified
	 *         rule.
	 */
	public IList<E> generateListValues(final E initRule, final E spacer) {
		final IList<E> retList = generateGenericValues(initRule, strang -> strang, spacer);

		return retList;
	}

	/**
	 * Get the initial rule of this grammar.
	 *
	 * @return The initial rule of this grammar.
	 */
	public String getInitialRule() {
		return initialRule;
	}

	/**
	 * Returns the number of rules in this grammar.
	 *
	 * @return The number of rules in this grammar.
	 */
	public int getRuleCount() {
		return rules.size();
	}

	/**
	 * Returns a set containing all of the rules in this grammar.
	 *
	 * @return The set of all rule names in this grammar.
	 */
	public IList<E> getRuleNames() {
		final IList<E> ruleNames = new FunctionalList<>();

		ruleNames.addAll(rules.keyList());
		ruleNames.addAll(specialRules.keyList());

		return ruleNames;
	}

	/**
	 * Get the subgrammar with the specified name.
	 *
	 * @param name
	 *        The name of the subgrammar to get.
	 *
	 * @return The subgrammar with the specified name.
	 */
	public WeightedGrammar<E> getSubgrammar(final E name) {
		if(name == null) throw new NullPointerException("Subgrammar name must not be null");

		return subgrammars.get(name);
	}

	/**
	 * Check if this grammar has an initial rule
	 *
	 * @return Whether or not this grammar has an initial rule
	 */
	public boolean hasInitialRule() {
		return initialRule != null && !initialRule.equalsIgnoreCase("");
	}

	/**
	 * Check if this grammar has a given rule.
	 *
	 * @param ruleName
	 *        The rule to check for.
	 *
	 * @return Whether or not the grammar has a rule by that name.
	 */
	public boolean hasRule(final E ruleName) {
		return rules.containsKey(ruleName) || specialRules.containsKey(ruleName);
	}

	/**
	 * Prefix a given rule with a token multiple times.
	 *
	 * @param ruleName
	 *        The name of the rule to prefix.
	 *
	 * @param prefixToken
	 *        The token to prefix to the rules.
	 *
	 * @param additionalProbability
	 *        The additional probability of the tokens.
	 *
	 * @param numberOfTimes
	 *        The number of times to prefix the token.
	 */
	public void multiPrefixRule(final E ruleName, final E prefixToken, final int additionalProbability,
			final int numberOfTimes) {
		if(ruleName == null)
			throw new NullPointerException("Rule name must not be null");
		else if(prefixToken == null)
			throw new NullPointerException("Prefix token must not be null");
		else if(numberOfTimes < 1)
			throw new IllegalArgumentException("Number of times to prefix must be positive.");

		final WeightedRandom<IList<E>> rule = rules.get(ruleName);

		final IList<IPair<Integer, IList<E>>> newResults = new FunctionalList<>();

		/*
		 * @NOTE Can this be simplified?
		 */
		rule.getValues().forEach((pair) -> {
			final IList<IList<E>> newRule = new FunctionalList<>();

			for(int i = 1; i <= numberOfTimes; i++) {
				final IList<E> newCase = pair.merge((left, right) -> {
					final IList<E> returnVal = new FunctionalList<>();

					for(final E val : right.toIterable()) {
						returnVal.add(val);
					}

					return returnVal;
				});

				for(int j = 1; j <= i; j++) {
					newCase.prepend(prefixToken);
				}

				newRule.add(newCase);
			}

			newRule.forEach((list) -> {
				final Integer currentProb = pair.merge((left, right) -> left);

				newResults.add(new Pair<>(currentProb + additionalProbability, list));
			});
		});

		newResults.forEach((pair) -> {
			pair.doWith((left, right) -> {
				addCase(ruleName, left, right);
			});
		});
	}

	/**
	 * Create a series of alternatives for a rule by prefixing them with a
	 * given token.
	 *
	 * @param additionalProbability
	 *        The amount to adjust the probability by.
	 *
	 * @param ruleName
	 *        The name of the rule to prefix.
	 *
	 * @param prefixToken
	 *        The token to prefix to the rule.
	 */
	public void prefixRule(final E ruleName, final E prefixToken, final int additionalProbability) {
		if(ruleName == null) {
			throw new NullPointerException("Rule name must not be null");
		} else if(prefixToken == null) {
			throw new NullPointerException("Prefix token must not be null");
		}

		final WeightedRandom<IList<E>> rule = rules.get(ruleName);

		final IList<IPair<Integer, IList<E>>> newResults = new FunctionalList<>();

		rule.getValues().forEach((pair) -> {
			final IList<E> newCase = pair.merge((left, right) -> {
				final IList<E> returnVal = new FunctionalList<>();

				for(final E val : right.toIterable()) {
					returnVal.add(val);
				}

				return returnVal;
			});

			newCase.prepend(prefixToken);

			newResults.add(new Pair<>(pair.merge((left, right) -> left) + additionalProbability, newCase));
		});

		newResults.forEach((pair) -> pair.doWith((left, right) -> addCase(ruleName, left, right)));
	}

	/**
	 * Set the initial rule of the grammar.
	 *
	 * @param initRule
	 *        The initial rule of this grammar.
	 */
	public void setInitialRule(final String initRule) {
		this.initialRule = initRule;
	}

	/**
	 * Suffix a token to a rule.
	 *
	 * @param ruleName
	 *        The rule to suffix.
	 *
	 * @param suffixToken
	 *        The token to prefix to the rule.
	 *
	 * @param additionalProbability
	 *        Additional probability of the prefixed rule.
	 */
	public void suffixRule(final E ruleName, final E suffixToken, final int additionalProbability) {
		if(ruleName == null) {
			throw new NullPointerException("Rule name must not be null");
		} else if(suffixToken == null) {
			throw new NullPointerException("Prefix token must not be null");
		}

		final WeightedRandom<IList<E>> rule = rules.get(ruleName);

		final IList<IPair<Integer, IList<E>>> newResults = new FunctionalList<>();

		rule.getValues().forEach((par) -> {
			final IList<E> newCase = par.merge((left, right) -> {
				final IList<E> returnVal = new FunctionalList<>();

				for(final E val : right.toIterable()) {
					returnVal.add(val);
				}

				return returnVal;
			});

			newCase.add(suffixToken);

			newResults.add(new Pair<>(par.merge((left, right) -> left) + additionalProbability, newCase));
		});

		newResults.forEach((pair) -> pair.doWith((left, right) -> addCase(ruleName, left, right)));
	}
}
