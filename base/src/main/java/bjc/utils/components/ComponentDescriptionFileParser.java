package bjc.utils.components;

import java.io.InputStream;

import bjc.utils.ioutils.RuleBasedConfigReader;

import static bjc.utils.ioutils.RuleBasedReaderPragmas.buildInteger;
import static bjc.utils.ioutils.RuleBasedReaderPragmas.buildStringCollapser;

/**
 * Read a component description from a file.
 *
 * @author ben
 */
public class ComponentDescriptionFileParser {
	/* The reader used to read in component descriptions */
	private static RuleBasedConfigReader<ComponentDescriptionState> reader;

	/* Initialize the reader and its pragmas. */
	static {
		/*
		 * This reader works entirely off of pragmas, so no need to
		 * handle rules.
		 */
		reader = new RuleBasedConfigReader<>((tokenizer, statePair) -> {
			/* Don't need to do anything on rule start. */
		}, (tokenizer, state) -> {
			/* Don't need to do anything on rule continuation. */
		}, (state) -> {
			/* Don't need to do anything on rule end. */
		});

		/* Setup reader pragmas. */
		setupReaderPragmas();
	}

	/**
	 * Parse a component description from a stream.
	 *
	 * @param inputSource
	 *        The stream to parse from.
	 *
	 * @return The description parsed from the stream.
	 */
	public static ComponentDescription fromStream(final InputStream inputSource) {
		if(inputSource == null) {
			throw new NullPointerException("Input source must not be null");
		}

		ComponentDescriptionState state = new ComponentDescriptionState();
		/*
		 * This is valid, because the thing that is returned is the same
		 * reference we passed in.
		 */
		reader.fromStream(inputSource, state);

		return state.toDescription();
	}

	/* Create all the pragmas the reader needs to function. */
	private static void setupReaderPragmas() {
		reader.addPragma("name", buildStringCollapser("name", (name, state) -> state.setName(name)));

		reader.addPragma("author", buildStringCollapser("author", (author, state) -> state.setAuthor(author)));

		reader.addPragma("description", buildStringCollapser("description",
				(description, state) -> state.setDescription(description)));

		reader.addPragma("version", buildInteger("version", (version, state) -> state.setVersion(version)));
	}
}
