package bjc.utils.gui.panels;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import bjc.utils.gui.layout.AutosizeLayout;
import bjc.utils.gui.layout.HLayout;

public class SimpleListPanel extends JPanel {
	private static final long serialVersionUID = 2719963952350133541L;

	public SimpleListPanel(String itemType,
			DefaultListModel<String> listModel,
			Predicate<String> itemVerifier,
			Consumer<String> onVerificationFailure) {
		setLayout(new AutosizeLayout());

		JPanel itemInputPanel = new JPanel();
		itemInputPanel.setLayout(new BorderLayout());

		JPanel addItemPanel = new JPanel();
		addItemPanel.setLayout(new HLayout(2));

		JTextField addItemField = new JTextField(255);
		JButton addItemButton = new JButton("Add " + itemType);

		addItemPanel.add(addItemField);
		addItemPanel.add(addItemButton);

		JList<String> itemList = new JList<>(listModel);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(itemList);

		JButton removeItemButton = new JButton("Remove " + itemType);

		addItemButton.addActionListener((ev) -> {
			addItem(listModel, itemVerifier, onVerificationFailure,
					addItemField);
		});

		addItemField.addActionListener((ev) -> {
			addItem(listModel, itemVerifier, onVerificationFailure,
					addItemField);
		});

		removeItemButton.addActionListener((ev) -> {
			listModel.remove(itemList.getSelectedIndex());
		});

		itemInputPanel.add(addItemPanel, BorderLayout.PAGE_START);
		itemInputPanel.add(listScroller, BorderLayout.CENTER);
		itemInputPanel.add(removeItemButton, BorderLayout.PAGE_END);

		add(itemInputPanel);
	}

	private static void addItem(DefaultListModel<String> listModel,
			Predicate<String> itemVerifier,
			Consumer<String> onVerificationFailure,
			JTextField addItemField) {
		String potentialItem = addItemField.getText();

		if (itemVerifier == null || itemVerifier.test(potentialItem)) {
			listModel.addElement(potentialItem);
		} else {
			onVerificationFailure.accept(potentialItem);
		}

		addItemField.setText("");
	}
}
