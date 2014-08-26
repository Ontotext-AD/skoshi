package com.ontotext.tools.skoseditor.repositories;

import org.openrdf.model.URI;

public interface ValidationRepository {

    void validateExists(URI id) throws IllegalArgumentException;

}
