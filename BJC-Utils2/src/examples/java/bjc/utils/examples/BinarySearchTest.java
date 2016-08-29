package bjc.utils.examples;

import java.util.Scanner;

import bjc.utils.funcdata.bst.BinarySearchTree;
import bjc.utils.funcdata.bst.TreeLinearizationMethod;

/**
 * Example showing how to use the binary search tree.
 * 
 * @author ben
 *
 */
public class BinarySearchTest {
	private static void displayTree(BinarySearchTree<Character> tree,
			Scanner inputSource) {
		System.out.print(
				"What order would you like the tree to be printed in (m for options): ");

		char command;

		while (true) {
			command = inputSource.nextLine().charAt(0);

			TreeLinearizationMethod linearizationMethod = null;

			switch (command) {
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
					linearizationMethod = TreeLinearizationMethod.PREORDER;
					break;

				case 'i':
					linearizationMethod = TreeLinearizationMethod.INORDER;
					break;

				case 'o':
					linearizationMethod = TreeLinearizationMethod.POSTORDER;
					break;

				default:
					System.out.println("ERROR: Unknown command.");
			}

			if (linearizationMethod != null) {
				tree.traverse(linearizationMethod, (element) -> {
					System.out.println("Node: " + element);
					return true;
				});

				return;
			}

			System.out.print(
					"What order would you like the tree to be printed in (m for options): ");
		}
	}

	/**
	 * Main method of class
	 * 
	 * @param args
	 *            Unused CLI args
	 */
	public static void main(String[] args) {
		Scanner inputSource = new Scanner(System.in);

		System.out.println("Binary Tree Constructor/Searcher");

		char command = ' ';

		BinarySearchTree<Character> searchTree = new BinarySearchTree<>(
				(o1, o2) -> o1 - o2);

		while (command != 'e') {
			System.out.print("Enter a command (m for help): ");
			command = inputSource.nextLine().charAt(0);

			switch (command) {
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
					command = inputSource.nextLine().charAt(0);

					searchTree.addNode(command);
					break;

				case 'r':
					System.out.print(
							"Enter the letter to add to the binary tree: ");
					command = inputSource.nextLine().charAt(0);

					searchTree.deleteNode(command);
					break;

				case 'd':
					displayTree(searchTree, inputSource);
					break;

				case 'f':
					System.out.print(
							"Enter the letter to add to the binary tree: ");
					command = inputSource.nextLine().charAt(0);

					System.out.println("Node " + command + " was "
							+ (searchTree.isInTree(command) ? "" : "not ")
							+ "found");
					break;

				case 't':
					searchTree.trim();
					break;

				case 'b':
					searchTree.balance();
					break;

				default:
					System.out.println("ERROR: Unrecognized command.");
			}
		}

		inputSource.close();
	}
}
