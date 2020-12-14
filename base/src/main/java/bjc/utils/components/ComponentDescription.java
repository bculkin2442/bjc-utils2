package bjc.utils.components;

/**
 * Generic implementation of a description for a component.
 *
 * @author ben
 */
public class ComponentDescription implements DescribedComponent {
	/* Check arguments are good. */
	@SuppressWarnings("unused")
	private static void sanityCheckArgs(final String name, final String author,
			final String description, final int version) {
		if (name == null) {
			throw new NullPointerException("Component name can't be null");
		} else if (version <= 0) {
			throw new IllegalArgumentException(
					"Component version must be greater than 0");
		}
	}

	/** The author of the component */
	private final String author;
	/** The description of the component */
	private final String description;
	/** The name of the component */
	private final String name;
	/** The version of the component */
	private final int version;

	/**
	 * Create a new component description.
	 *
	 * @param name
	 *                    The name of the component.
	 *
	 * @param author
	 *                    The author of the component.
	 *
	 * @param description
	 *                    The description of the component.
	 *
	 * @param version
	 *                    The version of the component.
	 *
	 * @throws IllegalArgumentException
	 *                                  Thrown if version is less than 1.
	 */
	public ComponentDescription(final String name, final String author,
			final String description, final int version) {
		sanityCheckArgs(name, author, description, version);

		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
	}

	@Override
	public String getAuthor() {
		if (author == null) {
			return DescribedComponent.super.getAuthor();
		}

		return author;
	}

	@Override
	public String getDescription() {
		if (description == null) {
			return DescribedComponent.super.getDescription();
		}

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
		return String.format("%s component v%d, written by %s", name, version, author);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (author == null ? 0 : author.hashCode());
		result = prime * result + (description == null ? 0 : description.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + version;

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final ComponentDescription other = (ComponentDescription) obj;

		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;

		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (version != other.version)
			return false;

		return true;
	}
}
