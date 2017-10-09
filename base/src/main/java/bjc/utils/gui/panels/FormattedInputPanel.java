package bjc.utils.gui.panels;

import java.util.function.Consumer;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bjc.utils.gui.layout.HLayout;

/**
 * A simple panel allowing for input of a single formatted value
 *
 * @author ben
 *
 * @param <InputVal>
 *                The type of value being formatted
 */
public class FormattedInputPanel<InputVal> extends JPanel {
	private static final long serialVersionUID = 5232016563558588031L;

	private final JFormattedTextField field;

	/**
	 * Create a new formatted input panel
	 *
	 * @param label
	 *                The label for this panel
	 * @param length
	 *                The length of this panel
	 * @param formatter
	 *                The formatter to use for input
	 * @param reciever
	 *                The action to call whenever the value changes
	 */
	@SuppressWarnings("unchecked")
	public FormattedInputPanel(final String label, final int length, final AbstractFormatter formatter,
			final Consumer<InputVal> reciever) {
		setLayout(new HLayout(2));

		final JLabel lab = new JLabel(label);
		field = new JFormattedTextField(formatter);

		field.setColumns(length);
		field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		field.addPropertyChangeListener("value", (event) -> {
			// This is safe, because InputVal should be the type of
			// whatever object the formatter is returning
			reciever.accept((InputVal) field.getValue());
		});

		add(lab);
		add(field);
	}

	/**
	 * Reset the value in this panel to a specified value
	 *
	 * @param value
	 *                The value to set the panel to
	 */
	public void resetValues(final InputVal value) {
		field.setValue(value);
	}
}
