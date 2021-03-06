package bjc.utils.gui.panels;

import java.text.ParseException;
import java.util.function.Consumer;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import bjc.utils.gui.layout.HLayout;
import bjc.utils.gui.panels.SliderInputPanel.*;

/**
 * A simple input panel for a slider-controlled value and a manual-input field
 * for setting the slider
 *
 * @author ben
 *
 */
public class SliderInputPanel extends JPanel {
	/**
	 * Represents the settings for a slider
	 *
	 * @author ben
	 *
	 */
	public static class SliderSettings {
		/**
		 * The minimum value of the slider
		 */
		public final int minValue;
		/**
		 * The maximum value of the slider
		 */
		public final int maxValue;

		/**
		 * The initial value of the slider
		 */
		public final int initValue;

		/**
		 * Create a new slider settings, with the initial value in the middle
		 *
		 * @param min
		 *            The minimum value of the slider
		 * @param max
		 *            The maximum value of the slider
		 */
		public SliderSettings(final int min, final int max) {
			this(min, max, (min + max) / 2);
		}

		/**
		 * Create a new set of slider sttings
		 *
		 * @param min
		 *             The minimum slider value
		 * @param max
		 *             The maximum slider value
		 * @param init
		 *             The initial slider value
		 */
		public SliderSettings(final int min, final int max, final int init) {
			minValue = min;
			maxValue = max;

			initValue = init;
		}
	}

	private static final long serialVersionUID = 2956394160569961404L;
	private final JSlider slider;
	private final JFormattedTextField field;

	/**
	 * Create a new slider input panel
	 *
	 * @param lab
	 *                  The label for the field
	 * @param settings
	 *                  The settings for slider values
	 * @param majorTick
	 *                  The setting for where to place big ticks
	 * @param minorTick
	 *                  The setting for where to place small ticks
	 * @param action
	 *                  The action to execute for a given value
	 */
	public SliderInputPanel(final String lab, final SliderSettings settings,
			final int majorTick, final int minorTick, final Consumer<Integer> action) {
		setLayout(new HLayout(3));

		final JLabel label = new JLabel(lab);

		slider = new JSlider(settings.minValue, settings.maxValue, settings.initValue);
		field = new JFormattedTextField(new NumberFormatter(settings));

		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		slider.addChangeListener(event -> {
			if (slider.getValueIsAdjusting()) {
				// Do nothing
			} else {
				final int val = slider.getValue();

				field.setValue(val);
				action.accept(val);
			}
		});

		field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		field.setColumns(15);
		field.addPropertyChangeListener("value", event -> {
			final Object value = field.getValue();

			if (value == null) {
				// Do nothing
			} else {
				slider.setValue((Integer) value);
			}
		});

		add(label);
		add(slider);
		add(field);
	}

	/**
	 * Reset the values in this panel to a specified value
	 *
	 * @param value
	 *              The value to reset the fields to
	 */
	public void resetValues(final int value) {
		slider.setValue(value);

		field.setValue(value);
	}
}

final class NumberFormatter extends JFormattedTextField.AbstractFormatter {
	private static final long serialVersionUID = -4448291795913908270L;

	private final int minValue;
	private final int maxValue;

	private final int initValue;

	public NumberFormatter(final SliderSettings settings) {
		minValue = settings.minValue;
		maxValue = settings.maxValue;

		initValue = settings.initValue;
	}

	@Override
	public Object stringToValue(final String text) throws ParseException {
		try {
			final int val = Integer.parseInt(text);

			if      (val < minValue) throw new ParseException(
					"Value must be greater than " + minValue, 0);
			else if (val > maxValue) throw new ParseException(
					"Value must be smaller than " + maxValue, 0);
			else                     return val;
		} catch (final NumberFormatException nfex) {
			final ParseException pex
					= new ParseException("Value must be a valid integer", 0);

			pex.initCause(nfex);

			throw pex;
		}
	}

	@Override
	public String valueToString(final Object value) throws ParseException {
		if (value == null) return Integer.toString(initValue);

		return Integer.toString((Integer) value);
	}
}
