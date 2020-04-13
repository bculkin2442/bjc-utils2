package bjc.utils.ioutils.format.directives;

import java.io.*;
import bjc.esodata.*;
import bjc.utils.ioutils.format.*;
import bjc.utils.ioutils.format.exceptions.*;

/**
 * Implementation of the ? directive, which does recursive execution of a format
 * string.
 *
 * @author bjculkin
 *
 */
public class RecursiveDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		return new RecursiveEdict(compCTX.decr.modifiers.atMod, compCTX.formatter);
	}
}

class RecursiveEdict implements Edict {
	private boolean isInline;

	private CLFormatter fmt;

	public RecursiveEdict(boolean isInline, CLFormatter fmt) {
		this.isInline = isInline;

		this.fmt = fmt;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		// System.err.printf("[TRACE] Processing ? directive with params: " +
		// formCTX.items.toString());
		Object body = formCTX.items.item();

		formCTX.items.right();

		if (isInline) {
			if (!(body instanceof String)) {
				throw new MismatchedFormatArgType("?", String.class, body.getClass());
			}

			try {
				String bod = (String) body;

				fmt.doFormatString(bod, formCTX.writer, formCTX.items, true);
			} catch (DirectiveEscape eex) {
				if (eex.endIteration) {
					throw new UnexpectedColonEscape();
				}
			}
		} else {
			if (formCTX.items.atEnd()) {
				throw new IllegalArgumentException(
						"? directive requires two format parameters");
			}

			Object o = formCTX.items.item();
			formCTX.items.right();

			if (!(o instanceof Iterable<?>)) {
				throw new MismatchedFormatArgType("?", Iterable.class, o.getClass());
			}

			if (!(body instanceof String)) {
				throw new MismatchedFormatArgType("?", String.class, body.getClass());
			}

			@SuppressWarnings("unchecked")
			Iterable<Object> itb = (Iterable<Object>) o;
			Tape<Object> newParams = new SingleTape<>(itb);

			try {
				String bod = (String) body;

				// :DynamicString
				fmt.doFormatString(bod, formCTX.writer, newParams, true);
			} catch (DirectiveEscape eex) {
				throw new UnexpectedColonEscape();
			}
		}
	}
}
