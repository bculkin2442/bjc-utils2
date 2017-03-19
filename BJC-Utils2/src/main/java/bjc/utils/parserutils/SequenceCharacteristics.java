package bjc.utils.parserutils;

/**
 * Marks the parameters for building a sequence tree.
 * 
 * @author EVE
 *
 * @param <T>
 *                The type of item in the tree.
 */
public class SequenceCharacteristics<T> {
	/**
	 * The item to mark the root of the tree.
	 */
	public final T root;

	/**
	 * The item to mark the contents of a group/subgroup.
	 */

	public final T contents;

	/**
	 * The item to mark a subgroup.
	 */
	public final T subgroup;

	/**
	 * Create a new set of parameters for building a tree.
	 * 
	 * @param root
	 *                The root marker.
	 * @param contents
	 *                The group/subgroup contents marker.
	 * @param subgroup
	 *                The subgroup marker.
	 */
	public SequenceCharacteristics(T root, T contents, T subgroup) {
		this.root = root;
		this.contents = contents;
		this.subgroup = subgroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contents == null) ? 0 : contents.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + ((subgroup == null) ? 0 : subgroup.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof SequenceCharacteristics)) return false;

		SequenceCharacteristics<?> other = (SequenceCharacteristics<?>) obj;

		if(contents == null) {
			if(other.contents != null) return false;
		} else if(!contents.equals(other.contents)) return false;

		if(root == null) {
			if(other.root != null) return false;
		} else if(!root.equals(other.root)) return false;

		if(subgroup == null) {
			if(other.subgroup != null) return false;
		} else if(!subgroup.equals(other.subgroup)) return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("SequenceCharacteristics [root=");
		builder.append(root == null ? "(null)" : root);
		builder.append(", contents=");
		builder.append(contents == null ? "(null)" : contents);
		builder.append(", subgroup=");
		builder.append(subgroup == null ? "(null)" : subgroup);
		builder.append("]");

		return builder.toString();
	}
}