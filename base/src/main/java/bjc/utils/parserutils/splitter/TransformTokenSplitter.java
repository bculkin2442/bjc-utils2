package bjc.utils.parserutils.splitter;

import java.util.function.UnaryOperator;

import bjc.funcdata.ListEx;

/**
 * A token splitter that performs a transform on the tokens from another
 * splitter.
 *
 * @author bjculkin
 *
 */
public class TransformTokenSplitter implements TokenSplitter {
	private TokenSplitter source;

	private UnaryOperator<String> transform;

	/**
	 * Create a new transforming splitter.
	 *
	 * @param source
	 *                  The splitter to use as a source.
	 *
	 * @param transform
	 *                  The transform to apply to tokens.
	 */
	public TransformTokenSplitter(TokenSplitter source, UnaryOperator<String> transform) {
		this.source = source;
		this.transform = transform;
	}

	@Override
	public ListEx<String> split(String input) {
		return source.split(input).map(transform);
	}

}
