package bjc.utils.patterns;

import java.util.Optional;
import java.util.function.*;
import java.util.regex.*;

import bjc.data.*;
import bjc.functypes.ID;
import bjc.functypes.Unit;

/**
 * A pattern that can be matched against.
 * 
 * @author Ben Culkin
 *
 * @param <InputType>  The type of object being matched against.
 * @param <ReturnType> The type returned by the pattern.
 * @param <PredType>   The state type returned by the predicate.
 */
public interface ComplexPattern<ReturnType, PredType, InputType> {
	/**
	 * Whether or not the given input matches this pattern.
	 * 
	 * @param input The object to check against this pattern.
	 * 
	 * @return Whether or not this pattern is matched, as well as a state value that
	 *         will get passed to the pattern if it did match.
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
	 * Create a pattern composed from a predicate &amp; a function.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <PreType> The type used as intermediate state.
	 * @param <InpType> The type initially matched against.
	 * 
	 * @param matcher   The predicate that says what this pattern matches.
	 * @param accepter  The action that happens when this pattern matches.
	 * 
	 * @return A pattern composed from the passed in functions.
	 */
	static <RetType, PreType, InpType> ComplexPattern<RetType, PreType, InpType> from(
			Function<InpType, Pair<Boolean, PreType>> matcher, BiFunction<InpType, PreType, RetType> accepter) {
		return new FunctionalPattern<>(matcher, accepter);
	}

	/**
	 * Create a pattern which checks if an object is of a given type (or a subtype
	 * of it).
	 * 
	 * @param <ClassType> The type to check if the object is an instance of.
	 * @param <RetType>   The type returned by the action.
	 * @param <InpType>   The type of the thing to match.
	 * 
	 * @param clasz       The Class instance for the type you want to check.
	 * @param action      The action to execute if the pattern does match.
	 * 
	 * @return A pattern which follows the specified condition.
	 */
	@SuppressWarnings("unchecked")
	static <ClassType, RetType, InpType> ComplexPattern<RetType, Unit, InpType> ofClass(Class<ClassType> clasz,
			Function<ClassType, RetType> action) {
		return from((input) -> Pair.pair(clasz.isInstance(input), null),
				(input, ignored) -> action.apply((ClassType) input));
	}

	/**
	 * Creates a pattern which matches a given object.
	 * 
	 * @param <RetType> The type returned when the pattern matches.
	 * @param <InpType> The type of the thing to match.
	 * 
	 * @param obj       The object being tested for equality.
	 * @param action    The action to execute when the object matches.
	 * 
	 * @return A pattern which tests against the equality of an object.
	 */
	static <RetType, InpType> ComplexPattern<RetType, Unit, InpType> matchesObject(InpType obj,
			Function<InpType, RetType> action) {
		return from((input) -> Pair.pair(obj.equals(input), null), (input, ignored) -> action.apply(input));
	}

	/**
	 * Tests if the toString rendition of an object matches a given condition.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <InpType> The type of the thing to match.
	 * 
	 * @param pattern   The string to check against.
	 * @param action    The action to check when the toString of the object matches
	 *                  the provided string. This is passed both the object, and its
	 *                  string form (in the event that you don't want to call
	 *                  toString multiple times, for whatever reason)
	 * 
	 * @return A pattern which tests against the toString representation of an
	 *         object.
	 */
	static <RetType, InpType> ComplexPattern<RetType, String, InpType> equalsString(String pattern,
			BiFunction<InpType, String, RetType> action) {
		Function<InpType, Pair<Boolean, String>> matcher = (input) -> {
			String objString = input.toString();

			return Pair.pair(pattern.equals(objString), objString);
		};

		return from(matcher, (input, objString) -> action.apply(input, objString));
	}

