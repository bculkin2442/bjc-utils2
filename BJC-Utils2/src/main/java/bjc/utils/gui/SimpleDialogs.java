package bjc.utils.gui;

import java.awt.Component;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JOptionPane;

/**
 * Utility class for getting simple input from the user.
 * @author ben
 *
 */
public class SimpleDialogs {
	/**
	 * Get a bounded integer from the user.
	 * @param parent The parent component for the dialogs.
	 * @param title The title for the dialogs.
	 * @param prompt The prompt to tell the user what to enter.
	 * @param lower The lower integer bound to accept.
	 * @param upper The upper integer bound to accept.
	 * @return A int within the specified bounds.
	 */
	public static int getBoundedInt(Component parent, String title,
			String prompt, int lower, int upper) {
		return getValue(parent, title, prompt, s -> {
			try {
				int n = Integer.parseInt(s);
				return (n < upper) && (n > lower);
			} catch (NumberFormatException nfe) {
				return false;
			}
		} , Integer::parseInt);
	}

	/**
	 * Get a integer from the user
	 * @param parent The parent component for dialogs.
	 * @param title The title for dialogs.
	 * @param prompt The prompt to tell the user what to enter.
	 * @return A int.
	 */
	public static int getInt(Component parent, String title,
			String prompt) {
		return getValue(parent, title, prompt, s -> {
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		} , Integer::parseInt);
	}

	/**
	 * Get a string from the user
	 * @param parent The parent component for dialogs.
	 * @param title The title for the dialogs.
	 * @param prompt The prompt to tell the user what to enter.
	 * @return A string.
	 */
	public static String getString(Component parent, String title, String prompt) {
		return JOptionPane.showInputDialog(parent, prompt, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Get a value parsable from a string from the user.
	 * @param parent The parent component for dialogs.
	 * @param title The title for dialogs.
	 * @param prompt The prompt to tell the user what to enter.
	 * @param p A predicate to determine if a input is valid.
	 * @param f The function to transform the string into a value.
	 * @return The value parsed from a string.
	 */
	public static <E> E getValue(Component parent, String title,
			String prompt, Predicate<String> p, Function<String, E> f) {
		String inp = getString(parent, title, prompt);

		while (!p.test(inp)) {
			showError(parent, "I/O Error", "Please enter a valid value");

			inp = getString(parent, title, prompt);
		}

		return f.apply(inp);
	}

	/**
	 * Get a whole number from the user.
	 * @param parent The parent component for dialogs.
	 * @param title The title for dialogs.
	 * @param prompt The prompt to tell the user what to enter.
	 * @return A whole number.
	 */
	public static int getWhole(Component parent, String title,
			String prompt) {
		return getBoundedInt(parent, title, prompt, 0, Integer.MAX_VALUE);
	}

	/**
	 * Ask the user a Yes/No question.
	 * @param parent The parent component for dialogs.
	 * @param title The title for dialogs.
	 * @param question The question to ask the user.
	 * @return True if the user said yes, false otherwise.
	 */
	public static boolean getYesNo(Component parent, String title,
			String question) {
		int res = JOptionPane.showConfirmDialog(parent, question, title,
				JOptionPane.YES_NO_OPTION);

		return (res == JOptionPane.YES_OPTION ? true : false);
	}
	
	/**
	 * Show a error message to the user
	 * @param parent The parent component for dialogs.
	 * @param title The title for dialogs.
	 * @param err The error to show the user.
	 */
	public static void showError(Component parent, String title,
			String err) {
		JOptionPane.showMessageDialog(parent, err, title,
				JOptionPane.ERROR_MESSAGE);
	}
}
