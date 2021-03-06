package bjc.utils.gui.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import bjc.data.Holder;
import bjc.utils.gui.layout.HLayout;

/**
 * A panel that outputs a value bound to a {@link Holder}
 *
 * @author ben
 *
 */
public class HolderOutputPanel extends JPanel {
	private static final long serialVersionUID = 166573313903782080L;

	private Timer updater;
	private final JLabel value;
	private final int nDelay;
	private final Holder<String> val;

	/**
	 * Create a new display panel, backed by a holder
	 *
	 * @param lab
	 *                    The label to attach to this field
	 * @param valueHolder
	 *                    The holder to get the value from
	 * @param nDelay
	 *                    The delay in ms between value updates
	 */
	public HolderOutputPanel(final String lab, final Holder<String> valueHolder,
			final int nDelay) {
		this.val = valueHolder;
		this.nDelay = nDelay;

		setLayout(new HLayout(2));

		final JLabel label = new JLabel(lab);
		value = new JLabel("(stopped)");

		updater = new Timer(nDelay, event -> {
			value.setText(valueHolder.getValue());
		});

		add(label);
		add(value);
	}

	/**
	 * Set this panel back to its initial state
	 */
	public void reset() {
		stopUpdating();

		value.setText("(stopped)");

		updater = new Timer(nDelay, event -> {
			value.setText(val.getValue());
		});
	}

	/**
	 * Start updating the contents of the field from the holder
	 */
	public void startUpdating() {
		updater.start();
	}

	/**
	 * Stop updating the contents of the field from the holder
	 */
	public void stopUpdating() {
		updater.stop();

		value.setText(value.getText() + " (stopped)");
	}
}
