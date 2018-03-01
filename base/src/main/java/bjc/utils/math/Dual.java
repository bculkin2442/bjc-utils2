package bjc.utils.math;

/**
 * Represents a 'dual' number.
 *
 * Think imaginary numbers, where instead of i, we add a value d such that d^2 =
 * 0.
 */
public class Dual {
	/**
	 * The real part of the dual number.
	 */
	public double real;
	/**
	 * The dual part of the dual number.
	 */
	public double dual;

	/**
	 * Create a new dual with both parts zero.
	 */
	public Dual() {
		real = 0;
		dual = 0;
	}

	/**
	 * Create a new dual number with a zero dual part.
	 *
	 * @param real
	 *        The real part of the number.
	 */
	public Dual(double real) {
		this.real = real;
		this.dual = 0;
	}

	/**
	 * Create a new dual number with a specified dual part.
	 *
	 * @param real
	 *        The real part of the number.
	 * @param dual
	 *        The dual part of the number.
	 */
	public Dual(double real, double dual) {
		this.real = real;
		this.dual = dual;
	}

	@Override
	public String toString() {
		return String.format("<%f, %f>", real, dual);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dual);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Dual other = (Dual) obj;
		if(Double.doubleToLongBits(dual) != Double.doubleToLongBits(other.dual)) return false;
		if(Double.doubleToLongBits(real) != Double.doubleToLongBits(other.real)) return false;
		return true;
	}
}