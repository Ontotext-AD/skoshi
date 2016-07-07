package com.ontotext.skoshi.model.navigation;

import com.ontotext.skoshi.model.entity.NamedEntity;

/**
 * Used as a data holder for most of the tree structures.
 */
public interface TreeNode extends NamedEntity {

	public abstract boolean isHasChildren();

    /** This breaks the immutability, but is a necessary
     * convenience, as all the trees are built on the fly
     * when we don't know immediately if the node has children
     */
    public abstract void setHasChildren(boolean hasChildren);

}
