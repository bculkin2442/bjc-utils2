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

public class DropdownListPanel extends JPanel {
	private static final long serialVersionUID = 2719963952350133541L;

	@SuppressWarnings("unchecked")
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
			listModel.addElement((T) addItemBox.getSelectedItem());
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
