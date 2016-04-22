package bjc.utils.components;

/**
 * Internal state of component description parser
 * 
 * @author ben
 *
 */
public class ComponentDescriptionState {
	private String	name;

	private String	description;

	private String	author;

	private int		version;

	/**
	 * Set the author of this component
	 * 
	 * @param author
	 *            The author of this component
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Set the description of this component
	 * 
	 * @param description
	 *            The description of this component
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the name of this component
	 * 
	 * @param name
	 *            The name of this component
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the version of this component
	 * 
	 * @param version
	 *            The version of this component
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Convert this state into the description it represents
	 * 
	 * @return The description represented by this state
	 */
	public ComponentDescription toDescription() {
		return new ComponentDescription(name, author, description,
				version);
	}
}
