package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.IllegalFormatConversionException;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.EscapeException;

public class RecursiveDirective implements Directive {
	public void format(FormatParameters dirParams) throws IOException {
		dirParams.tParams.right();

		CLFormatter.checkItem(dirParams.item, '?');

		if (dirParams.mods.atMod) {
			if (!(dirParams.item instanceof String))
				throw new IllegalFormatConversionException('?', dirParams.item.getClass());

			try {
				dirParams.fmt.doFormatString((String)dirParams.item, dirParams.rw, dirParams.tParams, true);
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		} else {
			if (dirParams.tParams.atEnd())
				throw new IllegalArgumentException("? directive requires two format parameters");

			Object o = dirParams.tParams.item();
			dirParams.tParams.right();

			if (!(o instanceof Iterable))
				throw new IllegalFormatConversionException('?', o.getClass());

			Iterable<Object> itb = (Iterable<Object>)o;
			Tape<Object> newParams = new SingleTape<>(itb);

			try {
				dirParams.fmt.doFormatString((String)dirParams.item, dirParams.rw, newParams, true);
			} catch (EscapeException eex) {
				throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		}
	}
}
