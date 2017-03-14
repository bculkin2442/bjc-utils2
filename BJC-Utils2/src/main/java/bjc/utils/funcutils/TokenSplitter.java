package bjc.utils.funcutils;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

import java.util.Iterator;
import java.util.function.BiFunction;

final class TokenSplitter implements BiFunction<String, String, IList<String>> {
	private String tokenToSplit;

	public TokenSplitter(String tok) {
		this.tokenToSplit = tok;
	}

	@Override
	public IList<String> apply(String operatorName, String operatorRegex) {
		if(operatorName == null)
			throw new NullPointerException("Operator name must not be null");
		else if(operatorRegex == null) throw new NullPointerException("Operator regex must not be null");

		if(tokenToSplit.contains(operatorName)) {
			if(StringUtils.containsOnly(tokenToSplit, operatorRegex))
				return new FunctionalList<>(tokenToSplit);

			IList<String> splitTokens = new FunctionalList<>(tokenToSplit.split(operatorRegex));
			IList<String> result = new FunctionalList<>();

			Iterator<String> itr = splitTokens.toIterable().iterator();
			int tokenExpansionSize = splitTokens.getSize();

			String elm = itr.next();

			for(int i = 0; itr.hasNext(); elm = itr.next()) {
				result.add(elm);

				if(i != tokenExpansionSize) {
					result.add(operatorName);
				}
			}

			return result;
		}

		return new FunctionalList<>(tokenToSplit);
	}
}
