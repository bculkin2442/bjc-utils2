package bjc.utils.components;

/**
 * Represents a optional component that has status information associated with
 * it.
 *
 * @author ben
 *
 */
public interface IDescribedComponent extends Comparable<IDescribedComponent> {
	/**
	 * Get the author of this component.
	 *
	 * Providing this is optional, with "Anonymous" as the default author.
	 *
	 * @return The author of the component.
	 */
	default String getAuthor() {
		return "Anonymous";
	}

	/**
	 * Get the description of this component.
	 *
	 * Providing this is optional, with the default being a note that no
	 * description was provided.
	 *
	 * @return The description of the component
	 */
	default String getDescription() {
		return "No description provided.";
	}

	/**
	 * Get the name of this component.
	 *
	 * This is the only thing required of all components.
	 *
	 * @return The name of the component.
	 */
	String getName();

	/**
	 * Get the version of this component.
	 *
	 * Providing this is optional, with "1" as the default version.
	 *
	 * @return The version of this component.
	 */
	default int getVersion() {
		return 1;
	}

	@Override
	default int compareTo(final IDescribedComponent o) {
		int res = getName().compareTo(o.getName());

		if(res == 0) {
			res = getVersion() - o.getVersion();
		}

		return res;
	}
}
