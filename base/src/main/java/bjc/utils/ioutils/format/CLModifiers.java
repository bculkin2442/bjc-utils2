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
	public final boolean	atMod;
	/**
	 * Whether the colon mod is on.
	 */
	public final boolean	colonMod;

	/**
	 * Create a new set of CL modifiers.
	 * 
	 * @param at
	 *        The state of the at mod.
	 * @param colon
	 *        The state of the colon mod.
	 */
	public CLModifiers(boolean at, boolean colon) {
		atMod = at;
		colonMod = colon;
	}

	/**
	 * Create a set of modifiers from a modifier string.
	 * 
	 * @param modString
	 *        The string to parse modifiers from.
	 * @return A set of modifiers matching the string.
	 */
	public static CLModifiers fromString(String modString) {
		boolean atMod = false;
		boolean colonMod = false;
		if(modString != null) {
			atMod = modString.contains("@");
			colonMod = modString.contains(":");
		}

		return new CLModifiers(atMod, colonMod);
	}
}