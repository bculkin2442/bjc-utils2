package bjc.utils.gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * Utility class for making JLists and their models.
 * 
 * @author ben
 *
 */
public class SimpleJList {
	/**
	 * Create a new JList from a given list.
	 * 
	 * @param <E>
	 *            The type of data in the JList
	 * 
	 * @param listSource
	 *            The list to populate the JList with.
	 * @return A JList populated with the elements from ls.
	 */
	public static <E> JList<E> buildFromList(Iterable<E> listSource) {
		if (listSource == null) {
			throw new NullPointerException("Source must not be null");
		}

		return new JList<>(buildModel(listSource));
	}

	/**
	 * Create a new list model from a given list.
	 * 
	 * @param <E>
	 *            The type of data in the list model
	 * 
	 * @param listSource
	 *            The list to fill the list model from.
	 * @return A list model populated with the elements from ls.
	 */
	public static <E> ListModel<E> buildModel(Iterable<E> listSource) {
		if (listSource == null) {
			throw new NullPointerException("Source must not be null");
		}

		DefaultListModel<E> defaultModel = new DefaultListModel<>();

		listSource.forEach(defaultModel::addElement);

		return defaultModel;
	}
}
