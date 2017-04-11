package bjc.utils.parserutils.splitter;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.functypes.ID;

/**
 * A token splitter that chains several other splitters together.
 *
 * @author EVE
 *
 */
public class ChainTokenSplitter implements TokenSplitter {
	private final IList<TokenSplitter> spliters;

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
	 *                The splitters to append to the chain.
	 */
	public void appendSplitters(final TokenSplitter... splitters) {
		spliters.addAll(splitters);
	}

	/**
	 * Prepend a series of splitters to the chain.
	 *
	 * @param splitters
	 *                The splitters to append to the chain.
	 */
	public void prependSplitters(final TokenSplitter... splitters) {
		spliters.prependAll(splitters);
	}

	@Override
	public IList<String> split(final String input) {
		final IList<String> initList = new FunctionalList<>(input);

		return spliters.reduceAux(initList, (splitter, strangs) -> {
			return strangs.flatMap(splitter::split);
		}, ID.id());
	}
}