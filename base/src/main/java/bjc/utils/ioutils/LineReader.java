package bjc.utils.ioutils;

import java.util.Scanner;

public class LineReader implements AutoCloseable {
	private Scanner scn;

	@Override
	public void close() {
		scn.close();
	}
}
