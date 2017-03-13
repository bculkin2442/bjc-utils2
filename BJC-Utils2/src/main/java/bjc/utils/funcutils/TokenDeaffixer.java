package bjc.utils.funcutils;

import java.util.function.BiFunction;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

final class TokenDeaffixer implements BiFunction<String, String, IList<String>> {
	private String token;

	public TokenDeaffixer(String tok) {
		token = tok;
	}

	@Override
	public IList<String> apply(String operatorName, String operatorRegex) {
		if (operatorName == null) {
			throw new NullPointerException("Operator name must not be null");
		} else if (operatorRegex == null) {
			throw new NullPointerException("Operator regex must not be null");
		}

		if (StringUtils.containsOnly(token, operatorRegex)) {
			return new FunctionalList<>(token);
		} else if (token.startsWith(operatorName)) {
			if (token.endsWith(operatorName)) {
				return new FunctionalList<>(operatorName, token.split(operatorRegex)[1], operatorName);
			}

			return new FunctionalList<>(operatorName, token.split(operatorRegex)[1]);
		} else if (token.endsWith(operatorName)) {
			return new FunctionalList<>(token.split(operatorRegex)[0], operatorName);
		} else if (token.contains(operatorName)) {
			String[] tokenParts = token.split(operatorRegex);

			IList<String> returned = new FunctionalList<>();

			for (int i = 0; i < tokenParts.length; i++) {
				returned.add(tokenParts[i]);

				if (i < tokenParts.length - 1) {
					returned.add(operatorName);
				}
			}

			return returned;
		} else {
			return new FunctionalList<>(token);
		}
	}
}
