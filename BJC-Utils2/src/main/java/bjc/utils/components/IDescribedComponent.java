package bjc.utils.components;

/**
 * Represents a optional component that has status information associated
 * with it
 * 
 * @author ben
 *
 */
public interface IDescribedComponent {
	/**
	 * Get the name of this component.
	 * 
	 * This is the only thing required of all components
	 * 
	 * @return The name of the component
	 */
	public String getName();

	/**
	 * Get the description of this component
	 * 
	 * Providing this is optional, with the default being a note that no
	 * description was provided
	 * 
	 * @return The description of the component
	 */
	public default String getDescription() {
		return "No description provided.";
	}

	/**
	 * Get the author of this component
	 * 
	 * Providing this is optional, with "Anonymous" as the default author
	 * 
	 * @return The author of the component
	 */
	public default String getAuthor() {
		return "Anonymous";
	}

	/**
	 * Get the version of this component
	 * 
	 * Providing this is optional, with "1" as the default version
	 * 
	 * @return The version of this component
	 */
	public default int getVersion() {
		return 1;
	}
}
