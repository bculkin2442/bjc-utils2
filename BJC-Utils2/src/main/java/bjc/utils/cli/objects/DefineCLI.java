package bjc.utils.cli.objects;

public class DefineCLI {
	public static void main(String[] args) {

	}

	/**
	 * Run the CLI on an input source.
	 *
	 * @param input
	 * 	The place to read input from.
	 * @param ioSource
	 * 	The name of the place to read input from.
	 * @param interactive
	 *	Whether or not the source is interactive
	 */
	public void run(Scanner input, String ioSource, boolean interactive) {
		int lno = 0;
		while(input.hasNextLine()) {
			if(interactive)
				System.out.printf("define-conf(%d)>", lno);

			String ln = input.nextLine();

			lno += 1;

			Command com = Command.fromString(ln, lno, ioSource);
			if(com == null) continue;

			handleCommand(com, interactive);
		}

		input.close();
	}

	public void handleCommand(Command com, boolean interactive) {

	}
}
