package bjc.utils.data;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.bst.TreeLinearizationMethod;
import bjc.utils.functypes.ListFlattener;

/**
 * A node in a homogeneous tree with a unlimited amount of children.
 *
 * @author ben
 *
 * @param <ContainedType>
 *        The type of data contained in the tree nodes.
 *
 */
public interface ITree<ContainedType> {
	/**
	 * Append a child to this node.
	 *
	 * @param child
	 *        The child to append to this node.
	 */
	void addChild(ITree<ContainedType> child);

	/**
	 * Prepend a child to this node.
	 *
	 * @param child
	 *        The child to prepend to this node.
	 */
	void prependChild(ITree<ContainedType> child);

	/**
	 * Collapse a tree into a single version.
	 *
	 * @param <NewType>
	 *        The intermediate type being folded.
	 *
	 * @param <ReturnedType>
	 *        The type that is the end result.
	 *
	 * @param leafTransform
	 *        The function to use to convert leaf values.
	 *
	 * @param nodeCollapser
	 *        The function to use to convert internal nodes and their
	 *        children.
	 *
	 * @param resultTransformer
	 *        The function to use to convert a state to the returned
	 *        version.
	 *
	 * @return The final transformed state.
	 */
	<NewType, ReturnedType> ReturnedType collapse(Function<ContainedType, NewType> leafTransform,
			Function<ContainedType, ListFlattener<NewType>> nodeCollapser,
			Function<NewType, ReturnedType> resultTransformer);

	/**
	 * Execute a given action for each of this tree's children.
	 *
	 * @param action
	 *        The action to execute for each child.
	 */
	void doForChildren(Consumer<ITree<ContainedType>> action);

	/**
	 * Expand the nodes of a tree into trees, and then merge the contents of
	 * those trees into a single tree.
	 *
	 * @param mapper
	 *        The function to use to map values into trees.
	 *
	 * @return A tree, with some nodes expanded into trees.
	 */
	default ITree<ContainedType> flatMapTree(final Function<ContainedType, ITree<ContainedType>> mapper) {
		return topDownTransform(dat -> TopDownTransformResult.PUSHDOWN, node -> {
			if(node.getChildrenCount() > 0) {
				final ITree<ContainedType> parent = node.transformHead(mapper);

				node.doForChildren(parent::addChild);

				return parent;
			}

			return node.transformHead(mapper);
		});
	}

	/**
	 * Get the specified child of this tree.
	 *
	 * @param childNo
	 *        The number of the child to get.
	 *
	 * @return The specified child of this tree.
	 */
	default ITree<ContainedType> getChild(final int childNo) {
		return transformChild(childNo, child -> child);
	}

	/**
	 * Get a count of the number of direct children this node has.
	 *
	 * @return The number of direct children this node has.
	 */
	int getChildrenCount();

	/**
	 * Get the data stored in this node.
	 *
	 * @return The data stored in this node.
	 */
	default ContainedType getHead() {
		return transformHead(head -> head);
	}

	/**
	 * Rebuild the tree with the same structure, but different nodes.
	 *
	 * @param <MappedType>
	 *        The type of the new tree.
	 *
	 * @param leafTransformer
	 *        The function to use to transform leaf tokens.
	 *
	 * @param internalTransformer
	 *        The function to use to transform internal tokens.
	 *
	 * @return The tree, with the nodes changed.
	 */
	<MappedType> ITree<MappedType> rebuildTree(Function<ContainedType, MappedType> leafTransformer,
			Function<ContainedType, MappedType> internalTransformer);

	/**
	 * Transform some of the nodes in this tree.
	 *
	 * @param nodePicker
	 *        The predicate to use to pick nodes to transform.
	 *
	 * @param transformer
	 *        The function to use to transform picked nodes.
	 */
	void selectiveTransform(Predicate<ContainedType> nodePicker, UnaryOperator<ContainedType> transformer);

	/**
	 * Do a top-down transform of the tree.
	 *
	 * @param transformPicker
	 *        The function to use to pick how to progress.
	 *
	 * @param transformer
	 *        The function used to transform picked subtrees.
	 *
	 * @return The tree with the transform applied to picked subtrees.
	 */
	ITree<ContainedType> topDownTransform(Function<ContainedType, TopDownTransformResult> transformPicker,
			UnaryOperator<ITree<ContainedType>> transformer);

	/**
	 * Transform one of this nodes children.
	 *
	 * @param <TransformedType>
	 *        The type of the transformed value.
	 *
	 * @param childNo
	 *        The number of the child to transform.
	 *
	 * @param transformer
	 *        The function to use to transform the value.
	 *
	 * @return The transformed value.
	 *
	 * @throws IllegalArgumentException
	 *         if the childNo is out of bounds (0 &lt;= childNo &lt;=
	 *         childCount()).
	 */
	<TransformedType> TransformedType transformChild(int childNo,
			Function<ITree<ContainedType>, TransformedType> transformer);

	/**
	 * Transform the value that is the head of this node.
	 *
	 * @param <TransformedType>
	 *        The type of the transformed value.
	 *
	 * @param transformer
	 *        The function to use to transform the value.
	 *
	 * @return The transformed value.
	 */
	<TransformedType> TransformedType transformHead(Function<ContainedType, TransformedType> transformer);

	/**
	 * Transform the tree into a tree with a different type of token.
	 *
	 * @param <MappedType>
	 *        The type of the new tree.
	 *
	 * @param transformer
	 *        The function to use to transform tokens.
	 *
	 * @return A tree with the token types transformed.
	 */
	default <MappedType> ITree<MappedType> transformTree(final Function<ContainedType, MappedType> transformer) {
		return rebuildTree(transformer, transformer);
	}

	/**
	 * Perform an action on each part of the tree.
	 *
	 * @param linearizationMethod
	 *        The way to traverse the tree.
	 *
	 * @param action
	 *        The action to perform on each tree node.
	 */
	void traverse(TreeLinearizationMethod linearizationMethod, Consumer<ContainedType> action);

	/**
	 * Find the farthest to right child that satisfies the given predicate.
	 *
	 * @param childPred
	 *        The predicate to satisfy.
	 *
	 * @return The index of the right-most child that satisfies the
	 *         predicate, or -1 if one doesn't exist.
	 */
	int revFind(Predicate<ITree<ContainedType>> childPred);
}
