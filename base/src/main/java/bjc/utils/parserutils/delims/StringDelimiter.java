package bjc.utils.parserutils.delims;

import bjc.data.ITree;

/**
 * A sequence delimiter specialized for strings.
 *
 * @author EVE
 *
 */
public class StringDelimiter extends SequenceDelimiter<String> {

	/**
	 * Override of
	 * {@link SequenceDelimiter#delimitSequence(SequenceCharacteristics, Object...)}
	 * for ease of use for strings.
	 *
	 * @param seq
	 *        The sequence to delimit.
	 *
	 * @return The sequence as a tree.
	 *
	 * @throws DelimiterException
	 *         if something went wrong with delimiting the sequence.
	 *
	 * @see SequenceDelimiter
	 */
	public ITree<String> delimitSequence(final String... seq) throws DelimiterException {
		return super.delimitSequence(new SequenceCharacteristics<>("root", "contents", "subgroup"), seq);
	}
}
