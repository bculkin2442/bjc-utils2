package bjc.utils.components;

import java.io.InputStream;

import bjc.utils.exceptions.PragmaFormatException;
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

		reader.addPragma("name", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires one string argument");
			}

			state.setName(ListUtils
					.collapseTokens(tokenizer.toList((strang) -> strang)));
		});

		reader.addPragma("author", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma author requires one string argument");
			}

			state.setAuthor(ListUtils
					.collapseTokens(tokenizer.toList((strang) -> strang)));
		});

		reader.addPragma("description", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma description requires one string argument");
			}

			state.setDescription(ListUtils
					.collapseTokens(tokenizer.toList((strang) -> strang)));
		});

		reader.addPragma("version", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires one integer argument");
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
