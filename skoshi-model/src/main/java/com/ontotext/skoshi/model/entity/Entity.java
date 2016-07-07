package com.ontotext.skoshi.model.entity;

import org.openrdf.model.URI;

/**
 * A generic object specified by a concrete id.
 */
public interface Entity {

    /** Get the id of the com.ontotext.skoshi.model.entity */
	URI getId();

}
