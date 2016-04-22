package bjc.utils.cli;

/**
 * Generic implementation of a help topic
 * 
 * @author ben
 *
 */
public class GenericHelp implements ICommandHelp {
	private String	summary;
	private String	description;

	/**
	 * Create a new help topic
	 * 
	 * @param summary
	 *            The summary of this help topic
	 * @param description
	 *            The description of this help topic
	 */
	public GenericHelp(String summary, String description) {
		this.summary = summary;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getSummary() {
		return summary;
	}

}