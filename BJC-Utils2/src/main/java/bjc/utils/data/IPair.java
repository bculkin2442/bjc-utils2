package bjc.utils.data;

import bjc.utils.funcdata.theory.Bifunctor;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a pair of values
 *
 * @author ben
 * @param <LeftType>
 *                The type of the left side of the pair
 * @param <RightType>
 *                The type of the right side of the pair
 *
 */
public interface IPair<LeftType, RightType> extends Bifunctor<LeftType, RightType> {
	/**
	 * Bind a function across the values in this pair
	 *
	 * @param <BoundLeft>
	 *                The type of the bound left
	 * @param <BoundRight>
	 *                The type of the bound right
	 * @param binder
	 *                The function to bind with
	 * @return The bound pair
	 */
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder);

	/**
	 * Bind a function to the left value in this pair
	 *
	 * @param <BoundLeft>
	 *                The type of the bound value
	 * @param leftBinder
	 *                The function to use to bind
	 * @return A pair with the left type bound
	 */
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder);

	/**
	 * Bind a function to the right value in this pair
	 *
	 * @param <BoundRight>
	 *                The type of the bound value
	 * @param rightBinder
	 *                The function to use to bind
	 * @return A pair with the right type bound
	 */
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder);

	/**
	 * Pairwise combine two pairs together
	 *
	 * @param <OtherLeft>
	 *                The left type of the other pair
	 * @param <OtherRight>
	 *                The right type of the other pair
	 * @param otherPair
	 *                The pair to combine with
	 * @return The pairs, pairwise combined together
	 */
	public default <OtherLeft, OtherRight> IPair<IPair<LeftType, OtherLeft>, IPair<RightType, OtherRight>> combine(
			IPair<OtherLeft, OtherRight> otherPair) {
		return combine(otherPair, Pair<LeftType, OtherLeft>::new, Pair<RightType, OtherRight>::new);
	}

	/**
	 * Combine the contents of two pairs together
	 *
	 * @param <OtherLeft>
	 *                The type of the left value of the other pair
	 * @param <OtherRight>
	 *                The type of the right value of the other pair
	 * @param <CombinedLeft>
	 *                The type of the left value of the combined pair
	 * @param <CombinedRight>
	 *                The type of the right value of the combined pair
	 * @param otherPair
	 *                The other pair to combine with
	 * @param leftCombiner
	 * @param rightCombiner
	 * @return A pair with its values combined
	 */
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			IPair<OtherLeft, OtherRight> otherPair,
			BiFunction<LeftType, OtherLeft, CombinedLeft> leftCombiner,
			BiFunction<RightType, OtherRight, CombinedRight> rightCombiner);

	/**
	 * Immediately perfom the specified action with the contents of this
	 * pair
	 *
	 * @param consumer
	 *                The action to perform on the pair
	 */
	public default void doWith(BiConsumer<LeftType, RightType> consumer) {
		merge((leftValue, rightValue) -> {
			consumer.accept(leftValue, rightValue);

			return null;
		});
	}

	@Override
	default <OldLeft, OldRight, NewLeft> LeftBifunctorMap<OldLeft, OldRight, NewLeft> fmapLeft(
			Function<OldLeft, NewLeft> func) {
		return argumentPair -> {
			if(!(argumentPair instanceof IPair<?, ?>)) {
				String msg = "This function can only be applied to instances of IPair";

				throw new IllegalArgumentException(msg);
			}

			IPair<OldLeft, OldRight> argPair = (IPair<OldLeft, OldRight>) argumentPair;

			return argPair.mapLeft(func);
		};
	}

	@Override
	default <OldLeft, OldRight, NewRight> RightBifunctorMap<OldLeft, OldRight, NewRight>

			fmapRight(Function<OldRight, NewRight> func) {
		return argumentPair -> {
			if(!(argumentPair instanceof IPair<?, ?>)) {
				String msg = "This function can only be applied to instances of IPair";

				throw new IllegalArgumentException(msg);
			}

			IPair<OldLeft, OldRight> argPair = (IPair<OldLeft, OldRight>) argumentPair;

			return argPair.mapRight(func);
		};
	}

	/**
	 * Get the value on the left side of the pair
	 *
	 * @return The value on the left side of the pair
	 */
	@Override
	public default LeftType getLeft() {
		return merge((leftValue, rightValue) -> leftValue);
	}

	/**
	 * Get the value on the right side of the pair
	 *
	 * @return The value on the right side of the pair
	 */
	@Override
	public default RightType getRight() {
		return merge((leftValue, rightValue) -> rightValue);
	}

	/**
	 * Transform the value on the left side of the pair. Doesn't modify the
	 * pair
	 *
	 * @param <NewLeft>
	 *                The new type of the left part of the pair
	 * @param mapper
	 *                The function to use to transform the left part of the
	 *                pair
	 * @return The pair, with its left part transformed
	 */
	public <NewLeft> IPair<NewLeft, RightType> mapLeft(Function<LeftType, NewLeft> mapper);

	/**
	 * Transform the value on the right side of the pair. Doesn't modify the
	 * pair
	 *
	 * @param <NewRight>
	 *                The new type of the right part of the pair
	 * @param mapper
	 *                The function to use to transform the right part of the
	 *                pair
	 * @return The pair, with its right part transformed
	 */
	public <NewRight> IPair<LeftType, NewRight> mapRight(Function<RightType, NewRight> mapper);

	/**
	 * Merge the two values in this pair into a single value
	 *
	 * @param <MergedType>
	 *                The type of the single value
	 * @param merger
	 *                The function to use for merging
	 * @return The pair, merged into a single value
	 */
	public <MergedType> MergedType merge(BiFunction<LeftType, RightType, MergedType> merger);
}
