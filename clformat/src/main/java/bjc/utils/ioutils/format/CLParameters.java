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

			if (!opt.equals("")) nameIndices.put(opt, i);
		}
	}

	public void mapIndex(String opt, int idx) {
		nameIndices.put(opt, idx);
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
	 * Mostly, this just fills in V and # parameters.
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
		lParams.add(currParm.toString());

		List<String> parameters = new ArrayList<>();

		if (lParams.size() == 1 && lParams.get(0).equals(""))
			return new CLParameters(parameters.toArray(new String[0]));

		Map<String, String>  namedParams  = new HashMap<>();
		Map<String, Integer> nameIndices = new HashMap<>();

		int currParamNo = 0;
		for(String param : lParams) {
			if (param.startsWith("#") && !param.equals("#")) {
				boolean setIndex = false;

				int nameIdx = 0;
				for (int i = 1; i < param.length(); i++) {
					char ch = param.charAt(i);

					if (ch == ':' || ch == ';') {
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

	/**
	 * Get an optional character parameter with a default value.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @param def
	 *        The default value for the parameter.
	 * @return The value of the parameter if it exists, or the default
	 *         otherwise.
	 */
	public boolean getBooleanDefault(int idx, String paramName, String directive, boolean def) {
		if(!params[idx].equals("")) {
			return getBoolean(idx, paramName, directive);
		}

		return def;
	}

	/**
	 * Get a mandatory character parameter.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @return The value for the parameter.
	 */
	public boolean getBoolean(int idx, String paramName, String directive) {
		String bol = params[idx];

		if      (bol.matches("[Yy](?:es)?|[Tt](?:rue)?"))  return true;
		else if (bol.matches("[Nn]o?|[Ff](?:alse)?"))      return false;
		else {
			String msg = String.format("Invalid %s \"%s\" to %s directive", paramName, bol, directive);
			throw new IllegalArgumentException(msg);
		}
	}
	/**
	 * Get an optional character parameter with a default value.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @param def
	 *        The default value for the parameter.
	 * @return The value of the parameter if it exists, or the default
	 *         otherwise.
	 */
	public String getStringDefault(int idx, String paramName, String directive, String def) {
		if(!params[idx].equals("")) {
			return getString(idx, paramName, directive);
		}

		return def;
	}

	/**
	 * Get a mandatory character parameter.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @return The value for the parameter.
	 */
	public String getString(int idx, String paramName, String directive) {
		return params[idx];
	}

	/**
	 * Get an optional character parameter with a default value.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @param def
	 *        The default value for the parameter.
	 * @return The value of the parameter if it exists, or the default
	 *         otherwise.
	 */
	public char getCharDefault(int idx, String paramName, String directive, char def) {
		if(!params[idx].equals("")) {
			return getChar(idx, paramName, directive);
		}

		return def;
	}

	/**
	 * Get a mandatory character parameter.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @return The value for the parameter.
	 */
	public char getChar(int idx, String paramName, String directive) {
		String param = params[idx];

		if (param.length() == 1) {
			// Punt in the case we have a slightly malformed
			// character
			return param.charAt(0);
		}

		if(!param.startsWith("'")) {
			throw new IllegalArgumentException(
					String.format("Invalid %s \"%s\" to %s directive", paramName, param, directive));
		}

		return param.charAt(1);
	}

	// @TODO
	//
	// Add getString and getStringDefault

	/**
	 * Get an optional integer parameter with a default value.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @param def
	 *        The default value for the parameter.
	 * @return The value of the parameter if it exists, or the default
	 *         otherwise.
	 */
	public int getIntDefault(int idx, String paramName, String directive, int def) {
		if(!params[idx].equals("")) {
			return getInt(idx, paramName, directive);
		}

		return def;
	}

	/**
	 * Get a mandatory integer parameter.
	 * 
	 * @param idx
	 *        The index the parameter is at.
	 * @param paramName
	 *        The name of the parameter.
	 * @param directive
	 *        The directive this parameter belongs to.
	 * @return The value for the parameter.
	 */
	public int getInt(int idx, String paramName, String directive) {
		String param = params[idx];

		try {
			return Integer.parseInt(param);
		} catch(NumberFormatException nfex) {
			String msg = String.format("Invalid %s \"%s\" to %s directive", paramName, param, directive);

			IllegalArgumentException iaex = new IllegalArgumentException(msg);
			iaex.initCause(nfex);

			throw iaex;
		}
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
