package bjc.utils.data;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static bjc.utils.data.TopDownTransformResult.*;

public class TopDownTransformIterator<ContainedType> implements Iterator<ITree<ContainedType>> {
	private Function<ContainedType, TopDownTransformResult> picker;
	private BiFunction<ITree<ContainedType>, 
			Consumer<Iterator<ITree<ContainedType>>>,
			ITree<ContainedType>>                           transform;

	private ITree<ContainedType> preParent;
	private ITree<ContainedType> postParent;
	
	private Deque<ITree<ContainedType>> preChildren;
	private Deque<ITree<ContainedType>> postChildren;

	private TopDownTransformIterator<ContainedType> curChild;

	private boolean done;
	private boolean initial;

	private Deque<Iterator<ITree<ContainedType>>> toYield;
	private Iterator<ITree<ContainedType>>        curYield;

	public TopDownTransformIterator(Function<ContainedType, TopDownTransformResult>                          pickr,
			BiFunction<ITree<ContainedType>, Consumer<Iterator<ITree<ContainedType>>>, ITree<ContainedType>> transfrm,
			ITree<ContainedType> tree) {
		preParent = tree;

		preChildren  = new LinkedList<>();
		postChildren = new LinkedList<>();
		toYield      = new LinkedList<>();

		picker      = pickr;
		transform = transfrm;

		done    = false;
		initial = true;
	}

	public void addYield(Iterator<ITree<ContainedType>> src) {
		if(curYield != null) {
			toYield.push(curYield);
		}
		
		curYield = src;
	}

	public boolean hasNext() {
		return !done;
	}

	public ITree<ContainedType> next() {
		if(done) throw new NoSuchElementException();

		if(initial) {
			TopDownTransformResult res = picker.apply(preParent.getHead());

			switch(res) {
				case PASSTHROUGH:
					postParent = new Tree<>(preParent.getHead());

					if(preParent.getChildrenCount() != 0) {
						for(int i = 0; i < preParent.getChildrenCount(); i++) {
							preChildren.add(preParent.getChild(i));
						}
						
						// Return whatever the first child is
						break;
					} else {
						done = true;
						return postParent;
					}
				case SKIP:
					done = true;
					return preParent;
				case TRANSFORM:
					done = true;
					return transform.apply(preParent, this::addYield);
				case RTRANSFORM:
					preParent = transform.apply(preParent, this::addYield);
					break;
				case PUSHDOWN:
					if(preParent.getChildrenCount() != 0) {
						for(int i = 0; i < preParent.getChildrenCount(); i++) {
							preChildren.add(preParent.getChild(i));
						}
						
						// Return whatever the first child is
						break;
					} else {
						done = true;
						return transform.apply(new Tree<>(preParent.getHead()), this::addYield);
					}
				case PULLUP:
					ITree<ContainedType> intRes = transform.apply(preParent, this::addYield);
					
					postParent = new Tree<>(intRes.getHead());

					if(intRes.getChildrenCount() != 0) {
						for(int i = 0; i < intRes.getChildrenCount(); i++) {
							preChildren.add(intRes.getChild(i));
						}
						
						// Return whatever the first child is
						break;
					} else {
						done = true;
						return postParent;
					}
				default:
					throw new IllegalArgumentException("Unknown result type " + res);
			}

			if(res != RTRANSFORM) initial = false;
		}

		if(curYield != null) {
			if(curYield.hasNext()) {
				return curYield.next();
			} else {
				while(toYield.size() != 0 && !curYield.hasNext()) {
					curYield = toYield.pop();
				}

				if(toYield.size() == 0 && !curYield.hasNext()) {
					curYield = null;
				}
			}
		}

		if(curChild == null || !curChild.hasNext()) {
			if(preChildren.size() != 0) {
				curChild = new TopDownTransformIterator<>(picker, transform, preChildren.pop());
				
				ITree<ContainedType> res = curChild.next();
				postChildren.add(res);

				return res;
			} else {
				ITree<ContainedType> res = null;

				if(postParent == null) {
					res = new Tree<>(preParent.getHead());

					for(ITree<ContainedType> child : postChildren) {
						res.addChild(child);
					}

					res = transform.apply(res, this::addYield);
				} else {
					res = postParent;

					for(ITree<ContainedType> child : postChildren) {
						res.addChild(child);
					}
				}

				done = true;
				return res;
			}
		} else {
			ITree<ContainedType> res = curChild.next();
			postChildren.add(res);

			return res;
		}
	}
}
