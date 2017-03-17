package bjc.utils.data;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.bst.TreeLinearizationMethod;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A node in a homogenous tree.
 *
 * @author ben
 *
 * @param <ContainedType>
 */
public class Tree<ContainedType> implements ITree<ContainedType> {
	private ContainedType data;

	private IList<ITree<ContainedType>>	children;
	private boolean				hasChildren;
	private int				childCount	= 0;

	private int		ID;
	private static int	nextID	= 0;

	/**
	 * Create a new leaf node in a tree
	 *
	 * @param leaf
	 *                The data to store as a leaf node
	 */
	public Tree(ContainedType leaf) {
		data = leaf;

		hasChildren = false;

		ID = nextID++;
	}

	/**
	 * Create a new tree node with the specified children
	 *
	 * @param leaf
	 *                The data to hold in this node
	 * @param childrn
	 *                A list of children for this node
	 */
	public Tree(ContainedType leaf, IList<ITree<ContainedType>> childrn) {
		this(leaf);

		childCount = childrn.getSize();

		children = childrn;
	}

	/**
	 * Create a new tree node with the specified children
	 *
	 * @param leaf
	 *                The data to hold in this node
	 * @param childrn
	 *                A list of children for this node
	 */
	@SafeVarargs
	public Tree(ContainedType leaf, ITree<ContainedType>... childrn) {
		this(leaf);

		hasChildren = true;

		childCount = 0;

		children = new FunctionalList<>();

		for(ITree<ContainedType> child : childrn) {
			children.add(child);

			childCount++;
		}
	}

	@Override
	public void addChild(ITree<ContainedType> child) {
		if(hasChildren == false) {
			hasChildren = true;

			children = new FunctionalList<>();
		}

		childCount++;

		children.add(child);
	}

	@Override
	public <NewType, ReturnedType> ReturnedType collapse(Function<ContainedType, NewType> leafTransform,
			Function<ContainedType, Function<IList<NewType>, NewType>> nodeCollapser,
			Function<NewType, ReturnedType> resultTransformer) {

		return resultTransformer.apply(internalCollapse(leafTransform, nodeCollapser));
	}

	@Override
	public void doForChildren(Consumer<ITree<ContainedType>> action) {
		children.forEach(action);
	}

	@Override
	public ITree<ContainedType> flatMapTree(Function<ContainedType, ITree<ContainedType>> mapper) {
		if(hasChildren) {
			ITree<ContainedType> flatMappedData = mapper.apply(data);

			children.map((child) -> child.flatMapTree(mapper))
					.forEach((child) -> flatMappedData.addChild(child));

			return flatMappedData;
		}

		return mapper.apply(data);
	}

	@Override
	public int getChildrenCount() {
		return childCount;
	}

	protected <NewType> NewType internalCollapse(Function<ContainedType, NewType> leafTransform,
			Function<ContainedType, Function<IList<NewType>, NewType>> nodeCollapser) {
		if(hasChildren) {
			Function<IList<NewType>, NewType> nodeTransformer = nodeCollapser.apply(data);

			IList<NewType> collapsedChildren = children.map((child) -> {
				return child.collapse(leafTransform, nodeCollapser, (subTreeVal) -> subTreeVal);
			});

			return nodeTransformer.apply(collapsedChildren);
		}

		return leafTransform.apply(data);
	}

	protected void internalToString(StringBuilder builder, int indentLevel, boolean initial) {
		for(int i = 0; i < indentLevel; i++) {
			builder.append(">\t");
		}

		builder.append("Node #");
		builder.append(ID);
		builder.append(": ");
		builder.append(data == null ? "(null)" : data.toString());
		builder.append("\n");

		if(hasChildren) {
			children.forEach((child) -> {
				((Tree<ContainedType>) child).internalToString(builder, indentLevel + 1, false);
			});
		}
	}

	@Override
	public <MappedType> ITree<MappedType> rebuildTree(Function<ContainedType, MappedType> leafTransformer,
			Function<ContainedType, MappedType> operatorTransformer) {
		if(hasChildren) {
			IList<ITree<MappedType>> mappedChildren = children.map((child) -> {
				return child.rebuildTree(leafTransformer, operatorTransformer);
			});

			return new Tree<>(operatorTransformer.apply(data), mappedChildren);
		}

		return new Tree<>(leafTransformer.apply(data));
	}

	@Override
	public void selectiveTransform(Predicate<ContainedType> nodePicker, UnaryOperator<ContainedType> transformer) {
		if(hasChildren) {
			children.forEach((child) -> child.selectiveTransform(nodePicker, transformer));
		} else {
			data = transformer.apply(data);
		}
	}

	@Override
	public ITree<ContainedType> topDownTransform(Function<ContainedType, TopDownTransformResult> transformPicker,
			UnaryOperator<ITree<ContainedType>> transformer) {
		TopDownTransformResult transformResult = transformPicker.apply(data);

		switch(transformResult) {
		case PASSTHROUGH:
			ITree<ContainedType> result = new Tree<>(data);

			if(hasChildren) {
				children.forEach((child) -> {
					result.addChild(child.topDownTransform(transformPicker, transformer));
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
				children.forEach((child) -> {
					result.addChild(child.topDownTransform(transformPicker, transformer));
				});
			}

			return transformer.apply(result);
		case PULLUP:
			ITree<ContainedType> intermediateResult = transformer.apply(this);

			result = new Tree<>(intermediateResult.getHead());

			intermediateResult.doForChildren((child) -> {
				result.addChild(child.topDownTransform(transformPicker, transformer));
			});

			return result;
		default:
			throw new IllegalArgumentException("Recieved unknown transform result " + transformResult);

		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		internalToString(builder, 1, true);

		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	@Override
	public <TransformedType> TransformedType transformChild(int childNo,
			Function<ITree<ContainedType>, TransformedType> transformer) {
		if(childNo < 0 || childNo > childCount - 1)
			throw new IllegalArgumentException("Child index #" + childNo + " is invalid");

		return transformer.apply(children.getByIndex(childNo));
	}

	@Override
	public <TransformedType> TransformedType transformHead(Function<ContainedType, TransformedType> transformer) {
		return transformer.apply(data);
	}

	@Override
	public <MappedType> ITree<MappedType> transformTree(Function<ContainedType, MappedType> transformer) {
		if(hasChildren) {
			IList<ITree<MappedType>> transformedChildren = children
					.map((child) -> child.transformTree(transformer));

			return new Tree<>(transformer.apply(data), transformedChildren);
		}

		return new Tree<>(transformer.apply(data));
	}

	@Override
	public void traverse(TreeLinearizationMethod linearizationMethod, Consumer<ContainedType> action) {
		if(hasChildren) {
			switch(linearizationMethod) {
			case INORDER:
				if(childCount != 2) throw new IllegalArgumentException(
						"Can only do in-order traversal for binary trees.");

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + childCount;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;

		Tree<?> other = (Tree<?>) obj;

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
