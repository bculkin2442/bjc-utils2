package bjc.utils.gui.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A simple component for text input
 *
 * @author ben
 *
 */
public class SimpleInputPanel extends JPanel {
	private static final long serialVersionUID = -4734279623645236868L;

	/**
	 * The text field containing the input value
	 */
	public final JTextField inputValue;

	/**
	 * Create a new input panel
	 *
	 * @param label
	 *                The label for the field
	 * @param columns
	 *                The number of columns of text input to take
	 */
	public SimpleInputPanel(String label, int columns) {
		setLayout(new BorderLayout());

		JLabel inputLabel = new JLabel(label);

		if(columns < 1) {
			inputValue = new JTextField();
		} else {
			inputValue = new JTextField(columns);
		}

		add(inputLabel, BorderLayout.LINE_START);
		add(inputValue, BorderLayout.CENTER);
	}
}
