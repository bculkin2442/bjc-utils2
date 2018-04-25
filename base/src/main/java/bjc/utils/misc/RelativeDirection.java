package bjc.utils.misc;

import java.util.Random;
import java.util.function.Consumer;

import bjc.utils.exceptions.InvalidDirectionException;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Represents a direction that is relative to another direction
 * 
 * @author ben
 *
 */
public enum RelativeDirection {
	/**
	 * Same as direction
	 */
	BACKWARD,
	/**
	 * Opposing direction
	 */
	FORWARD,
	/**
	 * Counterclockwise from direction
	 */
	LEFT,
	/**
	 * Clockwise from direction
	 */
	RIGHT;

	private static Random RNG = new Random();

	/**
	 * Perform a specified action for a random number of relative
	 * directions.
	 * 
	 * @param numDirections
	 *            The number of cardinal directions to act on. Must be
	 *            greater than 0 and less then 5
	 * @param action
	 *            The action to perform for each of the relative directions
	 * @param ignoreBackwards
	 *            Whether or not to not have a chance of one of the
	 *            directions being backwards
	 */
	public static void forRandomDirections(int numDirections,
			Consumer<RelativeDirection> action, boolean ignoreBackwards) {
		int maxNDirections;

		if (ignoreBackwards) {
			maxNDirections = 3;
		} else {
			maxNDirections = 4;
		}

		if (numDirections <= 0 || numDirections > maxNDirections) {
			throw new IllegalArgumentException(
					"Tried to do things with incorrect number of relative directions. Tried with "
							+ numDirections);
		}

		IList<RelativeDirection> relativeDirs =
				new FunctionalList<>(values());

		if (ignoreBackwards) {
			relativeDirs.removeMatching(BACKWARD);
		}

		for (int i = 0; i <= maxNDirections - numDirections; i++) {
			RelativeDirection relativeDir =
					relativeDirs.randItem(RNG::nextInt);

			relativeDirs.removeMatching(relativeDir);
		}

		relativeDirs.forEach(action);
	}

	/**
	 * Properly convert a string to a relative direction
	 * 
	 * @param value
	 *            The string to convert
	 * @return The relative direction represented by the string
	 */
	public static RelativeDirection properValueOf(String value) {
		return valueOf(value.toUpperCase());
	}

	/**
	 * Change another direction by turning the way this direction specifies
	 * 
	 * @param dir
	 *            The direction to change
	 * @return The direction after turning this way
	 */
	public Direction makeAbsolute(Direction dir) {
		// Only cardinal directions can be truly absolutized
		if (dir.isCardinal()) {
			switch (this) {
				case BACKWARD:
					return dir;
				case FORWARD:
					return dir.opposing();
				case LEFT:
					return dir.rotateCounterClockwise();
				case RIGHT:
					return dir.rotateClockwise();
				default:
					throw new InvalidDirectionException(
							"Attempted to make absolute a direction in a unknown way "
									+ this);
			}
		}

		// Since it isn't a cardinal direction, absolutize it against a
		// random direction
		return this.makeAbsolute(Direction.NORTH);
	}
}
