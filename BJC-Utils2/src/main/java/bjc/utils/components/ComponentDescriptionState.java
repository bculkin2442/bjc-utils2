package bjc.utils.components;

/**
 * Internal state of component description parser
 *
 * @author ben
 *
 */
public class ComponentDescriptionState {
	// Tentative name of this component
	private String name;

	// Tentative description of this componet
	private String description;

	// Tentative author of this component
	private String author;

	// Tentative version of this component
	private int version;

	/**
	 * Set the author of this component
	 *
	 * @param author
	 *                The author of this component
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Set the description of this component
	 *
	 * @param description
	 *                The description of this component
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the name of this component
	 *
	 * @param name
	 *                The name of this component
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the version of this component
	 *
	 * @param version
	 *                The version of this component
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
		return new ComponentDescription(name, author, description, version);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + version;

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;

		ComponentDescriptionState other = (ComponentDescriptionState) obj;

		if(author == null) {
			if(other.author != null) return false;
		} else if(!author.equals(other.author)) return false;

		if(description == null) {
			if(other.description != null) return false;
		} else if(!description.equals(other.description)) return false;

		if(name == null) {
			if(other.name != null) return false;
		} else if(!name.equals(other.name)) return false;

		if(version != other.version) return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ComponentDescriptionState [");

		if(name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}

		if(description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}

		if(author != null) {
			builder.append("author=");
			builder.append(author);
			builder.append(", ");
		}

		builder.append("version=");
		builder.append(version);
		builder.append("]");

		return builder.toString();
	}

}
