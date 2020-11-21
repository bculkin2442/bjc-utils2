package bjc.utils.ioutils.format;

import bjc.esodata.*;

// @TODO Nov 13th, 2020 Ben Culkin :DefaultValues
// Create some way to specify default values for the arguments to the various
// asWhatever methods. This will cleanup use-site code for them

/* @TODO Nov 13th, 2020 Ben Culkin :ParamVariables
 * Create a new CLValue type that implements variables in a way. There are a
 * number of different sorts of 'scopes' that could be useful, so here is the
 * list of them:
 * - Static variables, stored on the CLString instance
 * - Global variables, stored on a map set on the FormatContext when formatting
 *   starts, and copied over whenever a new context is built.
 * - Lexical variables, stored on the FormatContext, but in some sort of map which
 *   models lexical scopes (extend on FunctionalMap may do the right thing, not sure)
 * - Local variables, stored on the FormatContext as well, but these
 *   aren't copied over when a new context is built.
 *   
 *   For static/global variables, maybe some equivalent of 'local' from perl to
 *   localize them.
 */
/**
 * Represents a parameter value to an edict that may have a dynamic value
 * obtained from the format arguments.
 *
 * @author Ben Culkin
 */
public interface CLValue {
	/**
	 * Create a CLValue from a string.
	 *
	 * @param val
	 *            The string to create the value from.
	 *
	 * @return The CLValue represented by the string.
	 */
	public static CLValue parse(String val) {
		if (val == null) return new NullValue();

		switch (val) {
		case "V": // Fall-through, V is the same as v
		case "v": return new VValue();
		case "#": return new HashValue();
		case "%": return new PercValue();
		default:  return new LiteralValue(val);
		}
	}

	/**
	 * Get the value of the parameter.
	 *
	 * @param params
	 *               The parameters passed to the directive.
	 *
	 * @return The string value of the parameter.
	 */
	public String getValue(Tape<Object> params);

	/**
	 * The format string to use for an invalid usage of a directive.
	 */
	public static final String MSG_FMT = "Invalid %s \"%s\" to %s directive";

	/**
	 * Get the value as an integer.
	 *
	 * @param params
	 *                  The format parameters to use.
	 *
	 * @param paramName
	 *                  The user-intelligible name for the value.
	 *
	 * @param directive
	 *                  The directive this value is for.
	 *
	 * @param def
	 *                  The default value for this value.
	 *
	 * @return The value as an integer, or the default value if the value has no
	 *         value.
	 */
	public default int asInt(Tape<Object> params, String paramName, String directive,
			int def) {
		String param = getValue(params);

		if (param != null && !param.equals("")) {
			try {
				return Integer.parseInt(param);
			} catch (NumberFormatException nfex) {
				String msg = String.format(MSG_FMT, paramName, param, directive);

				IllegalArgumentException iaex = new IllegalArgumentException(msg);
				iaex.initCause(nfex);

				throw iaex;
			}
		}

		return def;
	}

	/**
	 * Get a CLValue that represent 'nothing'.
	 *
	 * @return A CLValue that represents nothing.
	 */
	public static CLValue nil() {
		return new NullValue();
	}

	/**
	 * Get the value as a character.
	 *
	 * @param params
	 *                  The format parameters to use.
	 *
	 * @param paramName
	 *                  The user-intelligible name for the value.
	 *
	 * @param directive
	 *                  The directive the value is for.
	 *
	 * @param def
	 *                  The default value for the value.
	 *
	 * @return The value as an character, or the default value if the value has no
	 *         value.
	 */
	public default char asChar(Tape<Object> params, String paramName, String directive,
			char def) {
		String param = getValue(params);

		if (param != null && !param.equals("")) {
			if (param.length() == 1) {
				// Punt in the case we have a slightly malformed
				// character
				return param.charAt(0);
			}

			if (!param.startsWith("'")) {
				throw new IllegalArgumentException(
						String.format(MSG_FMT, paramName, param, directive));
			}

			return param.charAt(1);
		}

		return def;
	}

}

class NullValue implements CLValue {
	public static CLValue nullVal = new NullValue();

	@Override
	public String getValue(Tape<Object> params) {
		return null;
	}

	@Override
	public String toString() {
		return String.format("NullValue []");
	}
}

class PercValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		return Integer.toString(params.position());
	}

	@Override
	public String toString() {
		return String.format("PercValue []");
	}
}

class HashValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		return (Integer.toString(params.size() - params.position()));
	}

	@Override
	public String toString() {
		return String.format("HashValue []");
	}
}

class VValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		// Read parameter from items
		Object par = params.item();
		boolean succ = params.right();

		if (!succ) {
			throw new IllegalStateException("Couldn't advance tape for parameter");
		} else if (par == null) {
			throw new IllegalArgumentException(
					"Expected a format parameter for V inline parameter");
		}

		if (par instanceof Number) {
			int val = ((Number) par).intValue();

			return Integer.toString(val);
		} else if (par instanceof Character) {
			char ch = ((Character) par);

			return Character.toString(ch);
		} else if (par instanceof String) {
			return (String) par;
		} else {
			String msg = "Incorrect type of parameter for V inline parameter";

			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public String toString() {
		return String.format("VValue []");
	}
}

class LiteralValue implements CLValue {
	private String val;

	/**
	 * Create a new CLValue.
	 *
	 * @param vul
	 *            The value of the parameter.
	 */
	public LiteralValue(String vul) {
		val = vul;
	}

	/**
	 * Get the value of the parameter.
	 *
	 * @param params
	 *               The parameters passed to the directive.
	 */
	@Override
	public String getValue(Tape<Object> params) {
		return val;
	}

	@Override
	public String toString() {
		return String.format("LiteralValue [val=%s]", val);
	}
}
