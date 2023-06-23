package bjc.utils.test.ioutils.blocks;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import bjc.utils.ioutils.blocks.MLPromptBlockReader;

public class MLPromptBlockReaderTest {

	@Test
	public void test() {
		StringReader reader = new StringReader("");
		StringWriter writer = new StringWriter();
		
		MLPromptBlockReader blocker = new MLPromptBlockReader("%1:%2> ", "%3... ", reader, writer);
	}

}
