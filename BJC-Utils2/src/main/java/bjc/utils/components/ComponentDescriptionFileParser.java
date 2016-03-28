package bjc.utils.components;

import java.io.InputStream;

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
		reader = new RuleBasedConfigReader<>((fst, par) -> {
		}, (fst, stat) -> {
		}, (stat) -> {
		});

		reader.addPragma("name", (fst, stat) -> {
			stat.setName(ListUtils.collapseTokens(fst.toList((s) -> s)));
		});

		reader.addPragma("author", (fst, stat) -> {
			stat.setAuthor(ListUtils.collapseTokens(fst.toList((s) -> s)));
		});

		reader.addPragma("description", (fst, stat) -> {
			stat.setDescription(
					ListUtils.collapseTokens(fst.toList((s) -> s)));
		});

		reader.addPragma("version", (fst, stat) -> {
			stat.setVersion(Integer.parseInt(fst.nextToken()));
		});
	}

	/**
	 * Parse a component description from a stream
	 * 
	 * @param is
	 *            The stream to parse from
	 * @return The description parsed from the stream
	 */
	public ComponentDescription fromStream(InputStream is) {
		return reader.fromStream(is, new ComponentDescriptionState())
				.toDescription();
	}
}
