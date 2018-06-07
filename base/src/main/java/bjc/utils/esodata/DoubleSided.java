package bjc.utils.esodata;

public interface DoubleSided {
	/**
	 * Flips the object.
	 *
	 * The active side becomes inactive, and the inactive side becomes
	 * active.
	 */
	void flip();

	/**
	 * Check which side of the object is active;
	 *
	 * @return True if the front side is active, false otherwise.
	 */
	boolean currentSide();
}
