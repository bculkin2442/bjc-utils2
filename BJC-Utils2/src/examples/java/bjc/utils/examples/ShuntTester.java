package bjc.utils.examples;

import java.util.Scanner;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.ShuntingYard;

public class ShuntTester {
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a expression to shunt: ");
		String ln = scn.nextLine();

		ShuntingYard<String> yard = new ShuntingYard<>();

		FunctionalList<String> ls = yard.postfix(
				new FunctionalStringTokenizer(ln).toList((s) -> s),
				(s) -> s);

		System.out.println(ls.toString());

		scn.close();
	}
}
