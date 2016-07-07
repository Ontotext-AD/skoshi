package com.ontotext.skoshi.tree;

/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.io.Serializable;
import java.util.*;

/**
 * Original code from http://www.java2s.com/Code/Java/Collections-Data-Structure/Yourowntreewithgenericuserobject.htm <br>
 * Modified by Philip Alexiev.
 *
 * @param <T> Object's type in the tree.
 * @author ycoppel@google.com (Yohann Coppel)
 * @author philip.alexiev@ontotext.com (Philip Alexiev)
 */
public class Tree<T> implements Serializable {

	private static final long serialVersionUID = 6616829189693083147L;

	@JsonUnwrapped
	private final T head;
	private final List<Tree<T>> leafs = new ArrayList<>();

	@JsonIgnore
	private Tree<T> parent = null;

	@JsonIgnore
	private Map<T, Tree<T>> locate = new HashMap<>();

	public Tree(T head) {
		this.head = head;
		locate.put(head, this);
	}

	public void addLeaf(T root, T leaf) {
		if (locate.containsKey(root)) {
			locate.get(root).addLeaf(leaf);
		} else {
			addLeaf(root).addLeaf(leaf);
		}
	}

	public Tree<T> addLeaf(T leaf) {
		Tree<T> t = new Tree<>(leaf);
		leafs.add(t);
		t.parent = this;
		t.locate = this.locate;
		locate.put(leaf, t);
		return t;
	}

	public Tree<T> setAsParent(T parentRoot) {
		Tree<T> t = new Tree<>(parentRoot);
		t.leafs.add(this);
		this.parent = t;
		t.locate = this.locate;
		t.locate.put(head, this);
		t.locate.put(parentRoot, t);
		return t;
	}

	public T getHead() {
		return head;
	}

	public Tree<T> getTree(T element) {
		return locate.get(element);
	}

	public Tree<T> getParent() {
		return parent;
	}

	public Collection<T> getSuccessors(T root) {
		Collection<T> successors = new ArrayList<>();
		Tree<T> tree = getTree(root);
		if (null != tree) {
			for (Tree<T> leaf : tree.leafs) {
				successors.add(leaf.head);
			}
		}
		return successors;
	}

	public Collection<Tree<T>> getSubTrees() {
		return leafs;
	}

	public static <T> Collection<T> getSuccessors(T of, Collection<Tree<T>> in) {
		for (Tree<T> tree : in) {
			if (tree.locate.containsKey(of)) {
				return tree.getSuccessors(of);
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Sort the tree recursively using the provided comparator.
	 *
	 * @param comparator
	 */
	public void sort(Comparator<Tree<T>> comparator) {
		Collections.sort(this.leafs, comparator);
		for (Tree<T> leaf : leafs)
			leaf.sort(comparator);
	}

	@Override
	public String toString() {
		return "Tree[" + head.toString() + ", " + (leafs.size()) + " children]";
	}

	public String toStringDeep() {
		StringBuilder sb = new StringBuilder();
		printTree(sb, 0);
		return sb.toString();
	}

	private static final int INDENT = 2;
	private static final String SPACES = "                                        ";

	private void printTree(StringBuilder sb, int increment) {
		String inc = SPACES.substring(0, increment * INDENT);
		sb.append(inc).append(head.toString()).append("\n");
		for (Tree<T> child : leafs) {
			child.printTree(sb, increment + 1);
		}
	}
}
