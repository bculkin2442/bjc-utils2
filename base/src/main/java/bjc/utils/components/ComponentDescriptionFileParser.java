package bjc.utils.components;

import static bjc.utils.ioutils.RuleBasedReaderPragmas.buildInteger;
import static bjc.utils.ioutils.RuleBasedReaderPragmas.buildStringCollapser;

import java.io.InputStream;

import bjc.utils.ioutils.RuleBasedConfigReader;

/**
 * Read a component description from a file.
 *
 * The file format is based entirely off of pragma statements, and should have
 * at least one of each of the following statements
 * <ul>
 * <li>pragma name &lt;component-name&rt;</li>
 * <li>pragma author &lt;component-version&rt;</li>
 * <li>pragma description &lt;component-description&rt;</li>
 * <li>pragma version &lt;component-version&rt;</li>
 * </ul>
 * 
 * @author ben
 */
public class ComponentDescriptionFileParser {
	/* The reader used to read in component descriptions */
	private static RuleBasedConfigReader<ComponentDescriptionState> reader;

	/* Initialize the reader and its pragmas. */
	static {
		/*
		 * This reader works entirely off of pragmas, so no need to handle rules.
		 */
		reader = new RuleBasedConfigReader<>((tokenizer, statePair) -> {
			/* Don't need to do anything on rule start. */
		}, (tokenizer, state) -> {
			/* Don't need to do anything on rule continuation. */
		}, state -> {
			/* Don't need to do anything on rule end. */
		});

		/* Setup reader pragmas. */
		setupReaderPragmas();
	}

	/**
	 * Parse a component description from a stream.
	 *
	 * @param inputSource
	 *                    The stream to parse from.
	 *
	 * @return The description parsed from the stream.
	 */
	public static ComponentDescription fromStream(final InputStream inputSource) {
		if (inputSource == null) {
			throw new NullPointerException("Input source must not be null");
		}

		ComponentDescriptionState state = new ComponentDescriptionState();
		/*
		 * This is valid, because the thing that is returned is the same reference we
		 * passed in.
		 */
		reader.fromStream(inputSource, state);

		return state.toDescription();
	}

	/* Create all the pragmas the reader needs to function. */
	private static void setupReaderPragmas() {
		reader.addPragma("name",
				buildStringCollapser("name", (name, state) -> state.setName(name)));

		reader.addPragma("author", buildStringCollapser("author",
				(author, state) -> state.setAuthor(author)));

		reader.addPragma("description", buildStringCollapser("description",
				(description, state) -> state.setDescription(description)));

		reader.addPragma("version",
				buildInteger("version", (version, state) -> state.setVersion(version)));
	}

	private static final class ComponentDescriptionState {
		/* Tentative name of this component. */
		private String name;

		/* Tentative description of this component. */
		private String description;

		/* Tentative author of this component. */
		private String author;

		/* Tentative version of this component. */
		private int version;

		/**
		 * Set the author of this component.
		 *
		 * @param author
		 *               The author of this component.
		 */
		public void setAuthor(final String author) {
			this.author = author;
		}

		/**
		 * Set the description of this component.
		 *
		 * @param description
		 *                    The description of this component.
		 */
		public void setDescription(final String description) {
			this.description = description;
		}

		/**
		 * Set the name of this component.
		 *
		 * @param name
		 *             The name of this component.
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * Set the version of this component.
		 *
		 * @param version
		 *                The version of this component.
		 */
		public void setVersion(final int version) {
			this.version = version;
		}

		/**
		 * Convert this state into the description it represents.
		 *
		 * @return The description represented by this state.
		 */
		public ComponentDescription toDescription() {
			return new ComponentDescription(name, author, description, version);
		}

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

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;

			final ComponentDescriptionState other = (ComponentDescriptionState) obj;

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

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ComponentDescriptionState [");

			if (name != null) {
				builder.append("name=");
				builder.append(name);
				builder.append(", ");
			}

			if (description != null) {
				builder.append("description=");
				builder.append(description);
				builder.append(", ");
			}

			if (author != null) {
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
}
