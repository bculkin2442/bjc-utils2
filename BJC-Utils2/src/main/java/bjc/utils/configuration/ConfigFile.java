package bjc.utils.configuration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.lang.model.SourceVersion;

/**
 * A config file with support for categories, comments and other thins
 * 
 * @author ben
 *
 */
public class ConfigFile {
	/**
	 * A category in a configuration file
	 * 
	 * @author ben
	 *
	 */
	public static class ConfigCategory {
		private Map<String, ConfigCategory> children;

		/**
		 * Create a new config category
		 */
		public ConfigCategory() {
			children = new HashMap<>();
		}

		/**
		 * Add a child category to this category
		 * 
		 * @param childName
		 *            The name of the child
		 * @param child
		 *            The child category
		 */
		public void addChild(String childName, ConfigCategory child) {
			children.put(childName, child);
		}
	}

	private static Map<String, ConfigCategory> topLevelCategories;

	private static boolean isCommentMarker(String token) {
		switch (token) {
			case "#":
			case "//":
				return true;
			default:
				return false;
		}
	}

	/**
	 * Parse the values in a config file from a stream
	 * 
	 * @param source
	 *            The stream to parse values from
	 * @return A config file with values parsed from the stream
	 */
	public static ConfigFile parse(InputStream source) {
		Scanner scn = new Scanner(source);

		ConfigFile returnedFile = new ConfigFile();

		while (scn.hasNextLine()) {
			String currentLine = scn.nextLine();

			// Ignore blank lines
			if (currentLine.equals("")) {
				continue;
			}

			String[] currentTokens = currentLine.split(" ");

			// Ignore lines that start with a comment marker
			if (isCommentMarker(currentTokens[0])) {
				continue;
			} else if (SourceVersion.isName(currentTokens[0])
					&& currentTokens[1].equals("{")) {
				topLevelCategories.put(currentTokens[0],
						parseCategory(currentTokens[0], scn));
			}
		}

		scn.close();

		return returnedFile;
	}

	private static ConfigCategory parseCategory(
			@SuppressWarnings("unused") String categoryName,
			Scanner inputSource) {
		ConfigCategory category = new ConfigCategory();

		String currentLine = inputSource.nextLine();

		String[] tokens = currentLine.split("\\s+ ");

		// Parse contents of category
		while (!tokens[0].equals("}")) {
			// Ignore lines starting with comment marker
			if (isCommentMarker(tokens[0])) {
				continue;
			}

			int initialCommandToken = 0;

			// Skip over blank tokens from lots of spacing
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];

				if (token.equals("")) {
					continue;
				} else {
					initialCommandToken = i;
					break;
				}
			}

			String[] relevantTokens = Arrays.copyOfRange(tokens,
					initialCommandToken, tokens.length);

			// Parse child subcategories
			if (SourceVersion.isName(relevantTokens[0])
					&& relevantTokens[1].equals("{")) {
				parseCategory(relevantTokens[0], inputSource);
			} else {
				// Parse config fields
				parseEntry(category, relevantTokens);
			}
			currentLine = inputSource.nextLine();

			tokens = currentLine.split("\\s+ ");
		}

		return category;
	}

	private static void parseEntry(
			@SuppressWarnings("unused") ConfigCategory category,
			String[] entryParts) {
		String entry = String.join("", entryParts);

		String[] expParts = entry.split("=");

		String[] expSpecifiers = expParts[0].split(":");

		String expType = expSpecifiers[0];
		@SuppressWarnings("unused")
		String expName = expSpecifiers[1];

		switch (expType) {

		}
	}
}
