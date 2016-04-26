package bjc.utils.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import bjc.utils.data.IHolder;
import bjc.utils.gui.layout.HLayout;

/**
 * A panel that outputs a value bound to a {@link IHolder}
 * 
 * @author ben
 *
 */
public class HolderOutputPanel extends JPanel {
	private static final long	serialVersionUID	= 166573313903782080L;
	private Timer				updateTimer;
	private JLabel				value;

	/**
	 * Create a new display panel, backed by a holder
	 * 
	 * @param lab
	 *            The label to attach to this field
	 * @param val
	 *            The holder to get the value from
	 * @param nDelay
	 *            The delay in ms between value updates
	 */
	public HolderOutputPanel(String lab, IHolder<String> val, int nDelay) {
		setLayout(new HLayout(2));

		JLabel label = new JLabel(lab);
		value = new JLabel(val.getValue() + " (stopped)");

		updateTimer = new Timer(nDelay, (event) -> {
			value.setText(val.getValue());
		});

		add(label);
		add(value);
	}

	/**
	 * Start updating the contents of the field from the holder
	 */
	public void startUpdating() {
		updateTimer.start();
	}

	/**
	 * Stop updating the contents of the field from the holder
	 */
	public void stopUpdating() {
		updateTimer.stop();

		value.setText(value.getText() + " (stopped)");
	}
}
