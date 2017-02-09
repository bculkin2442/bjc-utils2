package bjc.utils.gui.panels;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bjc.utils.funcdata.IList;
import bjc.utils.gui.SimpleJList;
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
	 * @param add
	 *            The action that provides items
	 * @param edit
	 *            The action that edits items
	 * @param remove
	 *            The action that removes items
	 */
	public ListParameterPanel(Supplier<E> add,
			Consumer<E> edit, Consumer<E> remove) {
		this(add, edit, remove, null);
	}

	/**
	 * Create a new panel using the specified actions for doing things
	 * 
	 * @param add
	 *            The action that provides items
	 * @param edit
	 *            The action that edits items
	 * @param remove
	 *            The action that removes items
	 * @param defaults
	 *            The default values to put in the list
	 */
	public ListParameterPanel(Supplier<E> add,
			Consumer<E> edit, Consumer<E> remove,
			IList<E> defaults) {
		setLayout(new VLayout(2));

		JList<E> list;

		if (defaults != null) {
			list = SimpleJList.buildFromList(defaults.toIterable());
		} else {
			list = new JList<>(new DefaultListModel<>());
		}

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel buttonPanel = new JPanel();

		int numButtons = 0;

		if (add != null) {
			numButtons++;
		}

		if (edit != null) {
			numButtons++;
		}

		if (remove != null) {
			numButtons++;
		}

		buttonPanel.setLayout(new HLayout(numButtons));

		JButton addParam = null;

		if (add != null) {
			addParam = new JButton("Add...");
			addParam.addActionListener((event) -> {
				DefaultListModel<
						E> model = (DefaultListModel<E>) list.getModel();

				model.addElement(add.get());
			});
		}

		JButton editParam = null;

		if (edit != null) {
			editParam = new JButton("Edit...");
			editParam.addActionListener((event) -> {
				edit.accept(list.getSelectedValue());
			});
		}

		JButton removeParam = null;

		if (remove != null) {
			removeParam = new JButton("Remove...");
			removeParam.addActionListener((event) -> {
				DefaultListModel<
						E> model = (DefaultListModel<E>) list.getModel();

				remove.accept(model.remove(list.getSelectedIndex()));
			});
		}

		if (add != null) {
			buttonPanel.add(addParam);
		}

		if (edit != null) {
			buttonPanel.add(editParam);
		}

		if (remove != null) {
			buttonPanel.add(removeParam);
		}

		add(list);
		add(buttonPanel);
	}
}
