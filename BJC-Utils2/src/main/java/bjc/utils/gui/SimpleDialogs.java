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
	 *            The parent component for the dialogs.
	 * @param title
	 *            The title for the dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @param lower
	 *            The lower integer bound to accept.
	 * @param upper
	 *            The upper integer bound to accept.
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
	 * Asks the user to pick an option from a series of choices.
	 * 
	 * @param parent
	 *            The parent frame for this dialog
	 * @param title
	 *            The title of this dialog
	 * @param question
	 *            The question being asked
	 * @param choices
	 *            The available choices for the question
	 * @return The choice the user picked, or null if they didn't pick one
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getChoice(Frame parent, String title,
			String question, E... choices) {
		JDialog jd = new JDialog(parent, title, true);
		jd.setLayout(new VLayout(2));

		JPanel questionPane = new JPanel();

		JLabel questionText = new JLabel(question);
		JComboBox<E> questionChoices = new JComboBox<>(choices);

		questionPane.add(questionText);
		questionPane.add(questionChoices);

		JPanel buttonPane = new JPanel();

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(e -> jd.dispose());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> jd.dispose());

		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		jd.add(questionPane);
		jd.add(buttonPane);

		jd.pack();
		jd.setVisible(true);

		return (E) questionChoices.getSelectedItem();
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
		return JOptionPane.showInputDialog(parent, prompt, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Get a value parsable from a string from the user.
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param prompt
	 *            The prompt to tell the user what to enter.
	 * @param p
	 *            A predicate to determine if a input is valid.
	 * @param f
	 *            The function to transform the string into a value.
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
		int res = JOptionPane.showConfirmDialog(parent, question, title,
				JOptionPane.YES_NO_OPTION);

		return (res == JOptionPane.YES_OPTION ? true : false);
	}

	/**
	 * Show a error message to the user
	 * 
	 * @param parent
	 *            The parent component for dialogs.
	 * @param title
	 *            The title for dialogs.
	 * @param err
	 *            The error to show the user.
	 */
	public static void showError(Component parent, String title,
			String err) {
		JOptionPane.showMessageDialog(parent, err, title,
				JOptionPane.ERROR_MESSAGE);
	}
}
