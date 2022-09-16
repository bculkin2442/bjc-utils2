/** This package contains a number of classes useful for processing input that
 * is structured as a series of 'blocks' or records.
 * <p>
 *
 * The most fundamental unit here is that of {@link bjc.utils.ioutils.blocks.Block}. Each {@link
 * bjc.utils.ioutils.blocks.BlockReader} will yield a sequence of these, which contain a piece of text
 * as its contents, as well as the beginning/ending line for that block.
 *
 * There are a number of different types of {@link bjc.utils.ioutils.blocks.BlockReader}, which are
 * summarized here.
 * </p>
 *
 * <dl>
 * 	<dt>{@link bjc.utils.ioutils.blocks.SimpleBlockReader}</dt>
 * 	<dd>
 * 		The most basic form of BlockReader. This uses a regular expression to
 * 		delimit input reader from a {@link Reader} into a series of blocks.
 * 		Listed first, because this is
 * 	</dd>
 * </dl>
 * @author Ben Culkin */
package bjc.utils.ioutils.blocks;
