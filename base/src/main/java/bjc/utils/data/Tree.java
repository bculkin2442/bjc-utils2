package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.bst.TreeLinearizationMethod;

/**
 * A node in a homogeneous tree.
 *
 * @author ben
 *
 * @param <ContainedType>
 *        The type contained in the tree.
 */
public class Tree<ContainedType> implements ITree<ContainedType> {
	/* The data/label for this node. */
	private ContainedType data;

	/* The children of this node. */
	private IList<ITree<ContainedType>> children;

	/* Whether this node has children. */
	/*
	 * @NOTE Why have both this boolean and childCount? Why not just do a
	 * childCount == 0 whenever you'd check hasChildren?
	 * 	- Because hasChildren is set once and not reset, and really what
	 * 		it indicates is that children has been allocated.
	 */
	private boolean hasChildren;
	/* The number of children this node has. */
	private int childCount = 0;

	/* The ID of this node. */
	private int ID;
	/* The next ID to assign to a node. */
	private static int nextID = 0;

	/**
	 * Create a new leaf node in a tree.
	 */
	public Tree() {
		this(null);
	}

	/**
	 * Create a new leaf node in a tree.
	 *
	 * @param leaf
	 *        The data to store as a leaf node.
	 */
	public Tree(final ContainedType leaf) {
		data = leaf;

		hasChildren = false;

		ID = nextID++;
	}

	/**
	 * Create a new tree node with the specified children.
	 *
	 * @param leaf
	 *        The data to hold in this node.
	 *
	 * @param childrn
	 *        A list of children for this node.
	 */
	public Tree(final ContainedType leaf, final IList<ITree<ContainedType>> childrn) {
		this(leaf);

		hasChildren = true;

		childCount = childrn.getSize();

		children = childrn;
	}

	/**
	 * Create a new tree node with the specified children.
	 *
	 * @param leaf
	 *        The data to hold in this node.
	 *
	 * @param childrn
	 *        A list of children for this node.
	 */
	@SafeVarargs
	public Tree(final ContainedType leaf, final ITree<ContainedType>... childrn) {
		this(leaf);

		hasChildren = true;

		childCount = 0;

		children = new FunctionalList<>();

		for(final ITree<ContainedType> child : childrn) {
			children.add(child);

			childCount++;
		}
	}

	@Override
	public void addChild(final ContainedType child) {
		addChild(new Tree(child));
	}

	@Override
	public void addChild(final ITree<ContainedType> child) {
		if(hasChildren == false) {
			hasChildren = true;

			children = new FunctionalList<>();
		}

		childCount++;

		children.add(child);
	}

	@Override
	public void prependChild(final ITree<ContainedType> child) {
		if(hasChildren == false) {
			hasChildren = true;

			children = new FunctionalList<>();
		}

		childCount++;

		children.prepend(child);
	}

	@Override
	public void doForChildren(final Consumer<ITree<ContainedType>> action) {
		if(childCount > 0) {
			children.forEach(action);
		}
	}

	@Override
	public int getChildrenCount() {
		return childCount;
	}

	@Override
	public int revFind(final Predicate<ITree<ContainedType>> childPred) {
		if(childCount == 0) {
			return -1;
		}

		for(int i = childCount - 1; i >= 0; i--) {
			if(childPred.test(getChild(i))) return i;
		}

		return -1;
	}

	@Override
	public void traverse(final TreeLinearizationMethod linearizationMethod, final Consumer<ContainedType> action) {
		if(hasChildren) {
			switch(linearizationMethod) {
			case INORDER:
				if(childCount != 2) {
					final String msg = "Can only do in-order traversal for binary trees.";

					throw new IllegalArgumentException(msg);
				}

				children.getByIndex(0).traverse(linearizationMethod, action);

				action.accept(data);

				children.getByIndex(1).traverse(linearizationMethod, action);
				break;
			case POSTORDER:
				children.forEach((child) -> child.traverse(linearizationMethod, action));

				action.accept(data);
				break;
			case PREORDER:
				action.accept(data);

				children.forEach((child) -> child.traverse(linearizationMethod, action));
				break;
			default:
				break;

			}
		} else {
			action.accept(data);
		}
	}

	@Override
	public <NewType, ReturnedType> ReturnedType collapse(final Function<ContainedType, NewType> leafTransform,
			final BiFunction<ContainedType, IList<NewType>, NewType> nodeCollapser,
			final Function<NewType, ReturnedType> resultTransformer) {
		return resultTransformer.apply(internalCollapse(leafTransform, nodeCollapser));
	}

	@Override
	public ITree<ContainedType> flatMapTree(final Function<ContainedType, ITree<ContainedType>> mapper) {
		if(hasChildren) {
			final ITree<ContainedType> flatMappedData = mapper.apply(data);

			final IList<ITree<ContainedType>> mappedChildren = children
					.map(child -> child.flatMapTree(mapper));

			mappedChildren.forEach(flatMappedData::addChild);

			return flatMappedData;
		}

		return mapper.apply(data);
	}

	/*
	 * Do a collapse of this tree.
	 *
	 * @NOTE Why is this protected? I can't see any good reason someone'd
	 * want to override it.
	 */

