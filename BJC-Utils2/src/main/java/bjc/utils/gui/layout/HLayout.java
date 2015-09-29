package bjc.utils.gui.layout;

import java.awt.GridLayout;

/**
 * A layout manager that lays out its components horizontally, evenly sizing them.
 * @author ben
 *
 */
public class HLayout extends GridLayout {
	private static final long serialVersionUID = 1244964456966270026L;

	/**
	 * Create a new horizontal layout with the specified number of columns.
	 * @param cols The number of columns in this layout.
	 */
	public HLayout(int cols) {
		super(1, cols);
	}
}
