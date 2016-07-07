package com.ontotext.skoshi.tree;

import java.util.*;

public class DigraphImpl<T> implements Digraph<T> {

    protected Map<T, List<T>> adj;

    public DigraphImpl() {
        this.adj = new HashMap<>();
    }

    @Override
    public void addVertex(T vertex) {
        adj.put(vertex, new ArrayList<T>());
    }

    @Override
    public void addEdge(T parent, T child) {
        List<T> adjParent = adj.get(parent);
        if (adjParent == null) {
            adjParent = new ArrayList<>();
            adj.put(parent, adjParent);
        }
        if (!adj.containsKey(child)) {
            adj.put(child, new ArrayList<T>());
        }
        adjParent.add(child);
    }

    @Override
    public List<T> findRoots() {

        if (adj.size() == 0) return Collections.emptyList();

        List<T> roots = new ArrayList<>();
        Set<T> withParents = new HashSet<>();

        for (List<T> children : adj.values()) {
            for (T child : children) {
                withParents.add(child);
            }
        }

        for (T node : adj.keySet()) {
            if (!withParents.contains(node)) {
                roots.add(node);
            }
        }

        return roots;
    }

    @Override
    public List<T> adj(T parent) {
        return adj.get(parent);
    }

    @Override
    public void sort(Comparator<T> comparator) {
        TreeMap<T, List<T>> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(this.adj);
        this.adj = sortedMap;

        for (List<T> adjList : adj.values()) {
            Collections.sort(adjList, comparator);
        }
    }

    public String toString() {
        String indent = "";
        StringBuilder sb = new StringBuilder();
        List<T> roots = findRoots();
        for (T root : roots) {
            printRecursively(root, indent, sb);
        }
        return sb.toString();
    }

    private void printRecursively(T element, String indent, StringBuilder sb) {
        sb.append(indent).append(element).append("\n");
        if (adj.get(element) != null) {
            for (T child : adj(element)) {
                printRecursively(child, indent + "  ", sb);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Digraph<Character> digraph = new DigraphImpl<>();

        digraph.addEdge('a', 'b');
        digraph.addEdge('b', 'c');
        digraph.addEdge('d', 'e');
        digraph.addEdge('f', 'g');

        System.out.println("ROOTS: " + digraph.findRoots());
        System.out.println(digraph);
    }

}
