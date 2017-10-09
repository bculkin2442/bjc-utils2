package bjc.utils.gui.layout;

import java.awt.GridLayout;

/**
 * A layout that simply holds one component that it auto-resizes whenever it is
 * resized.
 *
 * @author ben
 *
 */
public class AutosizeLayout extends GridLayout {
	// Version id for serialization
	private static final long serialVersionUID = -2495693595953396924L;

	/**
	 * Create a new auto-size layout.
	 */
	public AutosizeLayout() {
		super(1, 1);
	}
}
