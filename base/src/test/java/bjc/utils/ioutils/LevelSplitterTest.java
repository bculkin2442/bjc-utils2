package bjc.utils.ioutils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static bjc.utils.funcutils.TestUtils.*;
import static bjc.utils.ioutils.LevelSplitterTest.RXPair.pair;

/**
 * Test of LevelSplitter.
 * 
 * @author bjculkin
 *
 */
public class LevelSplitterTest {
	static final class RXPair {
		public String inp;
		public String[] outp;

		public RXPair(String inp, String... outp) {
			this.inp = inp;
			this.outp = outp;
		}

		public static RXPair pair(String inp, String... outp) {
			return new RXPair(inp, outp);
		}
	}

	/**
	 * Test regex splitter.
	 */
	@Test
	public void testRXSplit() {
		LevelSplitter splitter = LevelSplitter.def;

		// Check generic splitting works
		assertRXSplit("\\s+", pair("", ""), pair("a", "a"), pair("a b", "a", "b"), pair("a  b", "a", "b"),
				pair("a\t \tb", "a", "b"));
	}

	private static void assertRXSplit(String pat, RXPair... pairs) {
		for (RXPair pair : pairs) {
			assertListEquals(LevelSplitter.def.levelSplitRX(pair.inp, pat), pair.outp);
		}
	}
}
