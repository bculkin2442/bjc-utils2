package bjc.utils.test.math;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import bjc.utils.exceptions.NonConstantPower;
import bjc.utils.exceptions.OperandsRemaining;
import bjc.utils.math.Dual;
import bjc.utils.math.DualExpr;
import bjc.utils.math.DualExprParser;
import bjc.utils.math.DualExprParser.Result;

@SuppressWarnings("javadoc")
public class DualExprParserTest {

	@Test
	public void testBasics() {
		assertDEEvalsTo(new Dual(1.0, 0.0), "1");
	}

	@Test
	public void testMath() {
		assertDEEvalsTo(new Dual(2.0, 0.0), "1 1 +");
		assertDEEvalsTo(new Dual(-1.0, 0.0), "1 2 -");
		assertDEEvalsTo(new Dual(4.0, 0.0), "2 2 *");
		assertDEEvalsTo(new Dual(2.0, 0.0), "4 2 /");
	}

	@Test
	public void testFuncs() {
		assertDEEvalsTo(new Dual(2.0, -0.0), "-2 abs");

		assertDEEvalsTo(new Dual(Math.sin(2), -0.0), "2 sin");
		assertDEEvalsTo(new Dual(Math.cos(2), -0.0), "2 cos");

		assertDEEvalsTo(new Dual(Math.log(2.0), 0.0), "2 log");
		assertDEEvalsTo(new Dual(Math.exp(2), 0.0), "2 exp");

		assertDEEvalsTo(new Dual(Math.sqrt(2), 0.0), "2 0.5 pow");
		assertDEEvalsTo(new Dual(4.0, 0.0), "2 2 pow");

		assertDEEvalsTo(new Dual(4.0, 0.0), "1 2 + eval 1 +");
	}

	@Test
	public void testVars() {
		assertDEEvalsTo(new Dual(2.0, 0.0), "x x +", "x", 1);

		assertDEEvalsTo(new Dual(4.0, 4.0), "x 2 pow", "x", new Dual(2.0, 1.0));
		assertDEEvalsTo(new Dual(8.0, 12.0), "x 3 pow", "x", new Dual(2.0, 1.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDivZero() {
		assertDEEvalsTo(new Dual(0.0, 0.0), "1 0 /");
	}

	@Test(expected = OperandsRemaining.class)
	public void testOpsRemaining() {
		DualExprParser.parseExpression("1 2");

		assertFalse("Shouldn't reach here", true);
	}

	@Test(expected = NonConstantPower.class)
	public void testNonConstantPower() {
		DualExprParser.parseExpression("1 1 1 dual pow");
	}

	private static void assertDEEvalsTo(Dual res, String inp) {
		assertDEEvalsTo(false, res, inp);
	}

	private static void assertDEEvalsTo(Dual res, String inp, Object... vars) {
		assertDEEvalsTo(false, res, inp, vars);
	}

	private static void assertDEEvalsTo(boolean printExpr, Dual res, String inp,
			Object... vars) {
		Map<String, DualExpr> varMap = new HashMap<>();

		for (int idx = 0; idx < vars.length; idx += 2) {
			if (!(vars[idx] instanceof String)) {
				throw new IllegalArgumentException("Expected string for variable name");
			}

			String name = (String) vars[idx];

			DualExpr val;
			Object tentVal = vars[idx + 1];

			if (tentVal instanceof Integer) {
				int vl = (int) tentVal;

				val = new DualExpr(new Dual(vl));
			} else if (tentVal instanceof Double) {
				double vl = (double) tentVal;

				val = new DualExpr(new Dual(vl));
			} else if (tentVal instanceof Dual) {
				val = new DualExpr((Dual) tentVal);
			} else if (tentVal instanceof DualExpr) {
				val = (DualExpr) tentVal;
			} else {
				throw new IllegalArgumentException(
						"Invalid argument type " + tentVal.getClass().getName());
			}

			varMap.put(name, val);
		}

		Result res1 = DualExprParser.parseExpression(inp, varMap);

		DualExpr de1 = res1.expr;

		if (printExpr)
			System.out.printf("Expression: '%s'\n", de1);

		assertEquals(res, de1.evaluate());
	}

	private static void assertDEEvalsTo(boolean printExpr, Dual res, String inp) {
		Result res1 = DualExprParser.parseExpression(inp);

		DualExpr de1 = res1.expr;

		if (printExpr)
			System.out.printf("Expression: '%s'\n", de1);

		assertEquals(res, de1.evaluate());
	}
}