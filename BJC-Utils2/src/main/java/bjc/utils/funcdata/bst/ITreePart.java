package bjc.utils.funcdata.bst;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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
	 * Add a element below this tree part somewhere.
	 * 
	 * @param element
	 *            The element to add below this tree part
	 * @param comparator
	 *            The thing to use for comparing values to find where to
	 *            insert the tree part.
	 */
	public void add(T element, Comparator<T> comparator);

	/**
	 * Collapses this tree part into a single value. Does not change the
	 * underlying tree.
	 * 
	 * @param <E>
	 *            The type of the final collapsed value
	 * 
	 * @param nodeCollapser
	 *            The function to use to transform data into mapped form.
	 * @param branchCollapser
	 *            The function to use to collapse data in mapped form into
	 *            a single value.
	 * @return A single value from collapsing the tree.
	 */
	public <E> E collapse(Function<T, E> nodeCollapser,
			BiFunction<E, E, E> branchCollapser);

	/**
	 * Check if this tre part or below it contains the specified data item
	 * 
	 * @param element
	 *            The data item to look for.
	 * @param comparator
	 *            The comparator to use to search for the data item
	 * @return Whether or not the given item is contained in this tree part
	 *         or its children.
	 */
	public boolean contains(T element, Comparator<T> comparator);

	/**
	 * Get the data associated with this tree part.
	 * 
	 * @return The data associated with this tree part.
	 */
	public T data();

	/**
	 * Remove the given node from this tree part and any of its children.
	 * 
	 * @param element
	 *            The data item to remove.
	 * @param comparator
	 *            The comparator to use to search for the data item.
	 */
	public void delete(T element, Comparator<T> comparator);

	/**
	 * Execute a directed walk through the tree.
	 * 
	 * @param treeWalker
	 *            The function to use to direct the walk through the tree.
	 * @return Whether the directed walk finished successfully.
	 */
	public boolean directedWalk(DirectedWalkFunction<T> treeWalker);

	/**
	 * Execute a provided function for each element of tree it succesfully
	 * completes for
	 * 
	 * @param linearizationMethod
	 *            The way to linearize the tree for executing
	 * @param traversalPredicate
	 *            The function to apply to each element, where it returning
	 *            false terminates traversal early
	 * @return Whether the traversal finished succesfully
	 */
	public boolean forEach(TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate);
}
