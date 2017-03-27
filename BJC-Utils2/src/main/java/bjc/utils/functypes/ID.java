package bjc.utils.functypes;

import java.util.function.UnaryOperator;

public class ID {
	public static <A> UnaryOperator<A> id() {
		return (x) -> x;
	}
}
