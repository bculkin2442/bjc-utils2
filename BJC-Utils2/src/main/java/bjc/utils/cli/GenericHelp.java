package bjc.utils.cli;

/**
 * Generic implementation of a help topic
 *
 * @author ben
 *
 */
public class GenericHelp implements CommandHelp {
	// The strings for this help topic
	private final String	summary;
	private final String	description;

	/**
	 * Create a new help topic
	 *
	 * @param summary
	 *                The summary of this help topic
	 * @param description
	 *                The description of this help topic, or null if this
	 *                help topic doesn't have a more detailed description
	 */
	public GenericHelp(final String summary, final String description) {
		if (summary == null) throw new NullPointerException("Help summary must be non-null");

		this.summary = summary;
		this.description = description;
	}

	@Override
	public String getDescription() {
		if (description == null) return summary;

		return description;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("GenericHelp [");

		if (summary != null) {
			builder.append("summary=");
			builder.append(summary);
		}

		if (description != null) {
			builder.append(", ");
			builder.append("description=");
			builder.append(description);
		}

		builder.append("]");

		return builder.toString();
	}
}
