package bjc.utils.ioutils.format;

import java.util.*;

import bjc.esodata.AbbrevMap2;
import bjc.utils.esodata.Tape;
import bjc.utils.parserutils.*;

/**
 * Represents a set of parameters to a CL format directive.
 *
 * @author Benjamin Culkin
 */
public class CLParameters {
	private static String MSG_FMT = "Invalid %s (%s) \"%s\" to %s directive";

	private static String RX_TRUE  = "(?i)y(?:es)?|t(?:rue)?(?i)";
	private static String RX_FALSE = "(?i)no?|f(?:alse)?(?i)";

	private CLValue[] params;

	private Set<String> abbrevWords;
	private AbbrevMap2   nameAbbrevs;

	private Map<String, CLValue>  namedParams;
	private Map<String, Integer> nameIndices;

	/**
	 * Create a new set of blank CL format parameters.
	 */
	public CLParameters() {
		this(new CLValue[0], new HashMap<>());
	}

	/**
	 * Create a new set of CL format parameters with unnamed values.
	 * 
	 * @param params
	 *        The CL format parameters to use.
	 */
	public CLParameters(CLValue[] params) {
		this(params, new HashMap<>());
	}

	/**
	 * Create a new set of CL format parameters with named values.
	 *
	 * @param namedParams
	 * 	The named parameters to use.
	 */
	public CLParameters(Map<String, CLValue> namedParams) {
		this(new CLValue[0], namedParams);
	}

	/**
	 * Create a new set of CL format parameters with both types of values.
	 *
	 * @param params
	 * 	The unnamed parameters to use.
	 *
	 * @param namedParams
	 * 	The named parameters to use.
	 */
	public CLParameters(CLValue[] params, Map<String, CLValue> namedParams) {
		this.params = params;

		this.namedParams  = namedParams;
		this.nameIndices = new HashMap<>();

		abbrevWords = new HashSet<>();
		nameAbbrevs = new AbbrevMap2();

		refreshAbbrevs();
	}

	// Refresh the mappings that track abbreviations
	private void refreshAbbrevs() {
		// @NOTE 9/19/18 @Cleanup @Leak Ben Culkin
		//
		// This never clears abbrevWords or nameAbbrevs, which I'm fine
		// with here, as these objects are fairly temporary.
		//
		// If it becomes an issue, I'll resolve it
		for (String key : namedParams.keySet()) {
			if (abbrevWords.contains(key)) continue;

			abbrevWords.add(key);
			nameAbbrevs.add(key);
		}

		for (String key : nameIndices.keySet()) {
			if (abbrevWords.contains(key)) continue;

			abbrevWords.add(key);
			nameAbbrevs.add(key);
		}
	}

	// Refresh a particular abbreviation
	private void refreshAbbrev(String key) {
		if (abbrevWords.contains(key)) return;

		abbrevWords.add(key);
		nameAbbrevs.add(key);
	}

	/**
	 * Map a set of names to indices.
	 *
	 * @param opts
	 * 	The names to bind to the parameter indices. The first one will be bound to index 0, and so
	 * 	forth. Pass an empty string to not bind a name to a particular index.
	 */
	public void mapIndices(String... opts) {
		for (int i = 0; i < opts.length; i++) {
			String opt = opts[i];

			if (!opt.equals("")) mapIndex(opt, i); 
		}

		refreshAbbrevs();
	}

	/**
	 * Map a singular name to an index.
	 *
	 * @param opt
	 * 	The name to map.
	 *
	 * @param idx
	 * 	The index to map it to.
	 */
	public void mapIndex(String opt, int idx) {
		mapIndex(opt, idx, true);
	}

	// Actually do the work of mapping an index
	private void mapIndex(String opt, int idx, boolean doRefresh) {
		if (params.length <= idx) {
			System.err.printf("WARN: Mapping invalid index %d (max %d) to \"%s\"\n",
					idx, params.length, opt.toUpperCase());
		}

		nameIndices.put(opt.toUpperCase(), idx);

		if (doRefresh) refreshAbbrevs();
	}

	/**
	 * Get a parameter by an index.
	 *
	 * @param idx
	 * 	The index to grab.
	 *
	 * @return The value at that index.
	 */
	public CLValue getByIndex(int idx) {
		if (idx < 0 || idx >= params.length) return null;

		return params[idx];
	}

	/**
	 * Get the length of the parameter list.
	 * 
	 * @return The length of the parameters.
	 */
	public int length() {
		return params.length;
	}

