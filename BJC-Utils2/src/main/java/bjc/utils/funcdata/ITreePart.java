package bjc.utils.funcdata;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.funcdata.bst.DirectedWalkFunction;

/**
 * A interface for the fundamental things that want to be part of a tree.
 * 
 * @author ben
 *
 * @param <T>
 *            The data contained in this part of the tree.
 */
public interface ITreePart<T> {
	/**
	 * Represents the ways to linearize a tree for traversal.
	 * 
	 * @author ben
	 *
	 */
	public enum TreeLinearizationMethod {
		/**
		 * Visit the left side of this tree part, the tree part itself, and
		 * then the right part.
		 */
		INORDER,
		/**
		 * Visit the left side of this tree part, the right side, and then
		 * the tree part itself.
		 */
		POSTORDER,
		/**
		 * Visit the tree part itself, then the left side of tthis tree
		 * part and then the right part.
		 */
		PREORDER
	}

	/**
	 * Add a element below this tree part somewhere.
	 * 
	 * @param dat
	 *            The element to add below this tree part
	 * @param comp
	 *            The thing to use for comparing values to find where to
	 *            insert the tree part.
	 */
	public void add(T dat, Comparator<T> comp);

	/**
	 * Collapses this tree part into a single value. Does not change the
	 * underlying tree.
	 * 
	 * @param <E>
	 *            The type of the final collapsed value
	 * 
	 * @param f
	 *            The function to use to transform data into mapped form.
	 * @param bf
	 *            The function to use to collapse data in mapped form into
	 *            a single value.
	 * @return A single value from collapsing the tree.
	 */
	public <E> E collapse(Function<T, E> f, BiFunction<E, E, E> bf);

	/**
	 * Check if this tre part or below it contains the specified data item
	 * 
	 * @param data
	 *            The data item to look for.
	 * @param cmp
	 *            The comparator to use to search for the data item
	 * @return Whether or not the given item is contained in this tree part
	 *         or its children.
	 */
	public boolean contains(T data, Comparator<T> cmp);

	/**
	 * Get the data associated with this tree part.
	 * 
	 * @return The data associated with this tree part.
	 */
	public T data();

	/**
	 * Remove the given node from this tree part and any of its children.
	 * 
	 * @param dat
	 *            The data item to remove.
	 * @param cmp
	 *            The comparator to use to search for the data item.
	 */
	public void delete(T dat, Comparator<T> cmp);

	/**
	 * Execute a directed walk through the tree.
	 * 
	 * @param ds
	 *            The function to use to direct the walk through the tree.
	 * @return Whether the directed walk finished successfully.
	 */
	public boolean directedWalk(DirectedWalkFunction<T> ds);

	/**
	 * Execute a provided function for each element of tree it succesfully
	 * completes for
	 * 
	 * @param tlm
	 *            The way to linearize the tree for executing
	 * @param c
	 *            The function to apply to each element, where it returning
	 *            false terminates traversal early
	 * @return Whether the traversal finished succesfully
	 */
	public boolean forEach(TreeLinearizationMethod tlm, Predicate<T> c);
}
