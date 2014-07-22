package com.ontotext.tools.skoseditor.services;

import org.openrdf.model.URI;

public interface UriEncodeService {

    String encode(URI id);
    URI decode(String id);

}
