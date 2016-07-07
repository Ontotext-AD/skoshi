package com.ontotext.skoshi.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public enum OwlimGraph {

	EXPLICIT("http://www.ontotext.com/explicit"),
	IMPLICIT("http://www.ontotext.com/implicit"),
	COUNT("http://www.ontotext.com/count");

	private final URI uri;

	private OwlimGraph(String uri) {
		this.uri = new URIImpl(uri);
	}

	@Override
	public String toString() {
		return uri.stringValue();
	}

}
