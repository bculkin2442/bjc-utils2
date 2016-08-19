package bjc.utils.gui;

import javax.swing.JInternalFrame;

public class SimpleInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = -2966801321260716617L;

	public SimpleInternalFrame() {
		super();
	}

	public SimpleInternalFrame(String title) {
		super(title);
	}

	protected void setupFrame() {
		setSize(320, 240);

		setResizable(true);

		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
	}

}