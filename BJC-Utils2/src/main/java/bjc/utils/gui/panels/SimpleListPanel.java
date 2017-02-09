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

/**
 * A simple list of strings
 * 
 * @author ben
 *
 */
public class SimpleListPanel extends JPanel {
	private static final long serialVersionUID = 2719963952350133541L;

	private static void addItem(DefaultListModel<String> model,
			Predicate<String> verifier,
			Consumer<String> onFailure,
			JTextField addItemField) {
		String potentialItem = addItemField.getText();

		if (verifier == null || verifier.test(potentialItem)) {
			model.addElement(potentialItem);
		} else {
			onFailure.accept(potentialItem);
		}

		addItemField.setText("");
	}

	/**
	 * Create a new list panel
	 * 
	 * @param type
	 *            The type of things in the list
	 * @param model
	 *            The model to put items into
	 * @param verifier
	 *            The predicate to use to verify items
	 * @param onFailure
	 *            The function to call when an item doesn't verify
	 */
	public SimpleListPanel(String type,
			DefaultListModel<String> model,
			Predicate<String> verifier,
			Consumer<String> onFailure) {
		setLayout(new AutosizeLayout());

		JPanel itemInputPanel = new JPanel();
		itemInputPanel.setLayout(new BorderLayout());

		JPanel addItemPanel = new JPanel();
		addItemPanel.setLayout(new HLayout(2));

		JTextField addItemField = new JTextField(255);
		JButton addItemButton = new JButton("Add " + type);

		addItemPanel.add(addItemField);
		addItemPanel.add(addItemButton);

		JList<String> itemList = new JList<>(model);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(itemList);

		JButton removeItemButton = new JButton("Remove " + type);

		addItemButton.addActionListener((ev) -> {
			addItem(model, verifier, onFailure,
					addItemField);
		});

		addItemField.addActionListener((ev) -> {
			addItem(model, verifier, onFailure,
					addItemField);
		});

		removeItemButton.addActionListener((ev) -> {
			model.remove(itemList.getSelectedIndex());
		});

		itemInputPanel.add(addItemPanel, BorderLayout.PAGE_START);
		itemInputPanel.add(listScroller, BorderLayout.CENTER);
		itemInputPanel.add(removeItemButton, BorderLayout.PAGE_END);

		add(itemInputPanel);
	}
}
