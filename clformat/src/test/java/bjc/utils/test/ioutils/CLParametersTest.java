package bjc.utils.test.ioutils;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

import org.junit.*;

import static bjc.utils.ioutils.format.CLParameters.fromDirective;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class CLParametersTest {
	@Test
	public void testBlankParams() {
		CLParameters params = new CLParameters();

		assertEquals("Blank parameters have 0 length", 0, params.length());
	}

	@Test
	public void testBasicParsing() {
		Tape<Object> scratch = new SingleTape<>();

		{
			CLParameters params = fromDirective("");
			assertEquals("A blank string gives blank parameters", 0, params.length());
		}

		{
			CLParameters params = fromDirective("1,2");
			assertEquals("Positionals are counted right", 2, params.length());

			CLValue val1 = params.getByIndex(0);
			CLValue val2 = params.getByIndex(1);

			assertEquals("First integer reads correctly", "1", val1.getValue(scratch));
			assertEquals("Second integer reads correctly", "2", val2.getValue(scratch));
		}	

		{
			CLParameters params = fromDirective("'a,\"abc\"");
			assertEquals("Chars/strings are counted right", 2, params.length());

			CLValue val1 = params.getByIndex(0);
			CLValue val2 = params.getByIndex(1);

			assertEquals("Character reads correctly", "'a", val1.getValue(scratch));
			assertEquals("String reads correctly", "abc", val2.getValue(scratch));
		}

		{
			CLParameters params = fromDirective("1,2,3");
			params.mapIndices("arg1","arrg2","arg3");

			CLValue val1 = params.resolveKey("arg1");
			CLValue val2 = params.resolveKey("arr");
			CLValue val3 = params.resolveKey("arg3");

			assertNotNull("1st arg not null", val1);
			assertNotNull("2nd arg not null", val2);
			assertNotNull("3rd arg not null", val3);

			assertEquals("Named parameter works right", "1", val1.getValue(scratch));
			assertEquals("Parameter abbreviation works", "2", val2.getValue(scratch));
			assertEquals("Last named param works", "3", val3.getValue(scratch));
		}

		{
			CLParameters params = fromDirective("#arg1:1,#arg2;3,4");
			params.mapIndices("altarg2", "arg3");

			CLValue val1 = params.resolveKey("arg1");
			CLValue val2 = params.resolveKey("altarg");
			CLValue val3 = params.resolveKey("arg3");

			assertNotNull("1st arg not null", val1);
			assertNotNull("2nd arg not null", val2);
			assertNotNull("3rd arg not null", val3);

			assertEquals("Non-indexed name parameters work", "1", val1.getValue(scratch));
			assertEquals("Indexed name parameters work", "3", val2.getValue(scratch));
			assertEquals("Mixed parameters work", "4", val3.getValue(scratch));
		}
	}
}
