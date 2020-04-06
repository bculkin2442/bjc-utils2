package bjc.utils.gui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.KeyStroke;

/**
 * Simple {@link JButton} that provides an easy way to add a key-map to it.
 * 
 * @author Ben Culkin
 *
 */
public class SimpleKeyedButton extends JButton {
	private final class KeyedButtonAction extends AbstractAction {
		private static final long serialVersionUID = 5854121457339741391L;

		private final Consumer<ActionEvent> aevListener;

		private KeyedButtonAction(String name, Consumer<ActionEvent> aevListener) {
			super(name);
			this.aevListener = aevListener;
		}

		@Override
		public void actionPerformed(ActionEvent aev) {
			aevListener.accept(aev);
		}
	}

	private static final long serialVersionUID = -8550504153221678178L;
	
	private String label;

	/**
	 * Create a new keyed button.
	 * 
	 * @param label The label for the button.
	 */
	public SimpleKeyedButton(String label) {
		super(label);
		
		this.label = label;
	}
	
	/**
	 * Sets the default action for this button, and installs a global (WHEN_IN_FOCUSED_WINDOW) keystroke handler for it.
	 * 
	 * @param eventName An unique internal name for the event.
	 * @param keystroke The keystroke for this event, passed to {@link KeyStroke#getKeyStroke(String)}.
	 * @param aevListener The listener that handles the implementation of the action.
	 */
	public void setGlobalDefaultKeystroke(String eventName, String keystroke, Consumer<ActionEvent> aevListener) {
		Action act = new KeyedButtonAction(eventName, aevListener);
		KeyStroke stroke = KeyStroke.getKeyStroke(keystroke);
		
		this.setAction(act);
		this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(stroke, eventName);
		this.getActionMap().put(eventName, act);
		this.setText(label);
	}
	
	/**
	 * Installs a global (WHEN_IN_FOCUSED_WINDOW) keystroke handler for an action.
	 * 
	 * @param eventName An unique internal name for the event.
	 * @param keystroke The keystroke for this event, passed to {@link KeyStroke#getKeyStroke(String)}.
	 * @param aevListener The listener that handles the implementation of the action.
	 */
	public void addGlobalKeystroke(String eventName, String keystroke, Consumer<ActionEvent> aevListener) {
		Action act = new KeyedButtonAction(eventName, aevListener);
		KeyStroke stroke = KeyStroke.getKeyStroke(keystroke);
		
		this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(stroke, eventName);
		this.getActionMap().put(eventName, act);
		this.setText(label);
	}
}
