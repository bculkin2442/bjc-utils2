package bjc.utils.components;

/**
 * Generic implementation of a description for a component
 * @author ben
 *
 */
public class ComponentDescription implements IDescribedComponent {
	/**
	 * The name of the component
	 */
	private String name;
	/**
	 * The author of the component
	 */
	private String author;
	/**
	 * The description of the component
	 */
	private String description;
	/**
	 * The version of the component
	 */
	private int version;
	
	/**
	 * Create a new component description
	 * @param name The name of the component
	 * @param author The author of the component
	 * @param description The description of the component
	 * @param version The version of the component
	 */
	public ComponentDescription(String name, String author,
			String description, int version) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
	}

	@Override
	public String getName() {
		return name;
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
	public int getVersion() {
		return version;
	}
}
