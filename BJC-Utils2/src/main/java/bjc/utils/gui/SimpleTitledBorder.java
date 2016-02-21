package bjc.utils.gui;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * A simple border with a title attached to it.
 * 
 * @author ben
 *
 */
public class SimpleTitledBorder extends TitledBorder {
	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = -5655969079949148487L;

	/**
	 * Create a new border with the specified title.
	 * 
	 * @param title
	 *            The title for the border.
	 */
	public SimpleTitledBorder(String title) {
		super(new EtchedBorder(), title);
	}

}
