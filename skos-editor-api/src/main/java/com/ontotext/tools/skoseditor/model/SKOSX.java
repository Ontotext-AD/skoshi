package com.ontotext.tools.skoseditor.model;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.SKOS;

public class SKOSX {

    public static final URI ACRONYM;
    public static final URI ABBREVIATION;
    public static final URI SYNONYM;

    public static final URI FACET;
    public static final URI HAS_FACET_CONCEPT;

    static {
        final ValueFactory f = ValueFactoryImpl.getInstance();

        ACRONYM = f.createURI(SKOS.NAMESPACE, "acronym");
        ABBREVIATION = f.createURI(SKOS.NAMESPACE, "abbreviation");
        SYNONYM = f.createURI(SKOS.NAMESPACE, "synonym");

        FACET = f.createURI(SKOS.NAMESPACE, "Facet");
        HAS_FACET_CONCEPT = f.createURI(SKOS.NAMESPACE, "hasFacetConcept");
    }

}
