package com.ontotext.skoshi.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;

/**
 * OpenPolicy SKOS customization constants (not part of original SKOS)
 *
 * @author philip
 */
public final class SKOSX {

	// String NS = "http://www.ontotext.com/skos/extension#";
	public static final String NAMESPACE = SKOS.NAMESPACE;

	// this may be achieved by skos labels extension,
	// but we don't need such level of sophistication
	public static final URI ABBREVIATION = new URIImpl(NAMESPACE + "abbreviation");
	public static final URI ACRONYM = new URIImpl(NAMESPACE + "acronym");

	// synonym also does not exist in original SKOS
	public static final URI SYNONYM = new URIImpl(NAMESPACE + "synonym");

	// this is kind of UI related, but necessary
	public static final URI FACET = new URIImpl(NAMESPACE + "Facet");
    public static final URI HAS_FACET_CONCEPT = new URIImpl(SKOS.NAMESPACE + "hasFacetConcept");

    public static final URI STEMMING = new URIImpl(NAMESPACE + "stemming");

    private SKOSX() {
	}
}
