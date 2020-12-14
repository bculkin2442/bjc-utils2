package bjc.utils.ioutils.format;

/**
 * A decree is the building blocks of what we need to pick and call a directive.
 *
 * Namely, it is the name of the directive, any modifiers attached to the
 * directive, and any prefix parameters that are also attached to the directive.
 *
 * @author Ben Culkin.
 */
public class SimpleDecree implements Decree {
	/**
	 * The name of the directive.
	 */
	public String name;

	/**
	 * Is this directive an actual directive, or just a literal string?
	 */
	public boolean isLiteral;

	/**
	 * Is this directive a user function call?
	 */
	public boolean isUserCall;

	/**
	 * The prefix parameters for this directive.
	 */
	public CLParameters parameters;

	/**
	 * The modifiers for this directive.
	 */
	public CLModifiers modifiers;

	/**
	 * Create a new blank decree.
	 */
	public SimpleDecree() {

	}

	/**
	 * Create a new literal text directive.
	 *
	 * @param txt
	 *            The text of the directive.
	 */
	public SimpleDecree(String txt) {
		this.name = txt;

		this.isLiteral = true;
	}

	/**
	 * Create a new directive.
	 *
	 * @param name
	 *               The name of the directive. Whether or not it is an actual
	 *               directive will be auto-determined (if it starts with a ~, it's
	 *               a directive.)
	 *
	 * @param params
	 *               The prefix parameters to the directive.
	 *
	 * @param mods
	 *               The modifiers to the directive.
	 */
	public SimpleDecree(String name, CLParameters params, CLModifiers mods) {
		this.name = name;

		this.parameters = params;

		this.modifiers = mods;

		this.isLiteral = false;
	}

	/**
	 * Create a new directive that may be a user function.
	 *
	 * @param name
	 *               The name of the directive. Whether or not it is an actual
	 *               directive will be auto-determined (if it starts with a ~ and is
	 *               not a user function, it's a directive.)
	 *
	 * @param isUser
	 *               Is this directive a user function?
	 *
	 * @param params
	 *               The prefix parameters to the directive.
	 *
	 * @param mods
	 *               The modifiers to the directive.
	 */
	public SimpleDecree(String name, boolean isUser, CLParameters params, CLModifiers mods) {
		this.name = name;

		this.parameters = params;

		this.modifiers = mods;

		this.isUserCall = isUser;

		this.isLiteral = isUser;
	}

	/**
	 * Check if this decree is a non-literal, with a particular name.
	 *
	 * @param nam
	 *            The name to see if we have.
	 *
	 * @return Whether or not the provided name equals our name.
	 */
	public boolean isNamed(String nam) {
		// Literals don't have a meaningful name
		if (isLiteral) return false;
		else           return name.equals(nam);
	}

	@Override
	public String toString() {
		return String.format(
				"Decree [name='%s', isLiteral=%s, isUserCall=%s, parameters=%s, modifiers='%s']",
				name, isLiteral, isUserCall, parameters, modifiers);
	}
}
