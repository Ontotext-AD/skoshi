package com.ontotext.skoshi.model.entity;

import org.openrdf.model.URI;

/**
 * A simple POJO implementation of the Entity interface
 */
public class EntityImpl implements Entity {

	private final URI id;

	public EntityImpl(URI id) {
		this.id = id;
	}

	@Override
	public URI getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EntityImpl entity = (EntityImpl) o;

		return id.equals(entity.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