	protected <NewType> NewType internalCollapse(final Function<ContainedType, NewType> leafTransform,
			final BiFunction<ContainedType, IList<NewType>, NewType> nodeCollapser) {
		if(hasChildren) {
			final IList<NewType> collapsedChildren = children.map(child -> {
				final NewType collapsed = child.collapse(leafTransform, nodeCollapser,
						subTreeVal -> subTreeVal);

				return collapsed;
			});

			return nodeCollapser.apply(data, collapsedChildren);
		}

		return leafTransform.apply(data);
	}

	protected void internalToString(final StringBuilder builder, final int indentLevel, final boolean initial) {
		if(!initial) {
			for(int i = 0; i < indentLevel; i++) {
				builder.append(">\t");
			}
		}

		builder.append("Node #");
		builder.append(ID);
		builder.append(": ");
		builder.append(data == null ? "(null)" : data.toString());
		builder.append("\n");

		if(hasChildren) {
			children.forEach(child -> {
				if(child instanceof Tree<?>) {
					final Tree<ContainedType> kid = (Tree<ContainedType>) child;

					kid.internalToString(builder, indentLevel + 1, false);
				} else {
					for(int i = 0; i < indentLevel + 1; i++) {
						builder.append(">\t");
					}

					builder.append("Unknown node of type ");
					builder.append(child.getClass().getName());
					builder.append("\n");
				}
			});
		}
	}

	@Override
	public <MappedType> ITree<MappedType> rebuildTree(final Function<ContainedType, MappedType> leafTransformer,
			final Function<ContainedType, MappedType> operatorTransformer) {
		if(hasChildren) {
			final IList<ITree<MappedType>> mappedChildren = children.map(child -> {
				return child.rebuildTree(leafTransformer, operatorTransformer);
			});

			final MappedType mapData = operatorTransformer.apply(data);
			return new Tree<>(mapData, mappedChildren);
		}

		return new Tree<>(leafTransformer.apply(data));
	}

	@Override
	public void selectiveTransform(final Predicate<ContainedType> nodePicker,
			final UnaryOperator<ContainedType> transformer) {
		if(hasChildren) {
			children.forEach(child -> child.selectiveTransform(nodePicker, transformer));
		} else {
			data = transformer.apply(data);
		}
	}

	@Override
	public ITree<ContainedType> topDownTransform(
			final Function<ContainedType, TopDownTransformResult> transformPicker,
			final UnaryOperator<ITree<ContainedType>> transformer) {
		final TopDownTransformResult transformResult = transformPicker.apply(data);

		switch(transformResult) {
		case PASSTHROUGH:
			ITree<ContainedType> result = new Tree<>(data);

			if(hasChildren) {
				children.forEach(child -> {
					final ITree<ContainedType> kid = child.topDownTransform(transformPicker,
							transformer);

					result.addChild(kid);
				});
			}

			return result;
		case SKIP:
			return this;
		case TRANSFORM:
			return transformer.apply(this);
		case RTRANSFORM:
			return transformer.apply(this).topDownTransform(transformPicker, transformer);
		case PUSHDOWN:
			result = new Tree<>(data);

			if(hasChildren) {
				children.forEach(child -> {
					final ITree<ContainedType> kid = child.topDownTransform(transformPicker,
							transformer);

					result.addChild(kid);
				});
			}

			return transformer.apply(result);
		case PULLUP:
			final ITree<ContainedType> intermediateResult = transformer.apply(this);

			result = new Tree<>(intermediateResult.getHead());

			intermediateResult.doForChildren(child -> {
				final ITree<ContainedType> kid = child.topDownTransform(transformPicker, transformer);

				result.addChild(kid);
			});

			return result;
		default:
			final String msg = String.format("Recieved unknown transform result type %s", transformResult);

			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public <TransformedType> TransformedType transformChild(final int childNo,
			final Function<ITree<ContainedType>, TransformedType> transformer) {
		if(childNo < 0 || childNo > childCount - 1) {
			final String msg = String.format("Child index #%d is invalid", childNo);

			throw new IllegalArgumentException(msg);
		}

		final ITree<ContainedType> selectedKid = children.getByIndex(childNo);

		return transformer.apply(selectedKid);
	}

	@Override
	public <TransformedType> TransformedType transformHead(
			final Function<ContainedType, TransformedType> transformer) {
		return transformer.apply(data);
	}

	@Override
	public void setHead(ContainedType dat) {
		this.data = dat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + childCount;
		result = prime * result + (children == null ? 0 : children.hashCode());
		result = prime * result + (data == null ? 0 : data.hashCode());

		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		internalToString(builder, 1, true);

		/* Delete a trailing nl. */
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Tree<?>)) return false;

		final Tree<?> other = (Tree<?>) obj;

		if(data == null) {
			if(other.data != null) return false;
		} else if(!data.equals(other.data)) return false;

		if(childCount != other.childCount) return false;

		if(children == null) {
			if(other.children != null) return false;
		} else if(!children.equals(other.children)) return false;

		return true;
	}
}
