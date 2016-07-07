package com.ontotext.skoshi.tree;

import java.util.Comparator;
import java.util.List;

public interface Digraph<T> {

    void addVertex(T vertex);

    void addEdge(T parent, T child);

    List<T> findRoots();

    List<T> adj(T parent);

    void sort(Comparator<T> comparator);
}
