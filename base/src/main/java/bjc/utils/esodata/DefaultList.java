package bjc.utils.esodata;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * A list that has a default value that out-of-bounds accesses return.
 *
 * @author Ben Culkin
 * @param <ValueType>
 *                The type of the values contained in the list.
 */
public class DefaultList<ValueType> extends AbstractList<ValueType> {
	/*
	 * @NOTE 9/17/18
	 *
	 * A possible feature to add would be the ability to set a 'default
	 * index', so that the default value is 'whatever is at that list index'
	 *
	 * Of course, this would cause an exception if that index was out of
	 * bounds, but what are you going to do?
	 */

	private ValueType defVal;

	private List<ValueType> backing;

	/**
	 * Create a new DefaultList.
	 */
	public DefaultList() {
		this(new ArrayList<>(), null);
	}

	/**
	 * Create a new DefaultList, with a set default value.
	 * 
	 * @param defVal
	 *                The default value for the list.
	 */
	public DefaultList(ValueType defVal) {
		this(new ArrayList<>(), defVal);
	}

	/**
	 * Create a new DefaultList, with a specific backing list.
	 * 
	 * @param backer
	 *                The backing list to use.
	 * 
	 */
	public DefaultList(List<ValueType> backer) {
		this(backer, null);
	}

	/**
	 * Create a new DefaultList, with a set default value.
	 * 
	 * @param backer
	 *                The backing list to use.
	 * 
	 * @param defVal
	 *                The default value for the list.
	 */
	public DefaultList(List<ValueType> backer, ValueType defVal) {
		this.defVal = defVal;

		this.backing = backer;
	}

	/**
	 * Get the default value.
	 * 
	 * @return The default value.
	 */
	public ValueType getDefault() {
		return defVal;
	}

	/**
	 * Set the default value.
	 * 
	 * @param defVal The default value.
	 */
	public void setDefault(ValueType defVal) {
		this.defVal = defVal;
	}

	@Override
	public ValueType get(int idx) {
		if (idx < 0 || idx >= backing.size()) return defVal;

		return backing.get(idx);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public ValueType set(int idx, ValueType val) {
		return backing.set(idx, val);
	}

	@Override
	public void add(int idx, ValueType val) {
		backing.add(idx, val);
	}

	@Override
	public ValueType remove(int idx) {
		return backing.remove(idx);
	}
}
