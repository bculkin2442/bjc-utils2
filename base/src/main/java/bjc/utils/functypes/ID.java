package bjc.utils.functypes;

import java.util.function.UnaryOperator;

/**
 * Identity function.
 * 
 * @author bjculkin
 */
public class ID {
	/**
	 * Create an identity function.
	 *
	 * @return
	 * 	A identity function.
	 */
	public static <A> UnaryOperator<A> id() {
		return (x) -> x;
	}
}
