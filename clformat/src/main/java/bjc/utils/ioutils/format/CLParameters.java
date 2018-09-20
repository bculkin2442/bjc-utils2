package bjc.utils.ioutils.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private String[] params;

	private Map<String, String>  namedParams;
	private Map<String, Integer> nameIndices;

	/**
	 * Create a new set of CL format parameters.
	 * 
	 * @param params
	 *        The CL format parameters to use.
	 */
	public CLParameters(String[] params) {
		this(params, new HashMap<>());
	}

	public CLParameters(Map<String, String> namedParams) {
		this(new String[0], namedParams);
	}

	public CLParameters(String[] params, Map<String, String> namedParams) {
		this.params = params;

		this.namedParams  = namedParams;
		this.nameIndices = new HashMap<>();
	}

	public void mapIndices(String... opts) {
		for (int i = 0; i < opts.length; i++) {
			String opt = opts[i];

			if (!opt.equals("")) mapIndex(opt, i); 
		}
	}

	public void mapIndex(String opt, int idx) {
		if (params.length <= idx) System.err.printf("WARN: Mapping invalid index %d (max %d) to \"%s\"\n", idx, params.length, opt);
		nameIndices.put(opt, idx);
	}

	public String getRaw(int idx) {
		if (idx < 0 || idx >= params.length) return "Out of Bounds";

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
	 * Creates a set of parameters from an array of parameters.
	 *
	 * This handles things like quoted strings, named parameters and the
	 * other special parameter features.
	 *
	 * @param params
	 *        The parameters of the directive.
	 * @param dirParams
	 *        The parameters of the format string.
	 *
	 * @return A set of CL parameters.
	 */
	public static CLParameters fromDirective(String unsplit, Tape<Object> dirParams) {
		List<String>  lParams   = new ArrayList<>();
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

		List<String> parameters = new ArrayList<>();

		// Special case empty blocks, so that we don't confuse people
		if (lParams.size() == 1 && lParams.get(0).equals(""))
			return new CLParameters(parameters.toArray(new String[0]));

		Map<String, String>  namedParams  = new HashMap<>();
		Map<String, Integer> nameIndices = new HashMap<>();

		int currParamNo = 0;
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

				String actVal = parseParam(paramVal, dirParams);

				namedParams.put(paramName, actVal);

				if (setIndex) parameters.add(actVal);
			} else {
				parameters.add(parseParam(param, dirParams));
			}

			currParamNo += 1;
		}

		return new CLParameters(parameters.toArray(new String[0]), namedParams);
	}

	private static String parseParam(String param, Tape<Object> dirParams) {
		if(param.equalsIgnoreCase("V")) {
			// Read parameter from items
			Object par = dirParams.item();
			boolean succ = dirParams.right();

			if(!succ) {
				throw new IllegalStateException("Couldn't advance tape for parameter");
			} else if(par == null) {
				throw new IllegalArgumentException(
						"Expected a format parameter for V inline parameter");
			} 

			if(par instanceof Number) {
				int val = ((Number) par).intValue();

				return Integer.toString(val);
			} else if(par instanceof Character) {
				char ch = ((Character) par);

				return Character.toString(ch);
			} else if (par instanceof String) {
				return (String)par;
			} else {
				throw new IllegalArgumentException(
						"Incorrect type of parameter for V inline parameter");
			}
		} else if (param.equals("#")) {
			return (Integer.toString(dirParams.size() - dirParams.position()));
		} else if (param.equals("%")) {
			return Integer.toString(dirParams.position());
		} else if (param.startsWith("\"")) {
			String dquote = param.substring(1, param.length() - 1);

			return TokenUtils.descapeString(dquote);
		}

		return param;
	}

	private String resolveKey(String key) {
		if      (nameIndices.containsKey(key)) return params[nameIndices.get(key)];
		else if (namedParams.containsKey(key)) return namedParams.get(key);

		return "";
	}

	public boolean getBoolean(String key, String paramName, String directive, boolean def) {
		String bol = resolveKey(key);

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

	public String getString(String key, String paramName, String directive, String def) {
		String vl = resolveKey(key);

		// @NOTE 9/19/17
		//
		// This raises the question of what to do if the empty string is a valid
		// value for a parameter
		if (!vl.equals("")) return vl;

		return def;
	}

	public char getChar(String key, String paramName, String directive, char def) {
		String param = resolveKey(key);

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

	public int getInt(String key, String paramName, String directive, int def) {
		String param = resolveKey(key);

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
