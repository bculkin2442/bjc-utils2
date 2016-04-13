package bjc.utils.data.experimental;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a pair of values
 * 
 * @author ben
 * @param <LeftType>
 *            The type of the left side of the pair
 * @param <RightType>
 *            The type of the right side of the pair
 *
 */
public interface IPair<LeftType, RightType> {
	/**
	 * Bind a function to the left value in this pair
	 * 
	 * @param <BoundLeft>
	 *            The type of the bound value
	 * @param leftBinder
	 *            The function to use to bind
	 * @return A pair with the left type bound
	 */
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder);

	/**
	 * Bind a function to the right value in this pair
	 * 
	 * @param <BoundRight>
	 *            The type of the bound value
	 * @param rightBinder
	 *            The function to use to bind
	 * @return A pair with the right type bound
	 */
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder);

	/**
	 * Bind a function across the values in this pair
	 * 
	 * @param <BoundLeft>
	 *            The type of the bound left
	 * @param <BoundRight>
	 *            The type of the bound right
	 * @param binder
	 *            The function to bind with
	 * @return The bound pair
	 */
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder);

	/**
	 * Merge the two values in this pair into a single value
	 * 
	 * @param <MergedType>
	 *            The type of the single value
	 * @param merger
	 *            The function to use for merging
	 * @return The pair, merged into a single value
	 */
	public <MergedType> MergedType merge(
			BiFunction<LeftType, RightType, MergedType> merger);

	/**
	 * Get the value on the left side of the pair
	 * 
	 * @return The value on the left side of the pair
	 */
	public default LeftType getLeft() {
		return merge((leftValue, rightValue) -> leftValue);
	}

	/**
	 * Get the value on the right side of the pair
	 * 
	 * @return The value on the right side of the pair
	 */
	public default RightType getRight() {
		return merge((leftValue, rightValue) -> rightValue);
	}

	/**
	 * Immediately perfom the specified action with the contents of this pair
	 * @param consumer The action to perform on the pair
	 */
	public default void doWith(BiConsumer<LeftType, RightType> consumer) {
		merge((leftValue, rightValue) -> {
			consumer.accept(leftValue, rightValue);
			
			return null;
		});
	}
}
