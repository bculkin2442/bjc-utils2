package bjc.utils.gui;

import java.awt.Component;
import java.awt.Frame;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bjc.utils.gui.layout.VLayout;

/**
 * Utility class for getting simple input from the user.
 *
 * @author ben
 *
 */
public class SimpleDialogs {
	/**
	 * Get a bounded integer from the user.
	 *
	 * @param parent
	 *                The parent component for the dialogs.
	 * @param title
	 *                The title for the dialogs.
	 * @param prompt
	 *                The prompt to tell the user what to enter.
	 * @param lowerBound
	 *                The lower integer bound to accept.
	 * @param upperBound
	 *                The upper integer bound to accept.
	 * @return A int within the specified bounds.
	 */
	public static int getBoundedInt(final Component parent, final String title, final String prompt,
			final int lowerBound, final int upperBound) {
		return getValue(parent, title, prompt, (strang) -> {
			try {
				final int value = Integer.parseInt(strang);

				return value < upperBound && value > lowerBound;
			} catch (final NumberFormatException nfex) {
				// We don't care about the specifics of the
				// exception, just
				// that this value isn't good
				return false;
			}
		}, Integer::parseInt);
	}

	/**
	 * Asks the user to pick an option from a series of choices.
	 *
	 * @param <E>
	 *                The type of choices for the user to pick
	 *
	 * @param parent
	 *                The parent frame for this dialog
	 * @param title
	 *                The title of this dialog
	 * @param question
	 *                The question being asked
	 * @param choices
	 *                The available choices for the question
	 * @return The choice the user picked, or null if they didn't pick one
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getChoice(final Frame parent, final String title, final String question,
			final E... choices) {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (title == null)
			throw new NullPointerException("Title must not be null");
		else if (question == null) throw new NullPointerException("Question must not be null");

		final JDialog chooser = new JDialog(parent, title, true);
		chooser.setLayout(new VLayout(2));

		final JPanel questionPane = new JPanel();

		final JLabel questionText = new JLabel(question);
		final JComboBox<E> questionChoices = new JComboBox<>(choices);

		questionPane.add(questionText);
		questionPane.add(questionChoices);

		final JPanel buttonPane = new JPanel();

		final JButton okButton = new JButton("Ok");
		final JButton cancelButton = new JButton("Cancel");

		okButton.addActionListener((event) -> chooser.dispose());
		cancelButton.addActionListener((event) -> chooser.dispose());

		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		chooser.add(questionPane);
		chooser.add(buttonPane);

		chooser.pack();
		chooser.setVisible(true);

		return (E) questionChoices.getSelectedItem();
	}

	/**
	 * Get a integer from the user
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for dialogs.
	 * @param prompt
	 *                The prompt to tell the user what to enter.
	 * @return A int.
	 */
	public static int getInt(final Component parent, final String title, final String prompt) {
		return getValue(parent, title, prompt, strang -> {
			try {
				Integer.parseInt(strang);
				return true;
			} catch (final NumberFormatException nfex) {
				// We don't care about this exception, just mark
				// the value
				// as not good
				return false;
			}
		}, Integer::parseInt);
	}

	/**
	 * Get a string from the user
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for the dialogs.
	 * @param prompt
	 *                The prompt to tell the user what to enter.
	 * @return A string.
	 */
	public static String getString(final Component parent, final String title, final String prompt) {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (title == null)
			throw new NullPointerException("Title must not be null");
		else if (prompt == null) throw new NullPointerException("Prompt must not be null");

		return JOptionPane.showInputDialog(parent, prompt, title, JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Get a value parsable from a string from the user.
	 *
	 * @param <E>
	 *                The type of the value parsed from the string
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for dialogs.
	 * @param prompt
	 *                The prompt to tell the user what to enter.
	 * @param validator
	 *                A predicate to determine if a input is valid.
	 * @param transformer
	 *                The function to transform the string into a value.
	 * @return The value parsed from a string.
	 */
	public static <E> E getValue(final Component parent, final String title, final String prompt,
			final Predicate<String> validator, final Function<String, E> transformer) {
		if (validator == null)
			throw new NullPointerException("Validator must not be null");
		else if (transformer == null) throw new NullPointerException("Transformer must not be null");

		String input = getString(parent, title, prompt);

		while (!validator.test(input)) {
			showError(parent, "I/O Error", "Please enter a valid value");

			input = getString(parent, title, prompt);
		}

		return transformer.apply(input);
	}

	/**
	 * Get a whole number from the user.
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for dialogs.
	 * @param prompt
	 *                The prompt to tell the user what to enter.
	 * @return A whole number.
	 */
	public static int getWhole(final Component parent, final String title, final String prompt) {
		return getBoundedInt(parent, title, prompt, 0, Integer.MAX_VALUE);
	}

	/**
	 * Ask the user a Yes/No question.
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for dialogs.
	 * @param question
	 *                The question to ask the user.
	 * @return True if the user said yes, false otherwise.
	 */
	public static boolean getYesNo(final Component parent, final String title, final String question) {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (title == null)
			throw new NullPointerException("Title must not be null");
		else if (question == null) throw new NullPointerException("Question must not be null");

		final int result = JOptionPane.showConfirmDialog(parent, question, title, JOptionPane.YES_NO_OPTION);

		return result == JOptionPane.YES_OPTION ? true : false;
	}

	/**
	 * Show a error message to the user
	 *
	 * @param parent
	 *                The parent component for dialogs.
	 * @param title
	 *                The title for dialogs.
	 * @param message
	 *                The error to show the user.
	 */
	public static void showError(final Component parent, final String title, final String message) {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (title == null)
			throw new NullPointerException("Title must not be null");
		else if (message == null) throw new NullPointerException("Error message must not be null");

		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show an informative message to the user
	 *
	 * @param parent
	 *                The parent for this dialog
	 * @param title
	 *                Show the title for this dialog
	 * @param message
	 *                Show the message for this dialog
	 */
	public static void showMessage(final Component parent, final String title, final String message) {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (title == null)
			throw new NullPointerException("Title must not be null");
		else if (message == null) throw new NullPointerException("Message must not be null");

		JOptionPane.showMessageDialog(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
	}
}
