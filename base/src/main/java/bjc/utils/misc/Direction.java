package bjc.utils.misc;

import java.util.Random;
import java.util.function.Consumer;

import bjc.utils.exceptions.InvalidDirectionException;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

import org.apache.commons.lang3.text.WordUtils;

/**
 * A set of cardinal directions
 * 
 * The particular axis assigned to the coordinates are based off of x being
 * the bottom left axis on a conventional 3D plot
 * 
 * @author ben
 *
 */
public enum Direction {
	/**
	 * Negative z-axis
	 */
	DOWN,
	/**
	 * Positive y-axis
	 */
	EAST,
	/**
	 * Positive x-axis
	 */
	NORTH,
	/**
	 * Negative x-axis
	 */
	SOUTH,
	/**
	 * Positive z-axis
	 */
	UP,
	/**
	 * Negative y-axis
	 */
	WEST;

	/**
	 * The source of randomness for picking random directions
	 */
	private static final Random RNG = new Random();

	/**
	 * Get a list of all the cardinal directions
	 * 
	 * @return A list of all the cardinal directions
	 */
	public static IList<Direction> cardinals() {
		return new FunctionalList<>(NORTH, SOUTH, EAST, WEST);
	}

	/**
	 * Perform the specified action once with each of the cardinal
	 * directions
	 * 
	 * @param act
	 *            The action to perform for each cardinal direction
	 */
	public static void forCardinalDirections(Consumer<Direction> act) {
		cardinals().forEach(act);
	}

	/**
	 * Perform a specified action for a random number of cardinals.
	 * 
	 * @param nCardinals
	 *            The number of cardinal directions to act on. Must be
	 *            greater than 0 and less then 5
	 * @param act
	 *            The action to perform for each of the cardinals
	 */
	public static void forRandomCardinals(int nCardinals,
			Consumer<Direction> act) {
		if (nCardinals <= 0 || nCardinals > 4) {
			throw new IllegalArgumentException(
					"Tried to do things with incorrect number of cardinal directions. Tried with "
							+ nCardinals);
		}

		IList<Direction> cards = cardinals();

		for (int i = 0; i <= 4 - nCardinals; i++) {
			Direction rDir = cards.randItem(RNG::nextInt);

			cards.removeMatching(rDir);
		}

		cards.forEach(act);
	}

	/**
	 * Create a value of the enumeration from a string
	 * 
	 * @param value
	 *            The string to turn into a value
	 * @return The direction corresponding to the given value
	 */
	public static Direction properValueOf(String value) {
		return valueOf(value.toUpperCase());
	}

	/**
	 * Test if this direction is a cardinal direction
	 * 
	 * @return If the direction is cardinal or not
	 */
	public boolean isCardinal() {
		switch (this) {
			case EAST:
			case NORTH:
			case SOUTH:
			case WEST:
				return true;
			case DOWN:
			case UP:
				return false;
			default:
				throw new InvalidDirectionException(
						"WAT. Somehow ended up with an invalid direction "
								+ this);
		}
	}

	/**
	 * Get the direction that opposes the current one
	 * 
	 * @return The direction that is in the opposite direction of the
	 *         current one
	 */
	public Direction opposing() {
		switch (this) {
			case NORTH:
				return SOUTH;
			case EAST:
				return WEST;
			case SOUTH:
				return NORTH;
			case WEST:
				return WEST;
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			default:
				throw new IllegalStateException(
						"Enumeration got into a invalid state. WAT");
		}
	}

	/**
	 * Get the direction that is clockwise on the compass from this one.
	 * 
	 * Only works on cardinals.
	 * 
	 * @return The cardinal clockwise from this one
	 */
	public Direction rotateClockwise() {
		switch (this) {
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			case UP:
			case DOWN:
			default:
				throw new InvalidDirectionException(
						"Can't rotate non-cardinal direction clockwise: "
								+ this);

		}
	}

	/**
	 * Get the direction that is counter-clockwise on the compass from this
	 * one.
	 * 
	 * Only works on cardinals.
	 * 
	 * @return The cardinal counter-clockwise from this one
	 */
	public Direction rotateCounterClockwise() {
		switch (this) {
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			case UP:
			case DOWN:
			default:
				throw new InvalidDirectionException(
						"Can't rotate non-cardinal direction counterclockwise: "
								+ this);

		}
	}

	@Override
	public String toString() {
		/*
		 * Make sure the word is properly capitalized for english
		 */
		return WordUtils.capitalize(super.toString().toLowerCase());
	}
}
