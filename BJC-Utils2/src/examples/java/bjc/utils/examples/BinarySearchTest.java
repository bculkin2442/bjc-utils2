package bjc.utils.examples;

import bjc.utils.funcdata.bst.BinarySearchTree;
import bjc.utils.funcdata.bst.TreeLinearizationMethod;

import java.util.Scanner;

/**
 * Example showing how to use the binary search tree.
 *
 * @author ben
 *
 */
public class BinarySearchTest {
	private static void display(BinarySearchTree<Character> tree, Scanner input) {
		System.out.print("What order would you like the tree to be printed in (m for options): ");
		char command;

		while(true) {
			command = input.nextLine().charAt(0);
			TreeLinearizationMethod method = null;

			switch(command) {
			case 'm':
				System.out.println("Possible tree printing methods: ");
				System.out.println("\tp: Preorder printing (print parent first, then left & right).");
				System.out.println("\ti: Inorder printing (print left first, then parent & right).");
				System.out.println("\to: Postorder printing (print left first, then right & parent).");
				break;
			case 'p':
				method = TreeLinearizationMethod.PREORDER;
				break;
			case 'i':
				method = TreeLinearizationMethod.INORDER;
				break;
			case 'o':
				method = TreeLinearizationMethod.POSTORDER;
				break;
			default:
				System.out.println("ERROR: Unknown command.");
			}

			if(method != null) {
				tree.traverse(method, (element) -> {
					System.out.println("Node: " + element);
					return true;
				});

				return;
			}

			System.out.print("What order would you like the tree to be printed in (m for options): ");
		}
	}

	/**
	 * Main method of class
	 *
	 * @param args
	 *                Unused CLI args
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Binary Tree Constructor/Searcher");
		BinarySearchTree<Character> tree = new BinarySearchTree<>((o1, o2) -> o1 - o2);

		char command = ' ';
		while(command != 'e') {
			System.out.print("Enter a command (m for help): ");
			command = input.nextLine().charAt(0);

			switch(command) {
			case 'm':
				System.out.println("Valid commands: ");
				System.out.println("\tm: Display this help message.");
				System.out.println("\te: Exit this program.");
				System.out.println("\ta: Add a node to the binary tree.");
				System.out.println("\td: Display the binary tree.");
				System.out.println("\tr: Remove a node from the binary tree.");
				System.out.println("\tf: Check if a given node is in the binary tree.");
				System.out.println("\tt: Trim all deleted nodes from the tree.");
				System.out.println("\tb: Balance the tree (also trims dead nodes)");
				break;
			case 'a':
				System.out.print("Enter the letter to add to the binary tree: ");
				command = input.nextLine().charAt(0);

				tree.addNode(command);
				break;
			case 'r':
				System.out.print("Enter the letter to add to the binary tree: ");
				command = input.nextLine().charAt(0);

				tree.deleteNode(command);
				break;
			case 'd':
				display(tree, input);
				break;
			case 'f':
				System.out.print("Enter the letter to add to the binary tree: ");
				command = input.nextLine().charAt(0);

				boolean inTree = tree.isInTree(command);
				if(inTree) {
					System.out.printf("Node %s was found\n", command);
				} else {
					System.out.printf("Node %s was not found\n", command);
				}
				break;
			case 't':
				tree.trim();
				break;
			case 'b':
				tree.balance();
				break;
			default:
				System.out.println("ERROR: Unrecognized command.");
			}
		}

		input.close();
	}
}
