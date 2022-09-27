package bjc.utils.graph;

import java.util.Set;

import bjc.data.Either;

// See https://web.archive.org/web/20190414072011/https://core.ac.uk/download/pdf/82129679.pdf
public class ASGraphGrammar<NonTerminal, Terminal, Label> {
	public class Rule<Node> {
		private ASGraph<Node, NonTerminal, ?> starting;
		
		// Must contain at least one node
		private ASGraph<Node, Either<NonTerminal, Terminal>, Label> production;
		
		// Start node and end node must be in production
		private Either<NonTerminal, Terminal> startNode;
		private Either<NonTerminal, Terminal> endNode;
		
		public void derive(ASGraph<Node, Either<NonTerminal, Terminal>, Label> graph) {
			// The derivation of H from G according to the rule A -> K(I/O) consists simply of replacing
			// a node N' in G whose value is A by the graph K. Arcs leading into N' are replaced by
			// arcs leading to I, arcs exiting from B' are replaced by arcs exiting from O, and any
			// loop arcs on N' are replaced by arcs from O to I. 

		}
	}

	// int is perhaps not the best node type, but it works
	private Set<Rule<Integer>> rules;
}
