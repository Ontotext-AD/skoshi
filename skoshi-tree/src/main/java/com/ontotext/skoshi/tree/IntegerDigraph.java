package com.ontotext.skoshi.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Directed Graph structure <br/>
 * <br/>
 * Code taken from: <br/>
 * <br/>
 * Algorithms <br/>
 * FOURTH EDITION <br/>
 * Robert Sedgewick
 * and
 * Kevin Wayne <br/>
 * Princeton University <br/>
 * <br/>
 *
 * @author philip
 * @see <a href="http://algs4.cs.princeton.edu/42directed/">http://algs4.cs.princeton.edu/42directed/</a> <br/>
 */
public class IntegerDigraph {
	private final int V;
	private int E;
	private final List<Integer>[] adj;

	@SuppressWarnings("unchecked")
	public IntegerDigraph(int V) {
		this.V = V;
		this.E = 0;
		adj = (List<Integer>[]) new List[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<>();
        }
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

	public void addEdge(int v, int w) {
		adj[v].add(w);
		E++;
	}

	public Iterable<Integer> adj(int v) {
		return adj[v];
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int ix = 0; ix < V; ix++) {
			sb.append("adj[").append(ix).append("]=").append(adj[ix]).append("\n");
		}
		return sb.toString();
	}
}
