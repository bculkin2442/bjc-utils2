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
		reader = new RuleBasedConfigReader<>((fst, par) -> {
		}, (fst, stat) -> {
		}, (stat) -> {
		});

		reader.addPragma("name", (fst, stat) -> {
			if (!fst.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires at least one argument");
			} else {
				stat.setName(
						ListUtils.collapseTokens(fst.toList((s) -> s)));
			}
		});

		reader.addPragma("author", (fst, stat) -> {
			if (!fst.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma author requires at least one argument");
			} else {
				stat.setAuthor(
						ListUtils.collapseTokens(fst.toList((s) -> s)));
			}
		});

		reader.addPragma("description", (fst, stat) -> {
			if (!fst.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma description requires at least one argument");
			} else {
				stat.setDescription(
						ListUtils.collapseTokens(fst.toList((s) -> s)));
			}
		});

		reader.addPragma("version", (fst, stat) -> {
			if (!fst.hasMoreTokens()) {
				throw new PragmaFormatException(
						"Pragma name requires at least one argument");
			} else {
				String token = fst.nextToken();

				try {
					stat.setVersion(Integer.parseInt(token));
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
	 * @param is
	 *            The stream to parse from
	 * @return The description parsed from the stream
	 */
	public static ComponentDescription fromStream(InputStream is) {
		return reader.fromStream(is, new ComponentDescriptionState())
				.toDescription();
	}
}
