package bjc.utils.examples;

import java.util.Scanner;

import bjc.utils.funcdata.ITreePart.TreeLinearizationMethod;
import bjc.utils.funcdata.bst.BinarySearchTree;

/**
 * Example showing how to use the binary search tree.
 * 
 * @author ben
 *
 */
public class BinarySearcher {
	/**
	 * Main method of class
	 * @param args Unused CLI args
	 */
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		System.out.println("Binary Tree Constructor/Searcher");

		char c = 'n';

		BinarySearchTree<Character> bst = new BinarySearchTree<>(
				(o1, o2) -> o1 - o2);

		while (c != 'e') {
			System.out.print("Enter a command (m for help): ");
			c = s.nextLine().charAt(0);

			switch (c) {
				case 'm':
					System.out.println("Valid commands: ");
					System.out.println("\tm: Display this help message.");
					System.out.println("\te: Exit this program.");
					System.out.println(
							"\ta: Add a node to the binary tree.");
					System.out.println("\td: Display the binary tree.");
					System.out.println(
							"\tr: Remove a node from the binary tree.");
					System.out.println(
							"\tf: Check if a given node is in the binary tree.");
					System.out.println(
							"\tt: Trim all deleted nodes from the tree.");
					System.out.println(
							"\tb: Balance the tree (also trims dead nodes)");
					break;

				case 'a':
					System.out.print(
							"Enter the letter to add to the binary tree: ");
					c = s.nextLine().charAt(0);

					bst.addNode(c);
					break;
				case 'r':
					System.out.print(
							"Enter the letter to add to the binary tree: ");
					c = s.nextLine().charAt(0);

					bst.deleteNode(c);
					break;
				case 'd':
					displayTree(bst, s);
					break;
				case 'f':
					System.out.print(
							"Enter the letter to add to the binary tree: ");
					c = s.nextLine().charAt(0);

					System.out.println("Node " + c + " was "
							+ (bst.isInTree(c) ? "" : "not ") + "found");
					break;
				case 't':
					bst.trim();
					break;
				case 'b':
					bst.balance();
					break;
				default:
					System.out.println("ERROR: Unrecognized command.");
			}

		}
		s.close();
	}

	private static void displayTree(BinarySearchTree<Character> bst,
			Scanner s) {
		System.out.print(
				"What order would you like the tree to be printed in (m for options): ");

		char c;

		while (true) {
			c = s.nextLine().charAt(0);

			TreeLinearizationMethod tlm = null;

			switch (c) {
				case 'm':
					System.out.println("Possible tree printing methods: ");
					System.out.println(
							"\tp: Preorder printing (print parent first, then left & right).");
					System.out.println(
							"\ti: Inorder printing (print left first, then parent & right).");
					System.out.println(
							"\to: Postorder printing (print left first, then right & parent).");
					break;
				case 'p':
					tlm = TreeLinearizationMethod.PREORDER;
					break;
				case 'i':
					tlm = TreeLinearizationMethod.INORDER;
					break;
				case 'o':
					tlm = TreeLinearizationMethod.POSTORDER;
					break;
				default:
					System.out.println("ERROR: Unknown command.");
			}

			if (tlm != null) {
				bst.traverse(tlm, ch -> {
					System.out.println("Node: " + ch);
					return true;
				});
				return;
			}

			System.out.print(
					"What order would you like the tree to be printed in (m for options): ");
		}
	}
}
