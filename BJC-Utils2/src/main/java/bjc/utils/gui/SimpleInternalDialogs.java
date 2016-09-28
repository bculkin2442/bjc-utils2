package bjc.utils.gui;

import java.awt.Component;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JOptionPane;

/**
 * Utility class for getting simple input from the user. Modified to work
 * with JDesktopPanes
 * 
 * @author ben
 *
 */
public class SimpleInternalDialogs {
	/**
	 * Get a bounded integer from the user.
	 * 
	 * @param parent
	 *            The parent component for the dialogs.
	 * @param title
	 *            The title for the dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @param lowerBound
	 *            The lower integer bound to accept.
	 * @param upperBound
	 *            The upper integer bound to accept.
	 * @return A int within the specified bounds.
	 */
	public static int getBoundedInt(Component parent, String title,
			String prompt, int lowerBound, int upperBound) {
		return getValue(parent, title, prompt, (strang) -> {
			try {
				int value = Integer.parseInt(strang);

				return (value < upperBound) && (value > lowerBound);
			} catch (NumberFormatException nfex) {
				// We don't care about the specifics of the exception, just
				// that this value isn't good
				return false;
			}
		}, Integer::parseInt);
	}

	/**
	 * Get a integer from the user
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @return A int.
	 */
	public static int getInt(Component parent, String title,
			String prompt) {
		return getValue(parent, title, prompt, strang -> {
			try {
				Integer.parseInt(strang);
				return true;
			} catch (NumberFormatException nfex) {
				// We don't care about this exception, just mark the value
				// as not good
				return false;
			}
		}, Integer::parseInt);
	}

	/**
	 * Get a string from the user
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for the dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @return A string.
	 */
	public static String getString(Component parent, String title,
			String prompt) {
		if (parent == null) {
			throw new NullPointerException("Parent must not be null");
		} else if (title == null) {
			throw new NullPointerException("Title must not be null");
		} else if (prompt == null) {
			throw new NullPointerException("Prompt must not be null");
		}

		return JOptionPane.showInternalInputDialog(parent, prompt, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Get a value parsable from a string from the user.
	 * 
	 * @param <E>
	 *            The type of the value parsed from the string
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @param inputValidator
	 *            A predicate to determine if a input is valid.
	 * @param inputTransformer
	 *            The function to transform the string into a value.
	 * @return The value parsed from a string.
	 */
	public static <E> E getValue(Component parent, String title,
			String prompt, Predicate<String> inputValidator,
			Function<String, E> inputTransformer) {
		if (inputValidator == null) {
			throw new NullPointerException("Validator must not be null");
		} else if (inputTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		String inputString = getString(parent, title, prompt);

		while (!inputValidator.test(inputString)) {
			showError(parent, "I/O Error", "Please enter a valid value");

			inputString = getString(parent, title, prompt);
		}

		return inputTransformer.apply(inputString);
	}

	/**
	 * Get a whole number from the user.
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @return A whole number.
	 */
	public static int getWhole(Component parent, String title,
			String prompt) {
		return getBoundedInt(parent, title, prompt, 0, Integer.MAX_VALUE);
	}

	/**
	 * Ask the user a Yes/No question.
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param question
	 *            The question to ask the user.
	 * @return True if the user said yes, false otherwise.
	 */
	public static boolean getYesNo(Component parent, String title,
			String question) {
		if (parent == null) {
			throw new NullPointerException("Parent must not be null");
		} else if (title == null) {
			throw new NullPointerException("Title must not be null");
		} else if (question == null) {
			throw new NullPointerException("Question must not be null");
		}

		int dialogResult = JOptionPane.showInternalConfirmDialog(parent, question,
				title, JOptionPane.YES_NO_OPTION);

		return (dialogResult == JOptionPane.YES_OPTION ? true : false);
	}

	/**
	 * Show a error message to the user
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param errorMessage
	 *            The error to show the user.
	 */
	public static void showError(Component parent, String title,
			String errorMessage) {
		if (parent == null) {
			throw new NullPointerException("Parent must not be null");
		} else if (title == null) {
			throw new NullPointerException("Title must not be null");
		} else if (errorMessage == null) {
			throw new NullPointerException(
					"Error message must not be null");
		}

		JOptionPane.showInternalMessageDialog(parent, errorMessage, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show an informative message to the user
	 * 
	 * @param parent
	 *            The parent for this dialog
	 * @param title
	 *            Show the title for this dialog
	 * @param message
	 *            Show the message for this dialog
	 */
	public static void showMessage(Component parent, String title,
			String message) {
		if (parent == null) {
			throw new NullPointerException("Parent must not be null");
		} else if (title == null) {
			throw new NullPointerException("Title must not be null");
		} else if (message == null) {
			throw new NullPointerException("Message must not be null");
		}

		JOptionPane.showInternalMessageDialog(parent, title, message,
				JOptionPane.INFORMATION_MESSAGE);
	}
}