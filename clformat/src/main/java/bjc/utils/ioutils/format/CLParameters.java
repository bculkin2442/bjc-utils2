package bjc.utils.ioutils.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bjc.utils.esodata.AbbrevMap;
import bjc.utils.esodata.Tape;
import bjc.utils.parserutils.TokenUtils;

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
	private AbbrevMap   nameAbbrevs;

	private Map<String, CLValue>  namedParams;
	private Map<String, Integer> nameIndices;

	/**
	 * Create a new set of CL format parameters.
	 * 
	 * @param params
	 *        The CL format parameters to use.
	 */
	public CLParameters(CLValue[] params) {
		this(params, new HashMap<>());
	}

	public CLParameters(Map<String, CLValue> namedParams) {
		this(new CLValue[0], namedParams);
	}

	public CLParameters(CLValue[] params, Map<String, CLValue> namedParams) {
		this.params = params;

		this.namedParams  = namedParams;
		this.nameIndices = new HashMap<>();

		abbrevWords = new HashSet<>();
		nameAbbrevs = new AbbrevMap();

		refreshAbbrevs();
	}

	private void refreshAbbrevs() {
		// @NOTE 9/19/18
		//
		// @Cleanup @Leak
		//
		// This never clears abbrevWords or nameAbbrevs, which I'm fine
		// with here, as these objects are fairly temporary.
		//
		// If it becomes an issue, I'll resolve it
		for (String key : namedParams.keySet()) {
			if (abbrevWords.contains(key)) continue;

			abbrevWords.add(key);
			nameAbbrevs.addWords(key);
		}

		for (String key : nameIndices.keySet()) {
			if (abbrevWords.contains(key)) continue;

			abbrevWords.add(key);
			nameAbbrevs.addWords(key);
		}
	}

	private void refreshAbbrev(String key) {
		if (abbrevWords.contains(key)) return;

		abbrevWords.add(key);
		nameAbbrevs.addWords(key);
	}

	public void mapIndices(String... opts) {
		for (int i = 0; i < opts.length; i++) {
			String opt = opts[i];

			if (!opt.equals("")) mapIndex(opt, i); 
		}

		refreshAbbrevs();
	}

	public void mapIndex(String opt, int idx) {
		mapIndex(opt, idx, true);
	}

	private void mapIndex(String opt, int idx, boolean doRefresh) {
		if (params.length <= idx) System.err.printf("WARN: Mapping invalid index %d (max %d) to \"%s\"\n", idx, params.length, opt.toUpperCase());

		nameIndices.put(opt.toUpperCase(), idx);

		if (doRefresh) refreshAbbrevs();
	}

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
	 *        The string to parse parameters from
	 * @param dirParams
	 *        The parameters of the format string.
	 *
	 * @return A set of CL parameters.
	 */
	public static CLParameters fromDirective(String unsplit) {
		List<String> lParams = new ArrayList<>();

		StringBuilder currParm = new StringBuilder();

		char prevChar = ' ';
		boolean inStr = false;

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
		if (lParams.size() == 1 && lParams.get(0).equals(""))
			return new CLParameters(parameters.toArray(new CLValue[0]));

		Map<String, CLValue>  namedParams  = new HashMap<>();

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

				String paramName = param.substring(0, nameIdx);
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

	private static CLValue parseParam(String param) {
		String val = param;

		if (param.startsWith("\"")) {
			String dquote = param.substring(1, param.length() - 1);

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

		if (!abbrevWords.contains(ucKey)) refreshAbbrev(ucKey);

		String[] keys = nameAbbrevs.deabbrev(ucKey);
		if (keys.length > 1) {
			StringBuilder sb = new StringBuilder();

			sb.append("Ambiguous parameter name \"");
			sb.append(ucKey);
			sb.append("\". Could've meant: ");
			for (int i = 0; i < keys.length; i++) {
				sb.append("\"");
				sb.append(keys[i]);
				sb.append("\"");
				if (i < keys.length - 1) sb.append(", ");
			}
			sb.append(".");

			throw new IllegalArgumentException(sb.toString());
		}

		String actKey = keys[0];

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

	public String getString(Tape<Object> params, String key, String paramName, String directive, String def) {
		String vl = resolveKey(key).getValue(params);

		// @NOTE 9/19/17
		//
		// This raises the question of what to do if the empty string is a valid
		// value for a parameter
		if (!vl.equals("")) return vl;

		return def;
	}

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

		for (int i = 0; i < params.length; i++) {
			if (i != 0) sb.append(", ");

			sb.append("\"");
			sb.append(params[i]);
			sb.append("\"");
		}

		sb.append("]");

		return sb.toString();
	}
}
