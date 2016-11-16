package bjc.utils.gui.panels;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bjc.utils.funcdata.IList;
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
	 *            The type of items in the dropdown list
	 * @param itemType
	 *            The label of the type of items in the list
	 * @param listModel
	 *            The model to put items into
	 * @param choices
	 *            The items to choose from
	 */
	public <T> DropdownListPanel(String itemType,
			DefaultListModel<T> listModel, IList<T> choices) {
		setLayout(new AutosizeLayout());

		JPanel itemInputPanel = new JPanel();
		itemInputPanel.setLayout(new BorderLayout());

		JPanel addItemPanel = new JPanel();
		addItemPanel.setLayout(new HLayout(2));

		JComboBox<T> addItemBox = new JComboBox<>();
		choices.forEach(addItemBox::addItem);

		JButton addItemButton = new JButton("Add " + itemType);

		addItemPanel.add(addItemBox);
		addItemPanel.add(addItemButton);

		JList<T> itemList = new JList<>(listModel);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JButton removeItemButton = new JButton("Remove " + itemType);

		addItemButton.addActionListener((ev) -> {
			listModel.addElement(
					addItemBox.getItemAt(addItemBox.getSelectedIndex()));
		});

		removeItemButton.addActionListener((ev) -> {
			listModel.remove(itemList.getSelectedIndex());
		});

		itemInputPanel.add(addItemPanel, BorderLayout.PAGE_START);
		itemInputPanel.add(itemList, BorderLayout.CENTER);
		itemInputPanel.add(removeItemButton, BorderLayout.PAGE_END);

		add(itemInputPanel);
	}
}