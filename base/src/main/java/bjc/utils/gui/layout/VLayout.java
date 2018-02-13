package bjc.utils.gui.layout;

import java.awt.GridLayout;

/**
 * A layout that lays out its components vertically, evenly sharing space among
 * them.
 *
 * @author ben
 *
 */
public class VLayout extends GridLayout {
	// Version ID for serializations
	private static final long serialVersionUID = -6417962941602322663L;

	/**
	 * Create a new vertical layout with the specified number of rows.
	 *
	 * @param rows
	 *        The number of rows.
	 */
	public VLayout(final int rows) {
		super(rows, 1);
	}
}
