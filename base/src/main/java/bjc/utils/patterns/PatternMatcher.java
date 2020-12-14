package bjc.utils.patterns;

import java.util.function.*;

import bjc.functypes.*;

/**
 * Represents a pattern matcher against a series of patterns.
 * 
 * @author Ben Culkin
 *
 * @param <ReturnType> The type returned from matching the patterns.
 * @param <InputType> The type to match against.
 */
@FunctionalInterface
public interface PatternMatcher<ReturnType, InputType> {
	/**
	 * Match an input object against a set of patterns.
	 * 
	 * @param input The object to match against.
	 * 
	 * @return The result of matching against the object.
	 * 
	 * @throws NonExhaustiveMatch If none of the patterns in this set match
	 */
	ReturnType matchFor(InputType input) throws NonExhaustiveMatch;
	
	/**
	 * Create a pattern matcher against a static set of patterns.
	 * 
	 * @param <RetType> The type returned from matching the patterns.
	 * @param <InpType> The type to match against.
	 * 
	 * @param patterns The set of patterns to match on.
	 * 
	 * @return A pattern matcher which matches on the given patterns.
	 */
	@SafeVarargs
	static <RetType, InpType> PatternMatcher<RetType, InpType> matchingOn(
			ComplexPattern<RetType, ?, InpType>... patterns) {
		return new SimplePatternMatcher<>(patterns);
	}
	
	/**
	 * Create a pattern matcher from a handler function.
	 * 
	 * @param <RetType> The type returned by the matcher.
	 * @param <InpType> The type to match against.
	 * 
	 * @param handler The handler function.
	 * 
	 * @return A pattern matcher defined by the given handler.
	 */
	static <RetType, InpType> PatternMatcher<RetType, InpType> from(
			ThrowFunction<InpType, RetType, NonExhaustiveMatch> handler) {
		return new FunctionalPatternMatcher<>(handler);
	}
	
	/**
	 * Create a pattern matcher which applies a transform to its input.
	 * 
	 * @param <NewInput> The new input type to use.
	 * @param transformer The function to convert from the new input to the old input.
	 * 
	 * @return A pattern matcher which takes values of the new type instead.
	 */
	default <NewInput> PatternMatcher<ReturnType, NewInput> transformInput(
			Function<NewInput, InputType> transformer) {
		return from(inp -> matchFor(transformer.apply(inp)));
	}

	/**
	 * Create a pattern matcher which applies a transform to its output.
	 * 
	 * @param <NewOutput> The new output type to use.
	 * 
	 * @param transformer The function to convert from the new output to the old output.
	 * 
	 * @return A pattern matcher which takes values of the new type instead.
	 */
	default <NewOutput> PatternMatcher<NewOutput, InputType> transformOutput(
			Function<ReturnType, NewOutput> transformer) {
		return from(inp -> transformer.apply(matchFor(inp)));
	}
}