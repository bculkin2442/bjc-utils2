package bjc.utils.ioutils.format;

import java.util.ArrayList;
import java.util.List;

import bjc.utils.esodata.Tape;

/**
 * Represents a set of parameters to a CL format directive.
 *
 * @author Benjamin Culkin
 */
public class CLParameters {
	private String[] params;

	/**
	 * Create a new set of CL format parameters.
	 * 
	 * @param params
	 *        The CL format parameters to use.
	 */
	public CLParameters(String[] params) {
		this.params = params;
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

		for (int i = 0; i < unsplit.length(); i++) {
			char c = unsplit.charAt(i);

			if (c == ',' && prevChar != '\'') {
				lParams.add(currParm.toString());
				
				currParm = new StringBuilder();
			} else {
				currParm.append(c);
			}

			prevChar = c;
		}
		lParams.add(currParm.toString());

		List<String> parameters = new ArrayList<>();

		if (lParams.size() == 1 && lParams.get(0).equals(""))
			return new CLParameters(parameters.toArray(new String[0]));

		for(String param : lParams) {
			if(param.equalsIgnoreCase("V")) {
				Object par = dirParams.item();
				boolean succ = dirParams.right();

				if(!succ) {
					throw new IllegalStateException("Couldn't advance tape for parameter");
				}

				if(par == null) {
					throw new IllegalArgumentException(
							"Expected a format parameter for V inline parameter");
				}

				if(par instanceof Number) {
					int val = ((Number) par).intValue();

					parameters.add(Integer.toString(val));
				} else if(par instanceof Character) {
					char ch = ((Character) par);

					parameters.add(Character.toString(ch));
				} else {
					throw new IllegalArgumentException(
							"Incorrect type of parameter for V inline parameter");
				}
			} else if (param.equals("#")) {
				parameters.add(Integer.toString(dirParams.size() - dirParams.position()));
			} else if (param.equals("%")) {
				parameters.add(Integer.toString(dirParams.position()));
			} else {
				parameters.add(param);
			}
		}

		return new CLParameters(parameters.toArray(new String[0]));
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
	public char getCharDefault(int idx, String paramName, char directive, char def) {
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
	public char getChar(int idx, String paramName, char directive) {
		String param = params[idx];

		if (param.length() == 1) {
			// Punt in the case we have a slightly malformed issue
			return param.charAt(0);
		}

		if(!param.startsWith("'")) {
			throw new IllegalArgumentException(
					String.format("Invalid %s \"%s\" to %c directive", paramName, param, directive));
		}

		return param.charAt(1);
	}

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
	public int getIntDefault(int idx, String paramName, char directive, int def) {
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
	public int getInt(int idx, String paramName, char directive) {
		String param = params[idx];

		try {
			return Integer.parseInt(param);
		} catch(NumberFormatException nfex) {
			String msg = String.format("Invalid %s \"%s\" to %c directive", paramName, param, directive);

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
