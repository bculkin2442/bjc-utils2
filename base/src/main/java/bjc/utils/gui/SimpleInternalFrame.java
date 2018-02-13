package bjc.utils.gui;

import javax.swing.JInternalFrame;

/**
 * A simple internal frame class
 *
 * @author ben
 *
 */
public class SimpleInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = -2966801321260716617L;

	/**
	 * Create a new blank internal frame
	 */
	public SimpleInternalFrame() {
		super();
	}

	/**
	 * Create a new blank internal frame with a specific title
	 *
	 * @param title
	 *        The title of the internal frame
	 */
	public SimpleInternalFrame(final String title) {
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