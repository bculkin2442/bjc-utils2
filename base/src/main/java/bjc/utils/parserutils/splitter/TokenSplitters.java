package bjc.utils.parserutils.splitter;

import java.util.function.UnaryOperator;

/**
 * Factory methods for producing token splitters.
 * 
 * @author student
 *
 */
public class TokenSplitters {
	/**
	 * Create a new chained token splitter.
	 * 
	 * @param splitters
	 *            The series of splitters to chain together.
	 * @return A chained-together series of splitters.
	 */
	public static TokenSplitter chainSplitter(final TokenSplitter... splitters) {
		ChainTokenSplitter cts = new ChainTokenSplitter();

		cts.appendSplitters(splitters);

		return cts;
	}

	/**
	 * Create a new transforming token splitter.
	 * 
	 * @param splitter
	 *            The splitter to gain tokens from
	 * 
	 * @param transform
	 *            The transform to apply to the strings.
	 * 
	 * @return A splitter that applies the chosen transform to the tokens.
	 */
	public static TokenSplitter transformSplitter(final TokenSplitter splitter, UnaryOperator<String> transform) {
		return new TransformTokenSplitter(splitter, transform);
	}
}
