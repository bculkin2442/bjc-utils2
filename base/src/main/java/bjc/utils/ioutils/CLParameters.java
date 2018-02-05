package bjc.utils.ioutils;

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

	public CLParameters(String[] params) {
		this.params = params;
	}

	public int length() {
		return params.length;
	}

	/**
	 * Creates a set of parameters from an array of parameters.
	 *
	 * Mostly, this just fills in V and # parameters.
	 *
	 * @param params
	 *                The parameters of the directive.
	 * @param dirParams
	 *                The parameters of the format string.
	 *
	 * @return A set of CL parameters.
	 */
	public static CLParameters fromDirective(String[] params, Tape<Object> dirParams) {
		List<String> parameters = new ArrayList<>();

		for (String param : params) {
			if (param.equalsIgnoreCase("V")) {
				Object par = dirParams.item();
				boolean succ = dirParams.right();

				if (!succ) {
					throw new IllegalStateException("Couldn't advance tape for parameter");
				}

				if (par == null) {
					throw new IllegalArgumentException(
							"Expected a format parameter for V inline parameter");
				}

				if (par instanceof Number) {
					int val = ((Number) par).intValue();

					parameters.add(Integer.toString(val));
				} else if (par instanceof Character) {
					char ch = ((Character) par);

					parameters.add(Character.toString(ch));
				} else {
					throw new IllegalArgumentException(
							"Incorrect type of parameter for V inline parameter");
				}
			} else if (param.equals("#")) {
				parameters.add(Integer.toString(dirParams.position()));
			} else {
				parameters.add(param);
			}
		}

		return new CLParameters(parameters.toArray(new String[0]));
	}

	public char getCharDefault(int idx, String paramName, char directive, char def) {
		if (!params[idx].equals("")) {
			return getChar(idx, paramName, directive);
		}

		return def;
	}

	public char getChar(int idx, String paramName, char directive) {
		String param = params[idx];

		if (!param.startsWith("'")) {
			throw new IllegalArgumentException(
					String.format("Invalid %s %s to %c directive", paramName, param, directive));
		}

		return param.charAt(1);
	}

	public int getIntDefault(int idx, String paramName, char directive, int def) {
		if (!params[idx].equals("")) {

		}

		return def;
	}

	public int getInt(int idx, String paramName, char directive) {
		String param = params[idx];

		try {
			return Integer.parseInt(param);
		} catch (NumberFormatException nfex) {
			String msg = String.format("Invalid %s %s to %c directive", paramName, param, directive);

			IllegalArgumentException iaex = new IllegalArgumentException(msg);
			iaex.initCause(nfex);

			throw iaex;
		}
	}
}
