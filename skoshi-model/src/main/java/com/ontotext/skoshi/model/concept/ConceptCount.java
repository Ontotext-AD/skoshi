package com.ontotext.skoshi.model.concept;

import org.openrdf.model.URI;

public class ConceptCount {

    private URI id;
    private int count;

    public ConceptCount(URI id, int count) {
        this.id = id;
        this.count = count;
    }


    public URI getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

}
