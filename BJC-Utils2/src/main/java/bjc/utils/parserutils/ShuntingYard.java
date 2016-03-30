package bjc.utils.parserutils;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcutils.StringUtils;

/**
 * Utility to run the shunting yard algorithm on a bunch of tokens
 * 
 * @author ben
 *
 * @param <E>
 *            The type of tokens being shunted
 */
public class ShuntingYard<E> {

	private final class TokenShunter implements Consumer<String> {
		private FunctionalList<E>	outp;
		private Deque<String>		stack;
		private Function<String, E>	transform;

		public TokenShunter(FunctionalList<E> outp, Deque<String> stack,
				Function<String, E> transform) {
			this.outp = outp;
			this.stack = stack;
			this.transform = transform;
		}

		@Override
		public void accept(String token) {
			if (ops.containsKey(token)) {
				while (!stack.isEmpty()
						&& isHigherPrec(token, stack.peek())) {
					outp.add(transform.apply(stack.pop()));
				}

				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\(")) {
				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\)")) {
				while (stack.peek().equals(token.replace(')', '('))) {
					outp.add(transform.apply(stack.pop()));
				}

				stack.pop();
			} else {
				outp.add(transform.apply(token));
			}
		}
	}

	/**
	 * A enum representing the fundamental operator types
	 * 
	 * @author ben
	 *
	 */
	public static enum Operator implements IPrecedent {
		/**
		 * Represents addition
		 */
		ADD(1),
		/**
		 * Represents division
		 */
		DIVIDE(4),
		/**
		 * Represents multiplication
		 */
		MULTIPLY(3),
		/**
		 * Represents subtraction
		 */
		SUBTRACT(2);

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
	 * @param i
	 *            The precedence of the operator to add
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

		inp.forEach(new TokenShunter(outp, stack, transform));

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