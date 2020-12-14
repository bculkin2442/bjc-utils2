package bjc.utils.patterns;

import java.util.function.*;
import java.util.regex.*;

import bjc.data.*;

/**
 * A pattern that can be matched against.
 * 
 * @author Ben Culkin
 *
 * @param <InputType> The type of object being matched against.
 * @param <ReturnType> The type returned by the pattern.
 * @param <PredType> The state type returned by the predicate.
 */
public interface ComplexPattern<ReturnType, PredType, InputType> {
	/**
	 * Whether or not the given input matches this pattern.
	 * 
	 * @param input The object to check against this pattern.
	 * 
	 * @return Whether or not this pattern is matched, as well as a state value
	 *         that will get passed to the pattern if it did match.
	 */
	Pair<Boolean, PredType> matches(InputType input);
	
	/**
	 * Apply this pattern, once it has matched.
	 * 
	 * @param input The object to apply this pattern to.
	 * @param state The state from the matcher.
	 * 
	 * @return The result of applying this pattern.
	 */
	ReturnType apply(InputType input, PredType state);
	
	/* Pattern producing functions */
	
	/**
	 * Create a pattern composed from a predicate & a function.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <PreType> The type used as intermediate state.
	 * @param <InpType> The type initially matched against.
	 * 
	 * @param matcher The predicate that says what this pattern matches.
	 * @param accepter The action that happens when this pattern matches.
	 * 
	 * @return A pattern composed from the passed in functions.
	 */
	static <RetType, PreType, InpType> ComplexPattern<RetType, PreType, InpType> from(
			Function<InpType, Pair<Boolean, PreType>> matcher,
			BiFunction<InpType, PreType, RetType> accepter)
	{
		return new FunctionalPattern<>(matcher, accepter);
	}
	
	/**
	 * Create a pattern which checks if an object is of a given type (or a subtype of it).
	 * 
	 * @param <ClassType> The type to check if the object is an instance of.
	 * @param <RetType> The type returned by the action.
	 * @param <InpType> The type of the thing to match.
	 * 
	 * @param clasz The Class instance for the type you want to check.
	 * @param action The action to execute if the pattern does match.
	 * 
	 * @return A pattern which follows the specified condition.
	 */
	@SuppressWarnings("unchecked")
	static <ClassType, RetType, InpType> ComplexPattern<RetType, ?, InpType> ofClass(
			Class<ClassType> clasz,
			Function<ClassType, RetType> action) 
	{
		return from(
				(input)          -> Pair.pair(clasz.isInstance(input), null),
				(input, ignored) -> action.apply((ClassType)input)
		);
	}
	
	/**
	 * Creates a pattern which matches a given object.
	 * 
	 * @param <RetType> The type returned when the pattern matches.
	 * @param <InpType> The type of the thing to match.
	 * 
	 * @param obj The object being tested for equality.
	 * @param action The action to execute when the object matches.
	 * 
	 * @return A pattern which tests against the equality of an object.
	 */
	static <RetType, InpType> ComplexPattern<RetType, ?, InpType> matchesObject(
			InpType obj,
			Function<InpType, RetType> action
			) 
	{
		return from(
				(input)          -> Pair.pair(obj.equals(input), null),
				(input, ignored) -> action.apply(input)
		);
	}
	
	/**
	 * Tests if the toString rendition of an object matches a given condition.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <InpType> The type of the thing to match.
	 * 
	 * @param pattern The string to check against.
	 * @param action The action to check when the toString of the object matches
	 *               the provided string. This is passed both the object, and its
	 *               string form (in the event that you don't want to call toString
	 *               multiple times, for whatever reason)
	 *               
	 * @return A pattern which tests against the toString representation of an object.
	 */
	static <RetType, InpType> ComplexPattern<RetType, ?, InpType> equalsString(
			String pattern,
			BiFunction<InpType, String, RetType> action
			) 
	{
		Function<InpType, Pair<Boolean, String>> matcher = (input) -> {
			String objString = input.toString();
			
			return Pair.pair(pattern.equals(objString), objString);
		};
		
		return from(
				matcher,
				(input, objString) -> action.apply(input, objString)
		);
	}
	
	/**
	 * Check if the toString of a given object matches a regex.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <InpType> The type of object to match against. 
	 * 
	 * @param regex The regex to match against.
	 * @param cond The predicate to use to determine if the regex matched.
	 * @param action The action to call when the regex matched.
	 * 
	 * @return A pattern which does the regex matching.
	 */
	static <RetType, InpType> ComplexPattern<RetType, Matcher, InpType> matchesRegex(
		String regex,
		Predicate<Matcher> cond,
		BiFunction<InpType, Matcher, RetType> action
	)
	{
		java.util.regex.Pattern regexPat = java.util.regex.Pattern.compile(regex);

		Function<InpType, Pair<Boolean, Matcher>> matcher = (input) -> {
			String inpString = input.toString();
		
			Matcher mat = regexPat.matcher(inpString);
			
			if (cond.test(mat)) return Pair.pair(true, mat);
			else                return Pair.pair(false, null);
		};
		
		return from(
				matcher,
				(input, res) -> action.apply(input, res)
		);
	}
	
	// @TODO Nov 21, 2020 Ben Culkin :MorePatterns
	// Try and write something to iterate over Iterator in a type-safe manner
	// Also, something for doing a sub-pattern match
	
	/**
	 * Create a pattern which will always execute.
	 * 
	 * @param <RetType> The type returned.
	 * @param <InpType> The type being matched against.
	 * 
	 * @param action The action to execute.
	 * 
	 * @return A pattern which will be executed.
	 */
	static <RetType, InpType> ComplexPattern<RetType, ?, InpType> otherwise(
			Function<InpType, RetType> action
			) 
	{
		return from(
				(input)          -> Pair.pair(true, null),
				(input, ignored) -> action.apply(input)
		);
	}
	
	/**
	 * Create a pattern which checks if the string form of a given object starts
	 * with a specific string.
	 * 
	 * @param <RetType> The type returned by the matcher.
	 * @param <InpType> The type being matched against.
	 * 
	 * @param pattern The string to check against.
	 * @param action The action to execute.
	 * 
	 * @return A pattern which functions as described.
	 */
	static <RetType, InpType> ComplexPattern<RetType, String, InpType> startsWith(
			String pattern,
			Function<String, RetType> action)
	{
		return from((input) -> {
			String objString = input.toString();
			
			if (objString.startsWith(pattern)) {
				return Pair.pair(
						true,
						objString.substring(
							pattern.length()));
			} else {
				return Pair.pair(false, null);
			}
		}, (ignored, input) -> action.apply(input));
	}
}