	/**
	 * Check if the toString of a given object matches a regex.
	 * 
	 * @param <RetType> The type returned by the pattern.
	 * @param <InpType> The type of object to match against.
	 * 
	 * @param regex     The regex to match against.
	 * @param cond      The predicate to use to determine if the regex matched.
	 * @param action    The action to call when the regex matched.
	 * 
	 * @return A pattern which does the regex matching.
	 */
	static <RetType, InpType> ComplexPattern<RetType, Matcher, InpType> matchesRegex(String regex,
			Predicate<Matcher> cond, BiFunction<InpType, Matcher, RetType> action) {
		java.util.regex.Pattern regexPat = java.util.regex.Pattern.compile(regex);

		Function<InpType, Pair<Boolean, Matcher>> matcher = (input) -> {
			String inpString = input.toString();

			Matcher mat = regexPat.matcher(inpString);

			if (cond.test(mat))
				return Pair.pair(true, mat);
			return Pair.pair(false, null);
		};

		return from(matcher, (input, res) -> action.apply(input, res));
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
	 * @param action    The action to execute.
	 * 
	 * @return A pattern which will be executed.
	 */
	static <RetType, InpType> ComplexPattern<RetType, Unit, InpType> otherwise(Function<InpType, RetType> action) {
		return from((input) -> Pair.pair(true, null), (input, ignored) -> action.apply(input));
	}

	/**
	 * Create a pattern which checks if the string form of a given object starts
	 * with a specific string.
	 * 
	 * @param <RetType> The type returned by the matcher.
	 * @param <InpType> The type being matched against.
	 * 
	 * @param pattern   The string to check against.
	 * @param action    The action to execute.
	 * 
	 * @return A pattern which functions as described.
	 */
	static <RetType, InpType> ComplexPattern<RetType, String, InpType> startsWith(String pattern,
			Function<String, RetType> action) {
		return from((input) -> {
			String objString = input.toString();

			if (objString.startsWith(pattern))
				return Pair.pair(true, objString.substring(pattern.length()));
			return Pair.pair(false, null);
		}, (input, state) -> action.apply(state));
	}

	// TODO: See about generalizing these to be able to take different return types
	/**
	 * Create a pattern which matches if any of its two components match.
	 * 
	 * @param <RetType>    The type each pattern returns
	 * @param <InpType>    The input for each pattern.
	 * @param <LeftState>  The first state type.
	 * @param <RightState> The second state type.
	 * 
	 * @param left         The first pattern.
	 * @param right        The second pattern.
	 * 
	 * @return A pattern which matches if either of its components do.
	 */
	static <RetType, InpType, LeftState, RightState> ComplexPattern<RetType, Either<LeftState, RightState>, InpType> or(
			ComplexPattern<RetType, LeftState, InpType> left, ComplexPattern<RetType, RightState, InpType> right) {
		// It would be convenient if we could just omit the two state types.
		// However, java isn't smart enough to infer the right types without the help
		Function<InpType, Pair<Boolean, Either<LeftState, RightState>>> matcher = (inp) -> {
			Pair<Boolean, LeftState> leftRes = left.matches(inp);
			if (leftRes.getLeft()) {
				return Pair.pair(true, Either.left(leftRes.getRight()));
			}

			Pair<Boolean, RightState> rightRes = right.matches(inp);
			if (rightRes.getLeft()) {
				return Pair.pair(true, Either.right(rightRes.getRight()));
			}
			return Pair.pair(false, null);
		};
		return from(matcher, (input, state) -> state.isLeft() ? left.apply(input, state.forceLeft())
				: right.apply(input, state.forceRight()));
	}

	/**
	 * Create a pattern which matches if both component patterns do.
	 * 
	 * @param <RetType>    The type returned by the patterns
	 * @param <InpType>    The input for the patterns
	 * @param <LeftState>  The state for the right pattern
	 * @param <RightState> The state for the left pattern
	 * 
	 * @param left         The left pattern
	 * @param right        The right pattern
	 * 
	 * @return A pattern which matches if both of the given patterns do.
	 */
	static <RetType, InpType, LeftState,
			RightState> ComplexPattern<Pair<RetType, RetType>, Pair<LeftState, RightState>, InpType> and(
					ComplexPattern<RetType, LeftState, InpType> left,
					ComplexPattern<RetType, RightState, InpType> right) {
		Function<InpType, Pair<Boolean, Pair<LeftState, RightState>>> matcher = (inp) -> {
			Pair<Boolean, LeftState> leftRes = left.matches(inp);
			if (!leftRes.getLeft())
				return Pair.pair(false, null);
			Pair<Boolean, RightState> rightRes = right.matches(inp);
			if (!rightRes.getLeft())
				return Pair.pair(false, null);
			return Pair.pair(true, Pair.pair(leftRes.getRight(), rightRes.getRight()));
		};

		return from(matcher, (input, state) -> {
			return Pair.pair(left.apply(input, state.getLeft()), right.apply(input, state.getRight()));
		});
	}

	static <RetType, InpType, Shared, State1,
			State2> ComplexPattern<Either<Shared, RetType>, Either<Shared, Pair<Shared, State2>>, InpType> then(
					ComplexPattern<Shared, State1, InpType> first, ComplexPattern<RetType, State2, Shared> second) {
		Function<InpType, Pair<Boolean, Either<Shared, Pair<Shared, State2>>>> matcher = (inp) -> {
			Pair<Boolean, State1> firstRes = first.matches(inp);
			if (!firstRes.getLeft())
				return Pair.pair(false, null);
			Shared shared = first.apply(inp, firstRes.getRight());
			Pair<Boolean, State2> secondRes = second.matches(shared);
			if (!secondRes.getLeft())
				return Pair.pair(true, Either.left(shared));
			return Pair.pair(true, Either.right(Pair.pair(shared, secondRes.getRight())));
		};

		return from(matcher, (input, state) -> {
			if (state.isLeft())
				return state.newRight();
			Pair<Shared, State2> right = state.forceRight();
			return Either.right(second.apply(right.getLeft(), right.getRight()));
		});
	}

	static <RetType, InpType, Shared, Other, State1,
			State2> ComplexPattern<Either<Either<Shared, RetType>, Other>,
					Either<Either<Shared, Pair<Shared, State2>>, Other>, InpType> maybeThen(
							ComplexPattern<Either<Shared, Other>, State1, InpType> first,
							ComplexPattern<RetType, State2, Shared> second) {
		Function<InpType, Pair<Boolean, Either<Either<Shared, Pair<Shared, State2>>, Other>>> matcher = (inp) -> {
			Pair<Boolean, State1> firstRes = first.matches(inp);
			if (!firstRes.getLeft())
				return Pair.pair(false, null);
			Either<Shared, Other> maybeShared = first.apply(inp, firstRes.getRight());
			if (!maybeShared.isLeft())
				return Pair.pair(true, maybeShared.newLeft());
			Shared shared = maybeShared.forceLeft();
			Pair<Boolean, State2> secondRes = second.matches(shared);
			if (!secondRes.getLeft())
				return Pair.pair(true, Either.left(Either.left(shared)));
			return Pair.pair(true, Either.left(Either.right(Pair.pair(shared, secondRes.getRight()))));
		};

		// Can't inline matcher, that breaks type-inference
		return from(matcher, (input, state) -> {
			if (!state.isLeft()) {
				return state.newLeft();
			}
			Either<Shared, Pair<Shared, State2>> left = state.forceLeft();
			if (left.isLeft()) {
				return Either.left(left.newRight());
			}
			Pair<Shared, State2> right = left.forceRight();
			return Either.left(Either.right(second.apply(right.getLeft(), right.getRight())));
		});
	}

	static <RetType, Shared1, Shared2, State, Input> ComplexPattern<RetType, State, Input> collapse(
			ComplexPattern<Either<Shared1, Shared2>, State, Input> patt, Function<Shared1, RetType> f,
			Function<Shared2, RetType> g) {
		return patt.mapOutput(eth -> eth.extract(f, g));
	}

	static <Shared, State, Input> ComplexPattern<Shared, State, Input> collapse(
			ComplexPattern<Either<Shared, Shared>, State, Input> patt) {
		return patt.mapOutput(Either::collapse);
	}

	static <RetType, State,
			Input> ComplexPattern<Optional<RetType>, ?, Input> maybe(ComplexPattern<RetType, State, Input> pat) {
		return from((inp) -> {
			Pair<Boolean, State> res = pat.matches(inp);
			if (res.getLeft())
				return Pair.pair(true, Either.left(res.getRight()));
			return Pair.pair(true, Either.right(null));
		}, (Input inp, Either<State, State> state) -> {
			// Need to specify the type; inference isn't smart enough to guess the right
			// thing
			if (state.isLeft())
				return Optional.of(pat.apply(inp, state.forceLeft()));
			return Optional.empty();
		});
	}

	default <NewInput> ComplexPattern<ReturnType, Pair<PredType, InputType>, NewInput> mapInput(
			Function<NewInput, InputType> func) {
		return from((inp) -> {
			InputType procInput = func.apply(inp);
			Pair<Boolean, PredType> res = matches(procInput);
			return Pair.pair(res.getLeft(), Pair.pair(res.getRight(), procInput));
		}, (input, state) -> apply(state.getRight(), state.getLeft()));
	}

	default <NewOutput> ComplexPattern<NewOutput, PredType, InputType> mapOutput(Function<ReturnType, NewOutput> func) {
		return from(ComplexPattern.this::matches, (inp, state) -> func.apply(apply(inp, state)));
	}

	default ComplexPattern<Pair<ReturnType, PredType>, PredType, InputType> withState() {
		return from(ComplexPattern.this::matches, (inp, state) -> Pair.pair(apply(inp, state), state));
	}
}