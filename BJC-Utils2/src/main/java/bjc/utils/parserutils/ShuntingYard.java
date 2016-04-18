package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IFunctionalList;
import bjc.utils.funcdata.IFunctionalMap;
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

		private Operator(int prec) {
			precedence = prec;
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

	private final class TokenShunter implements Consumer<String> {
		private IFunctionalList<E>	output;
		private Deque<String>		stack;
		private Function<String, E>	transform;

		public TokenShunter(IFunctionalList<E> outpt, Deque<String> stack,
				Function<String, E> transform) {
			this.output = outpt;
			this.stack = stack;
			this.transform = transform;
		}

		@Override
		public void accept(String token) {
			if (operators.containsKey(token)) {
				while (!stack.isEmpty()
						&& isHigherPrec(token, stack.peek())) {
					output.add(transform.apply(stack.pop()));
				}

				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\(")) {
				// Handle groups of parenthesis for multiple nesting levels
				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\)")) {
				// Handle groups of parenthesis for multiple nesting levels
				String swappedToken = token.replace(')', '(');

				while (!stack.peek().equals(swappedToken)) {
					output.add(transform.apply(stack.pop()));
				}

				stack.pop();
			} else {
				output.add(transform.apply(token));
			}
		}
	}

	/**
	 * Holds all the shuntable operations
	 */
	private IFunctionalMap<String, IPrecedent> operators;

	/**
	 * Create a new shunting yard with a default set of operators
	 * @param configureBasics Whether or not basic math operators should be provided
	 */
	public ShuntingYard(boolean configureBasics) {
		operators = new FunctionalMap<>();

		if (configureBasics) {
			operators.put("+", Operator.ADD);
			operators.put("-", Operator.SUBTRACT);
			operators.put("*", Operator.MULTIPLY);
			operators.put("/", Operator.DIVIDE);
		}
	}

	/**
	 * Add an operator to the list of shuntable operators
	 * 
	 * @param operatorToken
	 *            The token representing the operator
	 * @param precedence
	 *            The precedence of the operator to add
	 */
	public void addOp(String operatorToken, int precedence) {
		this.addOp(operatorToken,
				IPrecedent.newSimplePrecedent(precedence));
	}

	/**
	 * Add an operator to the list of shuntable operators
	 * 
	 * @param operatorToken
	 *            The token representing the operator
	 * @param precedence
	 *            The precedence of the operator
	 */
	public void addOp(String operatorToken, IPrecedent precedence) {
		if (operatorToken == null) {
			throw new NullPointerException("Operator must not be null");
		}

		operators.put(operatorToken, precedence);
	}

	private boolean isHigherPrec(String leftOperator,
			String rightOperator) {
		boolean operatorExists = operators.containsKey(rightOperator);

		if (!operatorExists) {
			return false;
		}

		boolean hasHigherPrecedence =
				operators.get(rightOperator).getPrecedence() >= operators
						.get(leftOperator).getPrecedence();

		return hasHigherPrecedence;
	}

	/**
	 * Transform a string of tokens from infix notation to postfix
	 * 
	 * @param input
	 *            The string to transform
	 * @param tokenTransformer
	 *            The function to use to transform strings to tokens
	 * @return A list of tokens in postfix notation
	 */
	public IFunctionalList<E> postfix(IFunctionalList<String> input,
			Function<String, E> tokenTransformer) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (tokenTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		IFunctionalList<E> output = new FunctionalList<>();

		Deque<String> stack = new LinkedList<>();

		input.forEach(new TokenShunter(output, stack, tokenTransformer));

		while (!stack.isEmpty()) {
			output.add(tokenTransformer.apply(stack.pop()));
		}

		return output;
	}

	/**
	 * Remove an operator from the list of shuntable operators
	 * 
	 * @param tok
	 *            The token representing the operator
	 */
	public void removeOp(String tok) {
		if (tok == null) {
			throw new NullPointerException("Token must not be null");
		}

		operators.remove(tok);
	}
}