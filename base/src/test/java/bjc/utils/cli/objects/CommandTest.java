package bjc.utils.cli.objects;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test that CLI command objects work correctly.
 * 
 * @author bjculkin
 *
 */
public class CommandTest {

	/**
	 * Test basic things work right.
	 */
	@Test
	public void testBasic() {
		Command com = Command.fromString("a b c", 1, "console");
		
		assertEquals("a b c", com.full);
		assertEquals("a", com.name);
		assertEquals("b c", com.remn);
		
		assertEquals("b", com.trimTo(' '));
		assertEquals("c", com.remn);
	}
	
	/**
	 * Test regex trimming works right.
	 */
	@Test
	public void testRX() {
		Command com = Command.fromString("a try1ZZZtry2ZZtry3", 1, "console");
		
		assertEquals("try1", com.trimToRX("Z+"));
		assertEquals("try2ZZtry3", com.remn);
		
		assertEquals("try2", com.trimToRX("Z+"));
		assertEquals("try3", com.remn);
	}
}
