package bjc.utils.parserutils.splitter;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.ListEx;

/**
 * A token splitter that chains several other splitters together.
 *
 * @author EVE
 *
 */
public class ChainTokenSplitter implements TokenSplitter {
	private final ListEx<TokenSplitter> spliters;

	/**
	 * Create a new chain token splitter.
	 */
	public ChainTokenSplitter() {
		spliters = new FunctionalList<>();
	}

	/**
	 * Append a series of splitters to the chain.
	 *
	 * @param splitters
	 *                  The splitters to append to the chain.
	 */
	public void appendSplitters(final TokenSplitter... splitters) {
		spliters.addAll(splitters);
	}

	/**
	 * Prepend a series of splitters to the chain.
	 *
	 * @param splitters
	 *                  The splitters to append to the chain.
	 */
	public void prependSplitters(final TokenSplitter... splitters) {
		spliters.prependAll(splitters);
	}

	@Override
	public ListEx<String> split(final String input) {
		final ListEx<String> initList = new FunctionalList<>(input);

		return spliters.reduceAux(initList, (splitter, strangs) -> strangs.flatMap(splitter::split));
	}
}