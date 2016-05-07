package bjc.utils.funcdata.theory;

import java.util.function.Function;

/**
 * A functor over a pair of heterogenous types
 * 
 * @author ben
 * @param <LeftType>
 *            The type stored on the 'left' of the pair
 * @param <RightType>
 *            The type stored on the 'right' of the pair
 *
 */
public interface Bifunctor<LeftType, RightType> {
	/**
	 * Lift a function to operate over the left part of this pair
	 * 
	 * @param <OldLeft>
	 *            The old left type of the pair
	 * @param <OldRight>
	 *            The old right type of the pair
	 * @param <NewLeft>
	 *            The new left type of the pair
	 * @param func
	 *            The function to lift to work over the left side of the
	 *            pair
	 * @return The function lifted to work over the left side of bifunctors
	 */
	public <OldLeft, OldRight, NewLeft>
			Function<Bifunctor<OldLeft, OldRight>, Bifunctor<NewLeft, OldRight>>
			fmapLeft(Function<OldLeft, NewLeft> func);

	/**
	 * Lift a function to operate over the right part of this pair
	 * 
	 * @param <OldLeft>
	 *            The old left type of the pair
	 * @param <OldRight>
	 *            The old right type of the pair
	 * @param <NewRight>
	 *            The new right type of the pair
	 * @param func
	 *            The function to lift to work over the right side of the
	 *            pair
	 * @return The function lifted to work over the right side of
	 *         bifunctors
	 */
	public <OldLeft, OldRight, NewRight>
			Function<Bifunctor<OldLeft, OldRight>, Bifunctor<OldLeft, NewRight>>
			fmapRight(Function<OldRight, NewRight> func);

	/**
	 * Get the value contained on the left of this bifunctor
	 * 
	 * @return The value on the left side of this bifunctor
	 */
	public LeftType getLeft();

	/**
	 * Get the value contained on the right of this bifunctor
	 * 
	 * @return The value on the right of this bifunctor
	 */
	public RightType getRight();
}
