package bjc.utils.parserutils.splitter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.ListEx;

/**
 * A token splitter that will not split certain tokens.
 *
 * @author EVE
 *
 */
public class ExcludingTokenSplitter implements TokenSplitter {
	private final Set<String> literalExclusions;
	private final ListEx<Predicate<String>> predExclusions;

	private final TokenSplitter spliter;

	/**
	 * Create a new excluding token splitter.
	 *
	 * @param splitter
	 *                 The splitter to apply to non-excluded strings.
	 */
	public ExcludingTokenSplitter(final TokenSplitter splitter) {
		spliter = splitter;

		literalExclusions = new HashSet<>();

		predExclusions = new FunctionalList<>();
	}

	/**
	 * Exclude literal strings from splitting.
	 *
	 * @param exclusions
	 *                   The strings to exclude from splitting.
	 */
	public final void addLiteralExclusions(final String... exclusions) {
		for (final String exclusion : exclusions) {
			literalExclusions.add(exclusion);
		}
	}

	/**
	 * Exclude all of the strings matching any of the predicates from splitting.
	 *
	 * @param exclusions
	 *                   The predicates to use for exclusions.
	 */
	@SafeVarargs
	public final void addPredicateExclusion(final Predicate<String>... exclusions) {
		for (final Predicate<String> exclusion : exclusions) {
			predExclusions.add(exclusion);
		}
	}

	@Override
	public ListEx<String> split(final String input) {
		if (literalExclusions.contains(input))
			return new FunctionalList<>(input);
		else if (predExclusions.anyMatch(pred -> pred.test(input)))
			return new FunctionalList<>(input);
		else
			return spliter.split(input);
	}
}
