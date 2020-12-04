package bjc.utils.patterns;

import bjc.data.*;

/**
 * Implements pattern-matching (of a sort) against a collection of patterns.
 * 
 * @author Ben Culkin
 * 
 * @param <ReturnType> The type returned by the pattern.
 */
public class SimplePatternMatcher<ReturnType, InputType>
	implements PatternMatcher<ReturnType, InputType> {
	private final ComplexPattern<ReturnType, Object, InputType>[] patterns;
	
	/**
	 * Create a new pattern matcher.
	 * 
	 * @param patterns The set of patterns to match against.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public SimplePatternMatcher(ComplexPattern<ReturnType, ?, InputType>...patterns) {
		// Note: this may seem a somewhat questionable cast, but because we never
		// actually do anything with the value who has a type matching the second
		// parameter, this should be safe
		this.patterns = (ComplexPattern<ReturnType, Object, InputType>[]) patterns;
	}

	@Override
	public ReturnType matchFor(InputType input) throws NonExhaustiveMatch {
		for (ComplexPattern<ReturnType, Object, InputType> pattern : patterns) {
			Pair<Boolean, Object> matches = pattern.matches(input);
			if (matches.getLeft()) {
				pattern.apply(input, matches.getRight());
			}
		}
		
		throw new NonExhaustiveMatch("Non-exhaustive match against " + input);
	}
}
