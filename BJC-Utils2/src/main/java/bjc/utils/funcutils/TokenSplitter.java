package bjc.utils.funcutils;

import java.util.function.BiFunction;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

final class TokenSplitter
		implements BiFunction<String, String, IList<String>> {
	private String tokenToSplit;

	public TokenSplitter(String tok) {
		this.tokenToSplit = tok;
	}

	@Override
	public IList<String> apply(String operatorName,
			String operatorRegex) {
		if (operatorName == null) {
			throw new NullPointerException(
					"Operator name must not be null");
		} else if (operatorRegex == null) {
			throw new NullPointerException(
					"Operator regex must not be null");
		}

		if (tokenToSplit.contains(operatorName)) {
			if (StringUtils.containsOnly(tokenToSplit, operatorRegex)) {
				return new FunctionalList<>(tokenToSplit);
			}

			IList<String> splitTokens = new FunctionalList<>(
					tokenToSplit.split(operatorRegex));

			IList<String> result = new FunctionalList<>();

			int tokenExpansionSize = splitTokens.getSize();

			splitTokens.forEachIndexed((tokenIndex, token) -> {

				if (tokenIndex != tokenExpansionSize && tokenIndex != 0) {
					result.add(operatorName);
					result.add(token);
				} else {
					result.add(token);
				}
			});

			return result;
		}

		return new FunctionalList<>(tokenToSplit);
	}
}