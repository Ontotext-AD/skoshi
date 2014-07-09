package com.ontotext.tools.skoseditor.model;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.SKOS;

public class SKOSX {

    public static final URI ACRONYM;
    public static final URI ABBREVIATION;
    public static final URI SYNONYM;

    static {
        final ValueFactory f = ValueFactoryImpl.getInstance();

        ACRONYM = f.createURI(SKOS.NAMESPACE, "acronym");
        ABBREVIATION = f.createURI(SKOS.NAMESPACE, "abbreviation");
        SYNONYM = f.createURI(SKOS.NAMESPACE, "synonym");
    }

}
