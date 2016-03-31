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
		}, (tokenizer, state) -> {
		}, (state) -> {
		});

		reader.addPragma("name", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires at least one argument");
			} else {
				state.setName(ListUtils.collapseTokens(
						tokenizer.toList((strang) -> strang)));
			}
		});

		reader.addPragma("author", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma author requires at least one argument");
			} else {
				state.setAuthor(ListUtils.collapseTokens(
						tokenizer.toList((strang) -> strang)));
			}
		});

		reader.addPragma("description", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma description requires at least one argument");
			} else {
				state.setDescription(ListUtils.collapseTokens(
						tokenizer.toList((strang) -> strang)));
			}
		});

		reader.addPragma("version", (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires at least one argument");
			} else {
				String token = tokenizer.nextToken();

				try {
					state.setVersion(Integer.parseInt(token));
				} catch (NumberFormatException nfex) {
					throw new PragmaFormatException("Argument " + token
							+ " to version pragma isn't a valid integer. "
							+ "This pragma requires a integer argument");
				}
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
	public static ComponentDescription
			fromStream(InputStream inputSource) {
		ComponentDescriptionState readState = reader
				.fromStream(inputSource, new ComponentDescriptionState());

		return readState.toDescription();
	}
}
