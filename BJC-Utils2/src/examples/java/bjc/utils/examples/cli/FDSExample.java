package bjc.utils.examples.cli;

import bjc.utils.cli.fds.FDS;
import bjc.utils.cli.fds.FDSException;
import bjc.utils.cli.fds.FDSMode;
import bjc.utils.cli.fds.SimpleFDSMode;

/**
 * Simple example for FDS.
 * 
 * @author bjculkin
 *
 */
public class FDSExample {
	/**
	 * Main method.
	 * 
	 * @param args
	 *                Unused CLI arguments.
	 */
	public static void main(String[] args) {
		System.out.println("Entering rudimentary FDS");
		System.out.println();

		FDSMode<TestContext> testMode = new SimpleFDSMode<>();
		TestContext ctx = new TestContext();

		try {
			FDS.runFDS(System.in, System.in, System.out, testMode, ctx);
		} catch (FDSException fex) {
			fex.printStackTrace();
		}

		System.out.println();
		System.out.println("Exiting FDS");
	}
}
