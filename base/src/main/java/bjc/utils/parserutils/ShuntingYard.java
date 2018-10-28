package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;
import bjc.utils.funcutils.StringUtils;

/**
 * Utility to run the shunting yard algorithm on a bunch of tokens.
 *
 * @author ben
 *
 * @param <TokenType>
 *                The type of tokens being shunted.
 */
public class ShuntingYard<TokenType> {
	/**
	 * A enum representing the fundamental operator types.
	 *
	 * @author ben
	 *
	 */
	public static enum Operator implements IPrecedent {
		/**
		 * Represents addition.
		 */
		ADD(1),
		/**
		 * Represents subtraction.
		 */
		SUBTRACT(2),

		/**
		 * Represents multiplication.
		 */
		MULTIPLY(3),
		/**
		 * Represents division.
		 */
		DIVIDE(4);

		private final int precedence;

		private Operator(final int prec) {
			precedence = prec;
		}

		@Override
		public int getPrecedence() {
			return precedence;
		}
	}

	/*
	 * Holds all the shuntable operations.
	 */
	private IMap<String, IPrecedent> operators;

	/**
	 * Create a new shunting yard with a default set of operators.
	 *
	 * @param configureBasics
	 *                Whether or not basic math operators should be
	 *                provided.
	 */
	public ShuntingYard(final boolean configureBasics) {
		operators = new FunctionalMap<>();

		/*
		 * Add basic operators if we're configured to do so
		 */
		if (configureBasics) {
			operators.put("+", Operator.ADD);
			operators.put("-", Operator.SUBTRACT);
			operators.put("*", Operator.MULTIPLY);
			operators.put("/", Operator.DIVIDE);
		}
	}

	/**
	 * Add an operator to the list of shuntable operators.
	 *
	 * @param operator
	 *                The token representing the operator.
	 *
	 * @param precedence
	 *                The precedence of the operator to add.
	 */
	public void addOp(final String operator, final int precedence) {
		/*
		 * Create the precedence marker
		 */
		final IPrecedent prec = IPrecedent.newSimplePrecedent(precedence);

		this.addOp(operator, prec);
	}

	/**
	 * Add an operator to the list of shuntable operators.
	 *
	 * @param operator
	 *                The token representing the operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public void addOp(final String operator, final IPrecedent precedence) {
		/*
		 * Complain about trying to add an incorrect operator
		 */
		if (operator == null)
			throw new NullPointerException("Operator must not be null");
		else if (precedence == null) throw new NullPointerException("Precedence must not be null");

		/*
		 * Add the operator to the ones we handle
		 */
		operators.put(operator, precedence);
	}

	/* Check if one operator is higher precedence than another. */
	private boolean isHigherPrec(final String left, final String right) {
		/*
		 * Check if the right operator exists
		 */
		final boolean exists = operators.containsKey(right);

		/*
		 * If it doesn't, the left is higher precedence.
		 */
		if (!exists) return false;

		/*
		 * Get the precedence of operators
		 */
		final int rightPrecedence = operators.get(right).getPrecedence();
		final int leftPrecedence = operators.get(left).getPrecedence();

		/*
		 * Evaluate what we were asked
		 */
		return rightPrecedence >= leftPrecedence;
	}

	/**
	 * Transform a string of tokens from infix notation to postfix.
	 *
	 * @param input
	 *                The string to transform.
	 *
	 * @param transformer
	 *                The function to use to transform strings to tokens.
	 *
	 * @return A list of tokens in postfix notation.
	 */
	public IList<TokenType> postfix(final IList<String> input, final Function<String, TokenType> transformer) {
		/*
		 * Check our input
		 */
		if (input == null)
			throw new NullPointerException("Input must not be null");
		else if (transformer == null) throw new NullPointerException("Transformer must not be null");

		/*
		 * Here's what we're handing back
		 */
		final IList<TokenType> output = new FunctionalList<>();

		/*
		 * The stack to put operators on
		 */
		final Deque<String> stack = new LinkedList<>();

		for (String token : input) {
			/*
			 * Handle operators
			 */
			if (operators.containsKey(token)) {
				/*
				 * Pop operators while there isn't a higher
				 * precedence one
				 */
				while (!stack.isEmpty() && isHigherPrec(token, stack.peek())) {
					output.add(transformer.apply(stack.pop()));
				}

				/*
				 * Put this operator onto the stack
				 */
				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\(")) {
				/*
				 * Handle groups of parenthesis for multiple
				 * nesting levels
				 */
				stack.push(token);
			} else if (StringUtils.containsOnly(token, "\\)")) {
				/*
				 * Handle groups of parenthesis for multiple
				 * nesting levels
				 */
				final String swappedToken = token.replace(')', '(');

				/*
				 * Remove tokens up to a matching parenthesis
				 */
				while (!stack.peek().equals(swappedToken)) {
					output.add(transformer.apply(stack.pop()));
				}

				/*
				 * Remove the parenthesis
				 */
				stack.pop();
			} else {
				/*
				 * Just add the transformed token
				 */
				output.add(transformer.apply(token));
			}
		}

		for (String token : stack) {
			output.add(transformer.apply(token));
		}

		return output;
	}

	/**
	 * Remove an operator from the list of shuntable operators.
	 *
	 * @param operator
	 *                The token representing the operator. If null, remove
	 *                all operators.
	 */
	public void removeOp(final String operator) {
		/*
		 * Check if we want to remove all operators
		 */
		if (operator == null) {
			operators = new FunctionalMap<>();
		} else {
			operators.remove(operator);
		}
	}
}
