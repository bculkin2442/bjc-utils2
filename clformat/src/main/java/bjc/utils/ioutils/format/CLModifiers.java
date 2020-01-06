package bjc.utils.ioutils.format;

/**
 * A collection of the modifiers attached to a CL format directive.
 * 
 * @author EVE
 *
 */
public class CLModifiers {
	/**
	 * Whether the at mod is on.
	 */
	public final boolean atMod;
	
	/**
	 * Whether the colon mod is on.
	 */
	public final boolean colonMod;
	
	/**
	 * Whether the dollar mod is on.
	 */
	public final boolean dollarMod;
	
	/**
	 * Whether the star mod is on.
	 */
	public final boolean starMod;

	/**
	 * Create a new set of CL modifiers.
	 * 
	 * @param at
	 *        The state of the at mod.
	 * @param colon
	 *        The state of the colon mod.
	 * @param dollar
	 *        The state of the dollar mod.
	 */
	public CLModifiers(boolean at, boolean colon, boolean dollar, boolean star) {
		atMod     = at;
		colonMod  = colon;
		dollarMod = dollar;
		starMod   = star;
	}

	/**
	 * Create a set of modifiers from a modifier string.
	 * 
	 * @param modString
	 *        The string to parse modifiers from.
	 * @return A set of modifiers matching the string.
	 */
	public static CLModifiers fromString(String modString) {
		boolean atMod     = false;
		boolean colonMod  = false;
		boolean dollarMod = false;
		boolean starMod   = false;

		if(modString != null) {
			atMod     = modString.contains("@");
			colonMod  = modString.contains(":");
			dollarMod = modString.contains("$");
			starMod   = modString.contains("*");
		}

		return new CLModifiers(atMod, colonMod, dollarMod, starMod);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (atMod) sb.append('@');
		if (colonMod) sb.append(':');
		if (dollarMod) sb.append('$');
		if (starMod) sb.append('*');
		
		return sb.toString();
	}
}
