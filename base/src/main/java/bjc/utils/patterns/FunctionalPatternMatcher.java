package bjc.utils.patterns;

import bjc.functypes.*;

/**
 * A simple pattern matcher backed by a function.
 * 
 * @author Ben Culkin
 *
 * @param <ReturnType> The type returned by the matcher.
 * @param <InputType> The type to match against.
 */
public class FunctionalPatternMatcher<ReturnType, InputType>
	implements PatternMatcher<ReturnType, InputType> {
	
	private final ThrowFunction<InputType, ReturnType, NonExhaustiveMatch> matcher;
	
	/**
	 * Create a new function-backed pattern matcher.
	 * 
	 * @param matcher The function backing this matcher.
	 */
	public FunctionalPatternMatcher(ThrowFunction<InputType, ReturnType, NonExhaustiveMatch> matcher) {
		this.matcher = matcher;
	}

	@Override
	public ReturnType matchFor(InputType input) throws NonExhaustiveMatch {
		return matcher.apply(input);
	}
}
