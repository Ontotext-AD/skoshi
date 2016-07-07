package com.ontotext.skoshi.model.entity;

import org.openrdf.model.URI;

/**
 * A simple POJO implementation of the NamedEntity interface
 */
public class NamedEntityImpl extends EntityImpl implements NamedEntity {

	private String label;

	public NamedEntityImpl(URI id, String label) {
		super(id);
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "NamedEntityImpl[" + getId() + ": " + getLabel() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		NamedEntityImpl that = (NamedEntityImpl) o;
		return label.equals(that.label);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + label.hashCode();
		return result;
	}
}
