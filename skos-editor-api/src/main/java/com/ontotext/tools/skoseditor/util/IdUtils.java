package com.ontotext.tools.skoseditor.util;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;

public class IdUtils {

    private IdUtils() {}

    public static URI label2id(String label) {
        label = label.replaceAll("[^a-zA-Z0-9]", "-");
        return new URIImpl(SKOS.NAMESPACE + label);
    }

}
