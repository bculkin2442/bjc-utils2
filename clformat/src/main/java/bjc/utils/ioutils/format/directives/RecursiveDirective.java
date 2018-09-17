package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.*;
import bjc.utils.ioutils.ReportWriter;
import java.util.IllegalFormatConversionException;

import java.io.IOException;
import java.util.regex.Matcher;

public class RecursiveDirective implements Directive {
	public void format(ReportWriter rw, Object arg, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		tParams.right();

		CLFormatter.checkItem(arg, '?');

		if (mods.atMod) {
			if (!(arg instanceof String))
				throw new IllegalFormatConversionException('?', arg.getClass());

			try {
				fmt.doFormatString((String)arg, rw, tParams, true);
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		} else {
			if (tParams.atEnd())
				throw new IllegalArgumentException("? directive requires two format parameters");

			Object o = tParams.item();
			tParams.right();

			if (!(o instanceof Iterable))
				throw new IllegalFormatConversionException('?', o.getClass());

			Iterable<Object> itb = (Iterable<Object>)o;
			Tape<Object> newParams = new SingleTape<>(itb);

			try {
				fmt.doFormatString((String)arg, rw, newParams, true);
			} catch (EscapeException eex) {
				throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		}
	}
}
