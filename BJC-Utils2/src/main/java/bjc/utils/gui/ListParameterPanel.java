package bjc.utils.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bjc.utils.funcdata.IList;
import bjc.utils.gui.layout.HLayout;
import bjc.utils.gui.layout.VLayout;

/**
 * A panel that has a list of objects and ways of manipulating that list
 * 
 * @author ben
 *
 * @param <E>
 *            The type of data stored in the list
 */
public class ListParameterPanel<E> extends JPanel {
	// Version id for serialization
	private static final long serialVersionUID = 3442971104975491571L;

	/**
	 * Create a new panel using the specified actions for doing things
	 * 
	 * @param addAction
	 *            The action that provides items
	 * @param editAction
	 *            The action that edits items
	 * @param removeAction
	 *            The action that removes items
	 */
	public ListParameterPanel(Supplier<E> addAction,
			Consumer<E> editAction, Consumer<E> removeAction) {
		this(addAction, editAction, removeAction, null);
	}

	/**
	 * Create a new panel using the specified actions for doing things
	 * 
	 * @param addAction
	 *            The action that provides items
	 * @param editAction
	 *            The action that edits items
	 * @param removeAction
	 *            The action that removes items
	 * @param defaultValues
	 *            The default values to put in the list
	 */
	public ListParameterPanel(Supplier<E> addAction,
			Consumer<E> editAction, Consumer<E> removeAction,
			IList<E> defaultValues) {
		setLayout(new VLayout(2));

		JList<E> list;

		if (defaultValues != null) {
			list = SimpleJList.buildFromList(defaultValues.toIterable());
		} else {
			list = new JList<>(new DefaultListModel<>());
		}

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel buttonPanel = new JPanel();

		int numButtons = 0;

		if (addAction != null) {
			numButtons++;
		}

		if (editAction != null) {
			numButtons++;
		}

		if (removeAction != null) {
			numButtons++;
		}

		buttonPanel.setLayout(new HLayout(numButtons));

		JButton addParam = null;

		if (addAction != null) {
			addParam = new JButton("Add...");
			addParam.addActionListener((event) -> {
				DefaultListModel<E> model = (DefaultListModel<E>) list
						.getModel();

				model.addElement(addAction.get());
			});
		}

		JButton editParam = null;

		if (editAction != null) {
			editParam = new JButton("Edit...");
			editParam.addActionListener((event) -> {
				editAction.accept(list.getSelectedValue());
			});
		}

		JButton removeParam = null;

		if (removeAction != null) {
			removeParam = new JButton("Remove...");
			removeParam.addActionListener((event) -> {
				DefaultListModel<E> model = (DefaultListModel<E>) list
						.getModel();

				removeAction.accept(model.remove(list.getSelectedIndex()));
			});
		}

		if (addAction != null) {
			buttonPanel.add(addParam);
		}

		if (editAction != null) {
			buttonPanel.add(editParam);
		}

		if (removeAction != null) {
			buttonPanel.add(removeParam);
		}

		add(list);
		add(buttonPanel);
	}
}
