package com.ontotext.skoshi.model.navigation;

import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.entity.NamedEntityImpl;
import org.openrdf.model.URI;

public class TreeNodeImpl extends NamedEntityImpl implements TreeNode {

	private boolean hasChildren;

	public TreeNodeImpl(URI id, String label, boolean hasChildren) {
		super(id, label);
		this.hasChildren = hasChildren;
	}

	public TreeNodeImpl(NamedEntity namedEntity) {
		super(namedEntity.getId(), namedEntity.getLabel());
	}

	/**
	 * Copy constructor
	 */
	public TreeNodeImpl(TreeNodeImpl node) {
		super(node.getId(), node.getLabel());
		this.hasChildren = node.hasChildren;
	}


	@Override
	public boolean isHasChildren() {
		return hasChildren;
	}

    @Override
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Override
	public String toString() {
		return "[id=" + getId() + ", label=\"" + getLabel() + "\", hasChildren=" + hasChildren + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		if (!super.equals(o))
			return false;

		TreeNodeImpl treeNode = (TreeNodeImpl) o;
		return hasChildren == treeNode.hasChildren;
	}
}
