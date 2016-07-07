package com.ontotext.skoshi.model.entity;

/**
 * A generic com.ontotext.skoshi.model.entity representation object.
 * Generally it is extended to apply class restrictions to the signatures.
 */
public interface NamedEntity extends Entity {

    /** Get the label of the com.ontotext.skoshi.model.entity */
	String getLabel();

}
