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
        return id;
    }

    @Override
    public String getPrefLabel() {
        return prefLabel;
    }

    @Override
    public String toString() {
        return "NamedEntity[id:"+id.getLocalName()+",lbl:"+prefLabel+"]";
    }
}
