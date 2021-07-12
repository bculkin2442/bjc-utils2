package bjc.utils.ioutils.format.examples;

import java.io.*;
import java.util.*;

public class CLFormatCLI {
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		PrintStream output = System.out;

		runCLI(scn, output);
	}

	private static void runCLI(Scanner input, PrintStream output) {
		output.println("CLFormat CLI v0.1");
		output.println("Enter a command (m for help)");

		String inp = input.nextLine().trim();

		boolean verboseErrors = false;

		while (!inp.equals("q")) {
			String[] args = inp.split("\\S+");

			String command = args[0];
			switch(command) {
			default:
				if (verboseErrors) {
					output.printf("! Error: %s is not a recognizable command\n", inp);
				} else {
					output.println("! UC");
				}
			}

			output.println("Enter a command (m for help)");

			String inp = input.nextLine().trim();
		}
	}
}
