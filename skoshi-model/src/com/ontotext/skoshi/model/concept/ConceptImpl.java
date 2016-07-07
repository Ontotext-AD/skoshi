package com.ontotext.skoshi.model.concept;

import com.ontotext.openpolicy.entity.NamedEntityImpl;
import org.openrdf.model.URI;


public class ConceptImpl extends NamedEntityImpl implements Concept {

	private static final long serialVersionUID = -3459473592046671382L;

	public ConceptImpl(URI id, String label) {
		super(id, label);
	}


}
