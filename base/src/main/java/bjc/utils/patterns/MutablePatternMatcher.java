package bjc.utils.patterns;

import java.util.*;

import bjc.data.*;

/**
 * A pattern matcher over a mutable set of patterns.
 * 
 * Note that modifying a pattern matcher while it is currently doing pattern
 * matching is a wonderful way to cause strange behavior.
 * 
 * @author Ben Culkin
 *
 * @param <ReturnType> The type returned by the pattern matcher.
 */
public class MutablePatternMatcher<ReturnType, InputType>
	implements IPatternMatcher<ReturnType, InputType>{
	private final List<ComplexPattern<ReturnType, Object, InputType>> patterns;
	
	/**
	 * Create a new mutable pattern matcher with no patterns.
	 */
	public MutablePatternMatcher() {
		patterns = new ArrayList<>();
	}

	/**
	 * Create a new mutable pattern matcher with the given set of patterns.
	 * 
	 * @param patterns The set of patterns to match on.
	 */
	@SuppressWarnings("unchecked")
	public MutablePatternMatcher(ComplexPattern<ReturnType, ?, InputType>... patterns) {
		this();
		
		for (ComplexPattern<ReturnType, ?, InputType> pattern : patterns) {
			// Note: this may seem a somewhat questionable cast, but because we never
			// actually do anything with the value who has a type matching the second
			// parameter, this should be safe
			this.patterns.add((ComplexPattern<ReturnType, Object, InputType>) pattern);
		}
	}
	
	@Override
	public ReturnType matchFor(InputType input) throws NonExhaustiveMatch {
		Iterator<ComplexPattern<ReturnType, Object, InputType>> iterator;
		for (iterator = new NonCMEIterator<>(patterns);
				iterator.hasNext();) {
			ComplexPattern<ReturnType, Object, InputType> pattern = iterator.next();
			
			IPair<Boolean, Object> matches = pattern.matches(input);
			
			if (matches.getLeft()) {
				pattern.apply(input, matches.getRight());
			}
		}

		throw new NonExhaustiveMatch("Non-exhaustive match against " + input);
	}

	/**
	 * Add a pattern to this pattern matcher.
	 * 
	 * @param pattern The pattern to add.
	 * 
	 * @return Whether or not the pattern was added.
	 */
	@SuppressWarnings("unchecked")
	public boolean addPattern(ComplexPattern<ReturnType, ?, InputType> pattern) {
		return patterns.add((ComplexPattern<ReturnType, Object, InputType>) pattern);
	}
	
	/**
	 * Remove a pattern from this pattern matcher.
	 * 
	 * @param pattern The pattern to remove.
	 * 
	 * @return Whether or not the pattern was removed.
	 */
	public boolean removePattern(ComplexPattern<ReturnType, ?, InputType> pattern) {
		return patterns.remove(pattern);
	}
}
