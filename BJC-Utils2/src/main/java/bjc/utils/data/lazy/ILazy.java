package bjc.utils.data.lazy;

/**
 * Interface for some maintenance operations on lazy objects
 * 
 * @author ben
 *
 */
public interface ILazy {
	/**
	 * Check if this object has been materialized
	 * 
	 * @return Whether or not this object has been materialized
	 */
	public boolean isMaterialized();

	/**
	 * Check if there are pending actions that need to be applied
	 * 
	 * @return Whether or not there are pending actions
	 */
	public boolean hasPendingActions();

	/**
	 * Make this object materialize itelf
	 */
	public void materialize();

	/**
	 * Make this object apply any pending objects
	 * 
	 * As a requirement, will materialize the object if it is not materialized
	 */
	public void applyPendingActions();
}