	/**
	 * Creates a set of parameters from a parameter string.
	 *
	 * This handles things like quoted strings, named parameters and the
	 * other special parameter features.
	 *
	 * @param unsplit
	 * 	The string to parse parameters from.
	 *
	 * @return A set of CL parameters.
	 */
	public static CLParameters fromDirective(String unsplit) {
		List<String> lParams = new ArrayList<>();

		StringBuilder currParm = new StringBuilder();

		char prevChar = ' ';
		// Are we currently in a string while we are parsing
		boolean inStr = false;

		// Parse out the parameters
		for (int i = 0; i < unsplit.length(); i++) {
			char c = unsplit.charAt(i);

			if (c == ',' && prevChar != '\'' && !inStr) {
				lParams.add(currParm.toString());
				
				currParm = new StringBuilder();
			} else if (c == '"' && prevChar != '\'') {
				inStr = true;
				
				currParm.append(c);
			} else if (inStr && c == '"' && prevChar != '\'') {
				inStr = false;

				currParm.append(c);
			} else {
				currParm.append(c);
			}

			prevChar = c;
		}

		// Add last parameter
		lParams.add(currParm.toString());

		List<CLValue> parameters = new ArrayList<>();

		// Special case empty blocks, so that we don't confuse people
		if (lParams.size() == 1 && lParams.get(0).equals("")) {
			return new CLParameters(parameters.toArray(new CLValue[0]));
		}

		Map<String, CLValue>  namedParams  = new HashMap<>();

		// Set up parameter names
		for(String param : lParams) {
			if (param.startsWith("#") && !param.equals("#")) {
				// Named parameter
				boolean setIndex = false;

				int nameIdx = 0;
				for (int i = 1; i < param.length(); i++) {
					char ch = param.charAt(i);

					if (ch == ':' || ch == ';') {
						// Semicolon says to add as
						// indexed parameter
						if (ch == ';') setIndex = true;

						nameIdx = i;
						break;
					}
				}

				// Trim off the 'hash' indicator
				String paramName = param.substring(1, nameIdx);
				String paramVal  = param.substring(nameIdx + 1);

				CLValue actVal = parseParam(paramVal);

				namedParams.put(paramName.toUpperCase(), actVal);

				if (setIndex) parameters.add(actVal);
			} else {
				parameters.add(parseParam(param));
			}
		}

		return new CLParameters(parameters.toArray(new CLValue[0]), namedParams);
	}

	// Actually parse the value for a parameter
	private static CLValue parseParam(String param) {
		String val = param;

		if (param.startsWith("\"")) {
			String dquote = param.substring(1, param.length() - 1);

			// String values get their escapes processed.
			val = TokenUtils.descapeString(dquote);
		}

		return CLValue.parse(val);
	}

	/**
	 * Get the corresponding value for a key.
	 *
	 * @param key
	 * 	The name of the parameter to look up.
	 *
	 * @return The value for that key, or null if none exists.
	 */
	public CLValue resolveKey(int key) {
		return resolveKey(Integer.toString(key));
	}

	/**
	 * Get the corresponding value for a key.
	 *
	 * @param key
	 * 	The name of the parameter to look up.
	 *
	 * @return The value for that key, or null if none exists.
	 */
	public CLValue resolveKey(String key) {
		String ucKey = key.toUpperCase();

		Set<String> keys = nameAbbrevs.deabbrevAll(ucKey);

		// We didn't find a parameter that could have been that. Create an appropriate and useful
		// error message.
		if (keys.size() > 1) {
			StringBuilder sb = new StringBuilder();

			sb.append("Ambiguous parameter name \"");
			sb.append(ucKey);
			sb.append("\". Could've meant: ");
			boolean isFirst = true;
			for (String possKey : keys) {
				if (!isFirst) sb.append(", ");
				if (isFirst) isFirst = false;

				sb.append("\"");
				sb.append(possKey);
				sb.append("\"");
			}
			sb.append(".");

			throw new IllegalArgumentException(sb.toString());
		}

		String actKey = keys.iterator().next();

		if (namedParams.containsKey(actKey)) {
			return namedParams.get(actKey);
		} else if (nameIndices.containsKey(actKey)) {
			int idx = nameIndices.get(actKey);

			// @NOTE 9/22/18
			//
			// Consider whether we should throw an exception here.
			if (idx < 0 || idx >= params.length) return null;

			return params[idx];
		} 

		return null;
	}

	/**
	 * Get a boolean-valued parameter.
	 *
	 * @param params
	 * 	The format parameters to use.
	 *
	 * @param key
	 * 	The name of the parameter to use for a key.
	 *
	 * @param paramName
	 * 	The name of the parameter, as a user-intelligble string.
	 *
	 * @param directive
	 * 	The directive this parameter belongs to.
	 *
	 * @param def
	 * 	The default value for this parameter.
	 *
	 * @return The boolean value for that parameter, or the default value if that parameter didn't
	 * exist.
	 */
	public boolean getBoolean(Tape<Object> params, String key, String paramName, String directive, boolean def) {
		String bol = resolveKey(key).getValue(params);

		if(!bol.equals("")) {
			if      (bol.matches(RX_TRUE))  return true;
			else if (bol.matches(RX_FALSE)) return false;
			else {
				String msg = String.format(MSG_FMT, paramName, key, bol, directive);
				throw new IllegalArgumentException(msg);
			}
		}

		return def;
	}

