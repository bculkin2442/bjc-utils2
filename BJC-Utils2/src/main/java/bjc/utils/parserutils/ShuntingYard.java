package bjc.utils.parserutils;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;

public class ShuntingYard<E> {

	private enum Operator {
		ADD(1), SUBTRACT(2), MULTIPLY(3), DIVIDE(4);
		final int precedence;

		Operator(int p) {
			precedence = p;
		}
	}

	private static Map<String, Operator> ops = new HashMap<String, Operator>();

	static {
		ops.put("+", Operator.ADD);
		ops.put("-", Operator.SUBTRACT);
		ops.put("*", Operator.MULTIPLY);
		ops.put("/", Operator.DIVIDE);
	}

	private boolean isHigherPrec(String op, String sub) {
		return (ops.containsKey(sub)
				&& ops.get(sub).precedence >= ops.get(op).precedence);
	}

	public FunctionalList<E> postfix(FunctionalList<String> inp,
			Function<String, E> transform) {
		FunctionalList<E> outp = new FunctionalList<>();
		Deque<String> stack = new LinkedList<>();

		inp.forEach((token) -> {
			if (ops.containsKey(token)) {
				while (!stack.isEmpty()
						&& isHigherPrec(token, stack.peek())) {
					outp.add(transform.apply(stack.pop()));
				}

				stack.push(token);
			} else if (token.equals("(")) {
				stack.push(token);
			} else if (token.equals(")")) {
				while (!stack.peek().equals("(")) {
					outp.add(transform.apply(stack.pop()));
				}

				stack.pop();
			} else {
				outp.add(transform.apply(token));
			}
		});

		while (!stack.isEmpty()) {
			outp.add(transform.apply(stack.pop()));
		}

		return outp;
	}

}