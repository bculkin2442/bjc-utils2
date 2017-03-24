package bjc.utils.parserutils.pratt;

/**
 * Represents the contextual state passed to a command.
 * 
 * @author EVE
 *
 * @param <K>
 *                The key type of the tokens.
 * @param <V>
 *                The value type of the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class ParserContext<K, V, C> {
	/**
	 * The source of tokens.
	 */
	public TokenStream<K, V>	tokens;
	/**
	 * The parser for sub-expressions.
	 */
	public PrattParser<K, V, C>	parse;
	/**
	 * The state of the parser.
	 */
	public C			state;

	/**
	 * Create a new parser context.
	 * 
	 * @param tokens
	 *                The source of tokens.
	 * 
	 * @param parse
	 *                The parser to call for sub expressions.
	 * 
	 * @param state
	 *                Any state needing to be kept during parsing.
	 */
	public ParserContext(TokenStream<K, V> tokens, PrattParser<K, V, C> parse, C state) {
		this.tokens = tokens;
		this.parse = parse;
		this.state = state;
	}
}