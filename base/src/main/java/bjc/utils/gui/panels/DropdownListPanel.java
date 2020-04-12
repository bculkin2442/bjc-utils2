package bjc.utils.gui.panels;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bjc.funcdata.IList;
import bjc.utils.gui.layout.AutosizeLayout;
import bjc.utils.gui.layout.HLayout;

/**
 * A panel that allows you to select choices from a dropdown list
 *
 * @author ben
 *
 */
public class DropdownListPanel extends JPanel {
	private static final long serialVersionUID = 2719963952350133541L;

	/**
	 * Create a new dropdown list panel
	 *
	 * @param <T>
	 *        The type of items in the dropdown list
	 * @param type
	 *        The label of the type of items in the list
	 * @param model
	 *        The model to put items into
	 * @param choices
	 *        The items to choose from
	 */
	public <T> DropdownListPanel(final String type, final DefaultListModel<T> model, final IList<T> choices) {
		setLayout(new AutosizeLayout());

		final JPanel itemInputPanel = new JPanel();
		itemInputPanel.setLayout(new BorderLayout());

		final JPanel addItemPanel = new JPanel();
		addItemPanel.setLayout(new HLayout(2));

		final JComboBox<T> addItemBox = new JComboBox<>();
		choices.forEach(addItemBox::addItem);

		final JButton addItemButton = new JButton("Add " + type);

		addItemPanel.add(addItemBox);
		addItemPanel.add(addItemButton);

		final JList<T> itemList = new JList<>(model);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JButton removeItemButton = new JButton("Remove " + type);

		addItemButton.addActionListener((ev) -> {
			model.addElement(addItemBox.getItemAt(addItemBox.getSelectedIndex()));
		});

		removeItemButton.addActionListener((ev) -> {
			model.remove(itemList.getSelectedIndex());
		});

		itemInputPanel.add(addItemPanel, BorderLayout.PAGE_START);
		itemInputPanel.add(itemList, BorderLayout.CENTER);
		itemInputPanel.add(removeItemButton, BorderLayout.PAGE_END);

		add(itemInputPanel);
	}
}
