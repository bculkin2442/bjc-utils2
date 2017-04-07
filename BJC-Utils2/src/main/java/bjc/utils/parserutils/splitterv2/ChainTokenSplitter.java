package bjc.utils.parserutils.splitterv2;

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
	private IList<TokenSplitter> spliters;

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
	public void appendSplitters(TokenSplitter... splitters) {
		spliters.addAll(splitters);
	}

	/**
	 * Prepend a series of splitters to the chain.
	 * 
	 * @param splitters
	 *                The splitters to append to the chain.
	 */
	public void prependSplitters(TokenSplitter... splitters) {
		spliters.prependAll(splitters);
	}

	@Override
	public IList<String> split(String input) {
		IList<String> initList = new FunctionalList<>(input);

		return spliters.reduceAux(initList, (splitter, strangs) -> {
			return strangs.flatMap(splitter::split);
		}, ID.id());
	}
}