package bjc.utils.patterns;

import bjc.data.*;

/**
 * A simpler form of a pattern.
 * 
 * @author Ben Culkin
 *
 * @param <ReturnType> The type returned by matching the pattern.
 */
public interface SimplePatttern<ReturnType> extends Pattern<ReturnType, Void> {
	/**
	 * Test if this pattern does match a given object.
	 * 
	 * @param input The object to test against.
	 * 
	 * @return Whether the object matches this pattern.
	 */
	boolean doesMatch(Object input);

	/**
	 * Applies this pattern to the input object.
	 * 
	 * @param input The object that passed the condition.
	 * 
	 * @return The result of applying this action to the input.
	 */
	ReturnType doApply(Object input);
	
	@Override
	default ReturnType apply(Object input, Void state) {
		return doApply(input);
	}
	
	@Override
	default Pair<Boolean, Void> matches(Object input) {
		return new SimplePair<>(doesMatch(input), null);
	}
}