package bjc.utils.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bjc.utils.funcdata.FunctionalList;
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
			FunctionalList<E> defaultValues) {
		setLayout(new VLayout(2));

		JList<E> list;

		if (defaultValues != null) {
			list = SimpleJList.buildFromList(defaultValues.toIterable());
		} else {
			list = new JList<>(new DefaultListModel<>());
		}

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new HLayout(3));

		JButton addParam = new JButton("Add...");
		JButton editParam = new JButton("Edit...");
		JButton removeParam = new JButton("Remove...");

		addParam.addActionListener(
				(event) -> ((DefaultListModel<E>) list.getModel())
						.addElement(addAction.get()));
		editParam.addActionListener(
				(event) -> editAction.accept(list.getSelectedValue()));
		removeParam.addActionListener((event) -> removeAction
				.accept(((DefaultListModel<E>) list.getModel())
						.remove(list.getSelectedIndex())));

		buttonPanel.add(addParam);
		buttonPanel.add(editParam);
		buttonPanel.add(removeParam);

		add(list);
		add(buttonPanel);
	}
}
