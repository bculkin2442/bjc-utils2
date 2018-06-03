package bjc.utils.data;

import static bjc.utils.data.TopDownTransformResult.RTRANSFORM;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * @TODO 10/11/17 Ben Culkin :TopDownStep
 * 
 * 	Figure out what is broken with this, and fix it so that step-wise
 * 	iteration works correctly.
 */

/**
 * An iterative top-down transform of a tree.
 * 
 * @author EVE
 *
 * @param <ContainedType>
 *            The type of the nodes in the tree.
 */
public class TopDownTransformIterator<ContainedType> implements Iterator<ITree<ContainedType>> {
	/**
	 * Alias type for a tree transformation.
	 * 
	 * @author student
	 *
	 * @param <ContainedType>
	 *            The type contained in the tree.
	 */
	public interface TreeTransform<ContainedType>
			extends BiFunction<ITree<ContainedType>, Consumer<Iterator<ITree<ContainedType>>>, ITree<ContainedType>> {

	}

	/*
	 * The function that picks how to transform a given node
	 */
	private final Function<ContainedType, TopDownTransformResult> picker;
	/*
	 * The transform to apply to a given node.
	 */
	private final TreeTransform<ContainedType> transform;

	private ITree<ContainedType> preParent;
	private ITree<ContainedType> postParent;

	private final Deque<ITree<ContainedType>> preChildren;
	private final Deque<ITree<ContainedType>> postChildren;

	private TopDownTransformIterator<ContainedType> curChild;

	private boolean done;
	private boolean initial;

	private final Deque<Iterator<ITree<ContainedType>>> toYield;
	private Iterator<ITree<ContainedType>> currYield;

	/**
	 * Create a new tree iterator.
	 * 
	 * @param pickr
	 *            The function to use to pick how to process nodes.
	 * @param transfrm
	 *            The transform to apply to the nodes.
	 * @param tree
	 *            The tree to transform.
	 */
	public TopDownTransformIterator(final Function<ContainedType, TopDownTransformResult> pickr,
			final TreeTransform<ContainedType> transfrm, final ITree<ContainedType> tree) {
		preParent = tree;

		preChildren  = new LinkedList<>();
		postChildren = new LinkedList<>();
		toYield      = new LinkedList<>();

		picker    = pickr;
		transform = transfrm;

		done    = false;
		initial = true;
	}

	/**
	 * Add a set of nodes to yield.
	 * 
	 * @param src
	 *            The nodes to yield.
	 */
	public void addYield(final Iterator<ITree<ContainedType>> src) {
		if (currYield != null) {
			toYield.push(currYield);
		}

		currYield = src;
	}

	@Override
	public boolean hasNext() {
		return !done;
	}

	/**
	 * Get the next yielded value.
	 * 
	 * @param val
	 *            The sentinel value to yield.
	 * 
	 * @return The next yielded value.
	 */
	public ITree<ContainedType> flushYields(final ITree<ContainedType> val) {
		if (currYield != null) {
			/*
			 * We have non-sentinel values to yield.
			 */

			/*
			 * Add the sentinel to yield later.
			 */
			toYield.add(new SingleIterator<>(val));

			if (currYield.hasNext()) {
				return currYield.next();
			}

			while (toYield.size() != 0 && !currYield.hasNext()) {
				currYield = toYield.pop();
			}

			if (toYield.size() == 0 && !currYield.hasNext()) {
				currYield = null;
				return val;
			}

			return currYield.next();
		}

		return val;
	}

	@Override
	public ITree<ContainedType> next() {
		if (done)
			throw new NoSuchElementException();

		/*
		 * Flush any values that need to be yielded.
		 */
		if (currYield != null) {
			ITree<ContainedType> yeld = flushYields(null);

			if (yeld != null)
				return yeld;
		}
		
		if (initial) {
			/*
			 * Get the way we are transforming.
			 */
			final TopDownTransformResult res = picker.apply(preParent.getHead());

			switch (res) {
			case PASSTHROUGH:
				postParent = new Tree<>(preParent.getHead());

				if (preParent.getChildrenCount() != 0) {
					for (int i = 0; i < preParent.getChildrenCount(); i++) {
						preChildren.add(preParent.getChild(i));
					}

					// Return whatever the first child is
					break;
				}

				done = true;
				return flushYields(postParent);
			case SKIP:
				done = true;
				return flushYields(preParent);
			case TRANSFORM:
				done = true;
				return flushYields(transform.apply(preParent, this::addYield));
			case RTRANSFORM:
				preParent = transform.apply(preParent, this::addYield);
				return flushYields(preParent);
			case PUSHDOWN:
				if (preParent.getChildrenCount() != 0) {
					for (int i = 0; i < preParent.getChildrenCount(); i++) {
						preChildren.add(preParent.getChild(i));
					}

					// Return whatever the first child is
					break;
				}

				done = true;
				return flushYields(transform.apply(new Tree<>(preParent.getHead()), this::addYield));
			case PULLUP:
				final ITree<ContainedType> intRes = transform.apply(preParent, this::addYield);

				postParent = new Tree<>(intRes.getHead());

				if (intRes.getChildrenCount() != 0) {
					for (int i = 0; i < intRes.getChildrenCount(); i++) {
						preChildren.add(intRes.getChild(i));
					}

					// Return whatever the first child is
					break;
				}

				done = true;
				return flushYields(postParent);
			default:
				throw new IllegalArgumentException("Unknown result type " + res);
			}

			if (res != RTRANSFORM) {
				initial = false;
			}
		}

		if (curChild == null || !curChild.hasNext()) {
			if (preChildren.size() != 0) {
				curChild = new TopDownTransformIterator<>(picker, transform, preChildren.pop());

				final ITree<ContainedType> res = curChild.next();
				//System.out.println("\t\tTRACE: adding node " + res + " to children");
				postChildren.add(res);

				return flushYields(res);
			}

			ITree<ContainedType> res = null;

			if (postParent == null) {
				res = new Tree<>(preParent.getHead());

				//System.out.println("\t\tTRACE: adding nodes " + postChildren + " to " + res);

				for (final ITree<ContainedType> child : postChildren) {
					res.addChild(child);
				}

				// res = transform.apply(res,
				// this::addYield);
			} else {
				res = postParent;

				//System.out.println("\t\tTRACE: adding nodes " + postChildren + " to " + res);
				for (final ITree<ContainedType> child : postChildren) {
					res.addChild(child);
				}
			}

			done = true;
			return flushYields(res);
		}

		final ITree<ContainedType> res = curChild.next();
		//System.out.println("\t\tTRACE: adding node " + res + " to children");
		postChildren.add(res);

		return flushYields(res);
	}
}
