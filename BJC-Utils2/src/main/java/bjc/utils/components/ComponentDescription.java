package bjc.utils.components;

/**
 * Generic implementation of a description for a component
 * 
 * @author ben
 *
 */
public class ComponentDescription implements IDescribedComponent {
	private static void sanityCheckArgs(String name, String author,
			String description, int version) {
		if (name == null) {
			throw new NullPointerException("Component name can't be null");
		} else if (author == null) {
			throw new NullPointerException(
					"Component author can't be null");
		} else if (description == null) {
			throw new NullPointerException(
					"Component description can't be null");
		} else if (version <= 0) {
			throw new IllegalArgumentException(
					"Component version must be greater than 0");
		}
	}

	/**
	 * The author of the component
	 */
	private String	author;
	/**
	 * The description of the component
	 */
	private String	description;
	/**
	 * The name of the component
	 */
	private String	name;

	/**
	 * The version of the component
	 */
	private int		version;

	/**
	 * Create a new component description
	 * 
	 * @param name
	 *            The name of the component
	 * @param author
	 *            The author of the component
	 * @param description
	 *            The description of the component
	 * @param version
	 *            The version of the component
	 * @throws IllegalArgumentException
	 *             thrown if version is less than 1
	 */
	public ComponentDescription(String name, String author,
			String description, int version) {
		sanityCheckArgs(name, author, description, version);

		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return name + " component v" + version + ", written by " + author;
	}
}
