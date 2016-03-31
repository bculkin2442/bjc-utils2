package bjc.utils.gui.layout;

import java.awt.GridLayout;

/**
 * A layout manager that lays out its components horizontally, evenly
 * sizing them.
 * 
 * @author ben
 *
 */
public class HLayout extends GridLayout {
	// Version ID for serialization
	private static final long serialVersionUID = 1244964456966270026L;

	/**
	 * Create a new horizontal layout with the specified number of columns.
	 * 
	 * @param columns
	 *            The number of columns in this layout.
	 */
	public HLayout(int columns) {
		super(1, columns);
	}
}
