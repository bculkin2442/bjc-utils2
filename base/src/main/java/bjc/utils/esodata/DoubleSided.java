package bjc.utils.esodata;

/**
 * Interface for a double-sided object.
 * 
 * @author bjculkin
 *
 */
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
