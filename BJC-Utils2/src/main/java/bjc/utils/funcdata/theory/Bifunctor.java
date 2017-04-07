package bjc.utils.funcdata.theory;

import java.util.function.Function;

/**
 * A functor over a pair of heterogeneous types
 *
 * @author ben
 * @param <LeftType>
 *                The type stored on the 'left' of the pair
 * @param <RightType>
 *                The type stored on the 'right' of the pair
 *
 */
public interface Bifunctor<LeftType, RightType> {
	/**
	 * Alias for functor mapping.
	 * 
	 * @author EVE
	 * 
	 * @param <OldLeft>
	 * @param <OldRight>
	 * @param <NewLeft>
	 * @param <NewRight>
	 */
	public interface BifunctorMap<OldLeft, OldRight, NewLeft, NewRight>
			extends Function<Bifunctor<OldLeft, OldRight>, Bifunctor<NewLeft, NewRight>> {

	}

	/**
	 * Alias for left functor mapping.
	 * 
	 * @author EVE
	 *
	 * @param <OldLeft>
	 * @param <OldRight>
	 * @param <NewLeft>
	 */
	public interface LeftBifunctorMap<OldLeft, OldRight, NewLeft>
			extends BifunctorMap<OldLeft, OldRight, NewLeft, OldRight> {

	}

	/**
	 * Alias for right functor mapping.
	 * 
	 * @author EVE
	 *
	 * @param <OldLeft>
	 * @param <OldRight>
	 * @param <NewRight>
	 */
	public interface RightBifunctorMap<OldLeft, OldRight, NewRight>
			extends BifunctorMap<OldLeft, OldRight, OldLeft, NewRight> {

	}

	/**
	 * Lift a pair of functions to a single function that maps over both
	 * parts of a pair
	 *
	 * @param <OldLeft>
	 *                The old left type of the pair
	 * @param <OldRight>
	 *                The old right type of the pair
	 * @param <NewLeft>
	 *                The new left type of the pair
	 * @param <NewRight>
	 *                The new right type of the pair
	 * @param leftFunc
	 *                The function that maps over the left of the pair
	 * @param rightFunc
	 *                The function that maps over the right of the pair
	 * @return A function that maps over both parts of the pair
	 */
	public default <OldLeft, OldRight, NewLeft, NewRight> BifunctorMap<OldLeft, OldRight, NewLeft, NewRight> bimap(
			Function<OldLeft, NewLeft> leftFunc, Function<OldRight, NewRight> rightFunc) {
		BifunctorMap<OldLeft, OldRight, NewLeft, NewRight> bimappedFunc = (argPair) -> {
			LeftBifunctorMap<OldLeft, OldRight, NewLeft> leftMapper = argPair.fmapLeft(leftFunc);

			Bifunctor<NewLeft, OldRight> leftMappedFunctor = leftMapper.apply(argPair);
			RightBifunctorMap<NewLeft, OldRight, NewRight> rightMapper = leftMappedFunctor
					.fmapRight(rightFunc);

			return rightMapper.apply(leftMappedFunctor);
		};

		return bimappedFunc;
	}

	/**
	 * Lift a function to operate over the left part of this pair
	 *
	 * @param <OldLeft>
	 *                The old left type of the pair
	 * @param <OldRight>
	 *                The old right type of the pair
	 * @param <NewLeft>
	 *                The new left type of the pair
	 * @param func
	 *                The function to lift to work over the left side of the
	 *                pair
	 * @return The function lifted to work over the left side of bifunctors
	 */
	public <OldLeft, OldRight, NewLeft> LeftBifunctorMap<OldLeft, OldRight, NewLeft> fmapLeft(
			Function<OldLeft, NewLeft> func);

	/**
	 * Lift a function to operate over the right part of this pair
	 *
	 * @param <OldLeft>
	 *                The old left type of the pair
	 * @param <OldRight>
	 *                The old right type of the pair
	 * @param <NewRight>
	 *                The new right type of the pair
	 * @param func
	 *                The function to lift to work over the right side of
	 *                the pair
	 * @return The function lifted to work over the right side of bifunctors
	 */
	public <OldLeft, OldRight, NewRight> RightBifunctorMap<OldLeft, OldRight, NewRight> fmapRight(
			Function<OldRight, NewRight> func);

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
