package bjc.utils.gui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * Utility class for making JLists and their models.
 * @author ben
 *
 */
public class SimpleJList {
	/**
	 * Create a new JList from a given list.
	 * @param ls The list to populate the JList with.
	 * @return A JList populated with the elements from ls.
	 */
	public static <E> JList<E> buildFromList(List<E> ls) {
		return new JList<E>(buildModel(ls));
	}

	/**
	 * Create a new list model from a given list.
	 * @param ls The list to fill the list model from.
	 * @return A list model populated with the elements from ls.
	 */
	public static <E> ListModel<E> buildModel(List<E> ls) {
		DefaultListModel<E> dlm = new DefaultListModel<>();

		ls.forEach(dlm::addElement);
		
		return dlm;
	}
}
