package bjc.utils.math;

/**
 * Represents an expression using dual numbers.
 *
 * Useful for automatically differentiating expressions.
 */
public class DualExpr {
	/**
	 * Represents the various types of dual expressions.
	 */
	public static enum ExprType {
		/**
		 * A fixed number.
		 */
		CONSTANT,
		/**
		 * An addition operation.
		 */
		ADDITION,
		/**
		 * A subtraction operation.
		 */
		SUBTRACTION,
		/**
		 * A multiplication operation.
		 */
		MULTIPLICATION,
		/**
		 * A division operation.
		 */
		DIVISION,
		/**
		 * A sine operation.
		 */
		SIN,
		/**
		 * A cosine operation.
		 */
		COS,
		/**
		 * An exponential function.
		 */
		EXPONENTIAL,
		/**
		 * A logarithm function.
		 */
		LOGARITHM,
		/**
		 * A power operation.
		 */
		POWER,
		/**
		 * An absolute value.
		 */
		ABSOLUTE
	}

	/**
	 * The type of the expression.
	 */
	public final DualExpr.ExprType type;

	/**
	 * The dual number value, for constants.
	 */
	public Dual number;

	/**
	 * The left (or first) part of the expression.
	 */
	public DualExpr left;
	/**
	 * The right (or second) part of the expression.
	 */
	public DualExpr right;

	/**
	 * The power to use, for power operations.
	 */
	public int power;

	/**
	 * Create a new constant dual number.
	 *
	 * @param num
	 *        The value of the dual number.
	 */
	public DualExpr(Dual num) {
		this.type = ExprType.CONSTANT;

		number = num;
	}

	/**
	 * Create a new unary dual number.
	 *
	 * @param type
	 *        The type of operation to perform.
	 * @param val
	 *        The parameter to the value.
	 */
	public DualExpr(DualExpr.ExprType type, DualExpr val) {
		this.type = type;

		left = val;
	}

	/**
	 * Create a new binary dual number.
	 *
	 * @param type
	 *        The type of operation to perform.
	 * @param left
	 *        The left hand side of the expression.
	 * @param right
	 *        The right hand side of the expression.
	 */
	public DualExpr(DualExpr.ExprType type, DualExpr left, DualExpr right) {
		this.type = type;

		this.left = left;
		this.right = right;
	}

	/**
	 * Create a new power expression.
	 *
	 * @param left
	 *        The expression to raise.
	 * @param power
	 *        The power to raise it by.
	 */
	public DualExpr(DualExpr left, int power) {
		this.type = ExprType.POWER;

		this.left = left;
		this.power = power;
	}

	/**
	 * Evaluate an expression to a number.
	 *
	 * Uses the rules provided in
	 * https://en.wikipedia.org/wiki/Automatic_differentiation
	 *
	 * @return The evaluated expression.
	 */
	public Dual evaluate() {
		/* The evaluated dual numbers. */
		Dual lval, rval;

		/* Perform the right operation for each type. */
		switch(type) {
		case CONSTANT:
			return number;
		case ADDITION:
			lval = left.evaluate();
			rval = right.evaluate();

			return new Dual(lval.real + rval.real, lval.dual + rval.dual);
		case SUBTRACTION:
			lval = left.evaluate();
			rval = right.evaluate();

			return new Dual(lval.real - rval.real, lval.dual - rval.dual);
		case MULTIPLICATION:
			lval = left.evaluate();
			rval = right.evaluate();

		{
			double lft = lval.dual * rval.real;
			double rght = lval.real * rval.dual;

			return new Dual(lval.real * rval.real, lft + rght);
		}
		case DIVISION:
			lval = left.evaluate();
			rval = right.evaluate();

		{
			if(rval.real == 0) {
				throw new IllegalArgumentException("ERROR: Attempted to divide by zero.");
			}

			double lft = lval.dual * rval.real;
			double rght = lval.real * rval.dual;

			double val = (lft - rght) / (rval.real * rval.real);

			return new Dual(lval.real / rval.real, val);
		}
		case SIN:
			lval = left.evaluate();

			return new Dual(Math.sin(lval.real), lval.dual * Math.cos(lval.real));
		case COS:
			lval = left.evaluate();

			return new Dual(Math.cos(lval.real), -lval.dual * Math.sin(lval.real));
		case EXPONENTIAL:
			lval = left.evaluate();

		{
			double val = Math.exp(lval.real);

			return new Dual(val, lval.dual * val);
		}
		case LOGARITHM:
			lval = left.evaluate();

			if(lval.real <= 0) {
				throw new IllegalArgumentException("ERROR: Attempted to take non-positive log.");
			}

			return new Dual(Math.log(lval.real), lval.dual / lval.real);
		case POWER:
			lval = left.evaluate();

			if(lval.real == 0) {
				throw new IllegalArgumentException("ERROR: Raising zero to a power.");
			}

		{
			double rl = Math.pow(lval.real, power);

			double lft = Math.pow(lval.real, power - 1);

			return new Dual(rl, power * lft * lval.dual);
		}
		case ABSOLUTE:
			lval = left.evaluate();

			return new Dual(Math.abs(lval.real), lval.dual * Math.signum(lval.real));
		default:
			String msg = "ERROR: Unknown expression type %s";

			throw new IllegalArgumentException(String.format(msg, type));
		}
	}

	@Override
	public String toString() {
		switch(type) {
		case ABSOLUTE:
			return String.format("abs(%s)", left.toString());
		case ADDITION:
			return String.format("(%s + %s)", left.toString(), right.toString());
		case CONSTANT:
			return String.format("%s", number.toString());
		case COS:
			return String.format("cos(%s)", left.toString());
		case DIVISION:
			return String.format("(%s / %s)", left.toString(), right.toString());
		case EXPONENTIAL:
			return String.format("exp(%s)", left.toString());
		case LOGARITHM:
			return String.format("log(%s)", left.toString());
		case MULTIPLICATION:
			return String.format("(%s * %s)", left.toString(), right.toString());
		case POWER:
			return String.format("(%s ^ %d)", left.toString(), power);
		case SIN:
			return String.format("sin(%s)", left.toString());
		case SUBTRACTION:
			return String.format("(%s - %s)", left.toString(), right.toString());
		default:
			return String.format("UNKNOWN_EXPR");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + power;
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;

		DualExpr other = (DualExpr) obj;

		if(type != other.type) {
			return false;
		}

		if(left == null) {
			if(other.left != null) {
				return false;
			}
		} else if(!left.equals(other.left)) {
			return false;
		}

		if(number == null) {
			if(other.number != null) return false;
		} else if(!number.equals(other.number)) {
			return false;
		}

		if(power != other.power) {
			return false;
		}

		if(right == null) {
			if(other.right != null) {
				return false;
			}
		} else if(!right.equals(other.right)) {
			return false;
		}

		return true;
	}
}