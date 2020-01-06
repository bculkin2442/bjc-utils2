package bjc.utils.ioutils.format;

import bjc.utils.esodata.*;

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
	 * 	The string to create the value from.
	 * 
	 * @return The CLValue represented by the string.
	 */
	public static CLValue parse(String val) {
		if (val == null) return new NullValue();

		if (val.equalsIgnoreCase("V")) {
			return new VValue();
		}

		switch (val) {
		case "V":
		case "v":
			return new VValue();
		case "#":
			return new HashValue();
		case "%":
			return new PercValue();
		default:
			return new LiteralValue(val);
		}
	}

	/**
	 * Get the value of the parameter.
	 *
	 * @param params
	 *	The parameters passed to the directive.
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
	 * 	The format parameters to use.
	 * 
	 * @param paramName
	 * 	The user-intelligble name for the value.
	 * 
	 * @param directive
	 * 	The directive this value is for.
	 * 
	 * @param def
	 * 	The default value for this value.
	 * 
	 * @return The value as an integer, or the default value if the value has no value.
	 */
	public default int asInt(Tape<Object> params, String paramName, String directive, int def) {
		String param = getValue(params);

		if (param != null && !param.equals("")) {
			try {
				return Integer.parseInt(param);
			} catch(NumberFormatException nfex) {
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
	 * 	The format parameters to use.
	 * 
	 * @param paramName
	 * 	The user-intelligble name for the value.
	 * 
	 * @param directive
	 * 	The directive the value is for.
	 * 
	 * @param def
	 * 	The default value for the value.
	 * 
	 * @return The value as an character, or the default value if the value has no value.
	 */
	public default char asChar(Tape<Object> params, String paramName, String directive, char def) {
		String param = getValue(params);

		if (param != null && !param.equals("")) {
			if (param.length() == 1) {
				// Punt in the case we have a slightly malformed
				// character
				return param.charAt(0);
			}

			if(!param.startsWith("'")) {
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
}

class PercValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		return Integer.toString(params.position());
	}
}

class HashValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		return (Integer.toString(params.size() - params.position()));
	}
}

class VValue implements CLValue {
	@Override
	public String getValue(Tape<Object> params) {
		// Read parameter from items
		Object par = params.item();
		boolean succ = params.right();

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
			return (String) par;
		} else {
			String msg = "Incorrect type of parameter for V inline parameter";

			throw new IllegalArgumentException(msg);
		}
	}
}

class LiteralValue implements CLValue {
	private String val;

	/**
	 * Create a new CLValue.
	 *
	 * @param vul
	 * 	The value of the parameter.
	 */
	public LiteralValue(String vul) {
		val = vul;
	}

	/**
	 * Get the value of the parameter.
	 *
	 * @param params
	 *	The parameters passed to the directive.
	 */
	public String getValue(Tape<Object> params) {
		return val;	
	}
}
