package bjc.utils.parserutils.splitterv2;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A token splitter that will not split certain tokens.
 * 
 * @author EVE
 *
 */
public class ExcludingTokenSplitter implements TokenSplitter {
	private Set<String> literalExclusions;

	private IList<Predicate<String>> predExclusions;

	private TokenSplitter spliter;

	/**
	 * Create a new excluding token splitter.
	 * 
	 * @param splitter
	 *                The splitter to apply to non-excluded strings.
	 */
	public ExcludingTokenSplitter(TokenSplitter splitter) {
		spliter = splitter;

		literalExclusions = new HashSet<>();

		predExclusions = new FunctionalList<>();
	}

	/**
	 * Exclude literal strings from splitting.
	 * 
	 * @param exclusions
	 *                The strings to exclude from splitting.
	 */
	public final void addLiteralExclusions(String... exclusions) {
		for (String exclusion : exclusions) {
			literalExclusions.add(exclusion);
		}
	}

	/**
	 * Exclude all of the strings matching any of the predicates from
	 * splitting.
	 * 
	 * @param exclusions
	 *                The predicates to use for exclusions.
	 */
	@SafeVarargs
	public final void addPredicateExclusion(Predicate<String>... exclusions) {
		for (Predicate<String> exclusion : exclusions) {
			predExclusions.add(exclusion);
		}
	}

	@Override
	public IList<String> split(String input) {
		if (literalExclusions.contains(input)) {
			return new FunctionalList<>(input);
		} else if (predExclusions.anyMatch(pred -> pred.test(input))) {
			return new FunctionalList<>(input);
		} else {
			return spliter.split(input);
		}
	}
}
