package com.ontotext.tools.skoseditor.model;

import org.openrdf.model.URI;

public class NamedEntityImpl implements NamedEntity {

    private URI id;
    private String prefLabel;

    public NamedEntityImpl(URI id, String prefLabel) {
        this.id = id;
        this.prefLabel = prefLabel;
    }

    @Override
    public URI getId() {
        return null;
    }

    @Override
    public String getPrefLabel() {
        return null;
    }
}
