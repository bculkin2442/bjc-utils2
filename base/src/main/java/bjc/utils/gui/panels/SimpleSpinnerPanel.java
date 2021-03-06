package bjc.utils.gui.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

/**
 * A simple spinner control alongside a label
 *
 * @author ben
 *
 */
public class SimpleSpinnerPanel extends JPanel {
	private static final long serialVersionUID = -4734279623645236868L;

	/**
	 * The spinner being used
	 */
	public final JSpinner inputValue;

	/**
	 * Create a new spinner panel
	 *
	 * @param label
	 *              The label for the spinner
	 * @param model
	 *              The model to attach to the spinner
	 */
	public SimpleSpinnerPanel(final String label, final SpinnerModel model) {
		setLayout(new BorderLayout());

		final JLabel inputLabel = new JLabel(label);

		inputValue = new JSpinner(model);

		add(inputLabel, BorderLayout.LINE_START);
		add(inputValue, BorderLayout.CENTER);
	}
}
