package bjc.utils.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bjc.utils.gui.layout.HLayout;

public class SimpleInputPanel extends JPanel {
	private static final long	serialVersionUID	= -4734279623645236868L;

	public final JTextField		inputValue;

	public SimpleInputPanel(String label, int columns) {
		setLayout(new HLayout(2));

		JLabel inputLabel = new JLabel(label);

		if (columns < 1) {
			inputValue = new JTextField();
		} else {
			inputValue = new JTextField(columns);
		}

		add(inputLabel);
		add(inputValue);
	}
}
