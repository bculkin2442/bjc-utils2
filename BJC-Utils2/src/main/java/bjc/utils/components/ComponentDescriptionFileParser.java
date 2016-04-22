package bjc.utils.components;

import java.io.InputStream;
import java.util.function.BiConsumer;

import bjc.utils.exceptions.PragmaFormatException;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcutils.ListUtils;
import bjc.utils.parserutils.RuleBasedConfigReader;

/**
 * Read a component description from a file
 * 
 * @author ben
 *
 */
public class ComponentDescriptionFileParser {
	private static RuleBasedConfigReader<ComponentDescriptionState> reader;

	static {
		// This reader works entirely off of pragmas, so no need to handle
		// rules
		reader = new RuleBasedConfigReader<>((tokenizer, statePair) -> {
			// Don't need to do anything on rule start
		}, (tokenizer, state) -> {
			// Don't need to do anything on rule continuation
		}, (state) -> {
			// Don't need to do anything on rule end
		});

		setupReaderPragmas();
	}

	private static void setupReaderPragmas() {
		reader.addPragma("name", buildStringCollapserPragma("name"));

		reader.addPragma("author", buildStringCollapserPragma("author"));

		reader.addPragma("description",
				buildStringCollapserPragma("description"));

		reader.addPragma("version", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma version requires one integer argument");
			}

			String token = tokenizer.nextToken();

			try {
				state.setVersion(Integer.parseInt(token));
			} catch (NumberFormatException nfex) {
				PragmaFormatException pfex = new PragmaFormatException(
						"Argument " + token
								+ " to version pragma isn't a valid integer. "
								+ "This pragma requires a integer argument");

				pfex.initCause(nfex);

				throw pfex;
			}
		});
	}

	private static BiConsumer<FunctionalStringTokenizer, ComponentDescriptionState> buildStringCollapserPragma(
			String pragmaName) {
		return (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException("Pragma " + pragmaName
						+ " requires one string argument");
			}

			state.setName(ListUtils
					.collapseTokens(tokenizer.toList((strang) -> strang)));
		};
	}

	/**
	 * Parse a component description from a stream
	 * 
	 * @param inputSource
	 *            The stream to parse from
	 * @return The description parsed from the stream
	 */
	public static ComponentDescription fromStream(
			InputStream inputSource) {
		ComponentDescriptionState readState = reader
				.fromStream(inputSource, new ComponentDescriptionState());

		return readState.toDescription();
	}
}
