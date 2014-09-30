package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.openpolicy.error.AlreadyExistsException;
import com.ontotext.openpolicy.error.NotFoundException;
import org.openrdf.model.URI;

public interface ValidationRepository {

    void validateExists(URI id) throws NotFoundException;

    void validateDoesNotExist(URI id) throws AlreadyExistsException;

}