	/**
	 * Get the string value for a parameter.
	 *
	 * @param params
	 * 	The format parameters we're using.
	 *
	 * @param key
	 * 	The key for the parameter.
	 *
	 * @param paramName
	 * 	The user-intelligble name for the parameter.
	 *
	 * @param directive
	 * 	The directive this parameter is for.
	 *
	 * @param def
	 * 	The default value for the parameter.
	 *
	 * @return The string value of the parameter, or the default value if there is no parameter by
	 * that name.
	 */
	public String getString(Tape<Object> params, String key, String paramName, String directive, String def) {
		String vl = resolveKey(key).getValue(params);

		// @NOTE 9/19/17
		//
		// This raises the question of what to do if the empty string is a valid
		// value for a parameter
		if (!vl.equals("")) return vl;

		return def;
	}

	/**
	 * Get the character value of a parameter.
	 *
	 * @param params
	 * 	The format parameters to use.
	 *
	 * @param key
	 * 	The key for the parameter.
	 *
	 * @param paramName
	 * 	The user-intelligble name for the parameter.
	 *
	 * @param directive
	 * 	The directive the parameter is for.
	 *
	 * @return The character value of the parameter, or the default value if the parameter isn't
	 * specified.
	 */
	public char getChar(Tape<Object> params, String key, String paramName, String directive, char def) {
		String param = resolveKey(key).getValue(params);

		if (!param.equals("")) {
			if (param.length() == 1) {
				// Punt in the case we have a slightly malformed
				// character
				return param.charAt(0);
			}

			if(!param.startsWith("'")) {
				throw new IllegalArgumentException(
						String.format(MSG_FMT, paramName, key, param, directive));
			}

			return param.charAt(1);
		}

		return def;
	}

	/**
	 * Get the integer value for a parameter.
	 *
	 * @param params
	 * 	The format parameters we are using.
	 *
	 * @param key
	 * 	The key for the parameter.
	 *
	 * @param paramName
	 * 	The user-intelligble name for the parameter.
	 *
	 * @param directive
	 * 	The directive the parameter is for.
	 *
	 * @param def
	 * 	The default value for the parameter.
	 *
	 * @return The integer value of the parameter, or the default value if there is no parameter by
	 * that name.
	 */
	public int getInt(Tape<Object> params, String key, String paramName, String directive, int def) {
		String param = resolveKey(key).getValue(params);

		if (!param.equals("")) {
			try {
				return Integer.parseInt(param);
			} catch(NumberFormatException nfex) {
				String msg = String.format(MSG_FMT, paramName, key, param, directive);

				IllegalArgumentException iaex = new IllegalArgumentException(msg);
				iaex.initCause(nfex);

				throw iaex;
			}
		}

		return def;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		Set<Integer> seenIndices = new HashSet<>();

		int idx = 0;
		// First off, the named parameters
		for (Map.Entry<String, CLValue> param : namedParams.entrySet()) {
			String paramName = param.getKey();
			CLValue paramValue = param.getValue();

			if (nameIndices.containsKey(paramName)) {
				int paramIdx = nameIndices.get(paramName);

				String msg = String.format("%s(%d):'%s'", paramName, paramIdx, paramValue);

				if (idx != 0) sb.append(", ");
				sb.append(msg);

				seenIndices.add(idx);
			} else {
				String msg = String.format("%s:'%s'", paramName, paramValue);

				if (idx != 0) sb.append(", ");
				sb.append(msg);
			}

			idx += 1;
		}

		sb.append(";");

		// Second off, indexed parameters with a name
		for (Map.Entry<String, Integer> paramMap : nameIndices.entrySet()) {
			String paramName = paramMap.getKey();
			int paramIdx = paramMap.getValue();

			// We've already gotten this argument before
			if (seenIndices.contains(paramIdx)) continue;

			String msg = String.format("%d(%s):'%s'", paramIdx, paramName, params[paramIdx]);

			if (idx != 0) sb.append(", ");
			sb.append(msg);

			seenIndices.add(paramIdx);
		}

		sb.append(";");

		// Third, unnamed indexed parameters
		for (idx = 0; idx < params.length; idx++) {
			// We've already gotten this argument before
			if (seenIndices.contains(idx)) continue;

			String msg = String.format("%d:'%s'", idx, params[idx]);

			if (idx != 0) sb.append(", ");
			sb.append(msg);
		}

		sb.append("]");

		return sb.toString();
	}
}
