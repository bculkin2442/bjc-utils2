package bjc.utils.gui.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimpleInputPanel extends JPanel {
	private static final long	serialVersionUID	= -4734279623645236868L;

	public final JTextField		inputValue;

	public SimpleInputPanel(String label, int columns) {
		setLayout(new BorderLayout());

		JLabel inputLabel = new JLabel(label);

		if (columns < 1) {
			inputValue = new JTextField();
		} else {
			inputValue = new JTextField(columns);
		}

		add(inputLabel, BorderLayout.LINE_START);
		add(inputValue, BorderLayout.CENTER);
	}
}
