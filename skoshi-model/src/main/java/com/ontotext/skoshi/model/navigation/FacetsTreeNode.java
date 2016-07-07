package com.ontotext.skoshi.model.navigation;

import org.openrdf.model.URI;

/**
 * A decorator of a TreeNode object which also holds some info related to the concepts facets.
 */
public class FacetsTreeNode implements TreeNode {

	private final TreeNode treeNode;
	private final int countInResults;
	private final boolean inQuery;

	public FacetsTreeNode(TreeNode treeNode, boolean hasChildren, int countInResults, boolean inQuery) {
		this.treeNode = treeNode;
        this.treeNode.setHasChildren(hasChildren);
		this.countInResults = countInResults;
		this.inQuery = inQuery;
	}

	@Override
	public URI getId() {
		return treeNode.getId();
	}

	@Override
	public String getLabel() {
		return treeNode.getLabel();
	}

	@Override
	public boolean isHasChildren() {
		return treeNode.isHasChildren();
	}

    @Override
	public void setHasChildren(boolean hasChildren) {
		treeNode.setHasChildren(hasChildren);
	}

	public int getCountInResults() {
		return countInResults;
	}

	public boolean isInQuery() {
		return inQuery;
	}

	@Override
	public String toString() {
		return "[id=" + getId() + ", label=\"" + getLabel() + "\", hasChildren=" + isHasChildren() + ", countInResults=" + getCountInResults() + ", inQuery=" + isInQuery() + "]";
	}

}
