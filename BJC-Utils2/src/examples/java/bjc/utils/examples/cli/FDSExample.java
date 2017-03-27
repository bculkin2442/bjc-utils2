package bjc.utils.examples.cli;

import java.io.InputStreamReader;

import bjc.utils.cli.fds.FDS;
import bjc.utils.cli.fds.FDSException;
import bjc.utils.cli.fds.FDSMode;
import bjc.utils.cli.fds.FDSState;
import bjc.utils.cli.fds.SimpleFDSMode;
import bjc.utils.cli.fds.FDSState.InputMode;
import bjc.utils.ioutils.BlockReader;
import bjc.utils.ioutils.PushbackBlockReader;

import static bjc.utils.ioutils.BlockReaders.*;

/**
 * Simple example for FDS.
 * 
 * @author bjculkin
 *
 */
public class FDSExample {
	private static final class Prompter implements Runnable {
		@Override
		public void run() {
			System.out.print("Enter a command (m to exit): ");
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Unused CLI arguments.
	 */
	public static void main(String[] args) {
		System.out.println("Entering rudimentary FDS");
		System.out.println();

		FDSMode<TestContext> testMode = new SimpleFDSMode<>();
		TestContext ctx = new TestContext();

		InputStreamReader reader = new InputStreamReader(System.in);

		try {
			BlockReader input = simple("\\R", reader);

			Prompter comPrompter = new Prompter();
			Prompter dataPrompter = new Prompter();

			BlockReader rawComInput = trigger(input, comPrompter);
			BlockReader rawDataInput = trigger(input, dataPrompter);

			PushbackBlockReader comInput = pushback(rawComInput);
			PushbackBlockReader dataInput = pushback(rawDataInput);

			FDSState<TestContext> fdsState = new FDSState<>(ctx, InputMode.CHORD, comInput::addBlock,
					dataInput::addBlock);
			fdsState.modes.push(testMode);

			FDS.runFDS(comInput, dataInput, System.out, fdsState);
		} catch (FDSException fex) {
			fex.printStackTrace();
		}

		System.out.println();
		System.out.println("Exiting FDS");
	}
}
