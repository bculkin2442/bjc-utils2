package bjc.utils.parserutils;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import bjc.utils.data.IPrecedent;
import bjc.utils.funcdata.FunctionalList;

/**
 * Utility to run the shunting yard algorithm on a bunch of tokens
 * 
 * @author ben
 *
 * @param <E>
 *            The type of tokens being shunted
 */
public class ShuntingYard<E> {

	public static enum Operator implements IPrecedent {
		ADD(1), DIVIDE(4), MULTIPLY(3), SUBTRACT(2);

		private final int precedence;

		private Operator(int p) {
			precedence = p;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see bjc.utils.parserutils.IPrecedent#getPrecedence()
		 */
		@Override
		public int getPrecedence() {
			return precedence;
		}
	}

	/**
	 * Holds all the shuntable operations
	 */
	private Map<String, IPrecedent> ops;

	/**
	 * Create a new shunting yard with a default set of operators
	 */
	public ShuntingYard() {
		ops = new HashMap<>();

		ops.put("+", Operator.ADD);
		ops.put("-", Operator.SUBTRACT);
		ops.put("*", Operator.MULTIPLY);
		ops.put("/", Operator.DIVIDE);
	}

	/**
	 * Add an operator to the list of shuntable operators
	 * 
	 * @param tok
	 *            The token representing the operator
	 */
	public void addOp(String tok, int i) {
		this.addOp(tok, IPrecedent.newSimplePrecedent(i));
	}

	/**
	 * Add an operator to the list of shuntable operators
	 * 
	 * @param tok
	 *            The token representing the operator
	 * @param prec
	 *            The precedence of the operator
	 */
	public void addOp(String tok, IPrecedent prec) {
		ops.put(tok, prec);
	}

	private boolean isHigherPrec(String op, String sub) {
		return (ops.containsKey(sub) && ops.get(sub).getPrecedence() >= ops
				.get(op).getPrecedence());
	}

	/**
	 * Transform a string of tokens from infix notation to postfix
	 * 
	 * @param inp
	 *            The string to transform
	 * @param transform
	 *            The function to use to transform strings to tokens
	 * @return A list of tokens in postfix notation
	 */
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

	/**
	 * Remove an operator from the list of shuntable operators
	 * 
	 * @param tok
	 *            The token representing the operator
	 */
	public void removeOp(String tok) {
		ops.remove(tok);
	}
}