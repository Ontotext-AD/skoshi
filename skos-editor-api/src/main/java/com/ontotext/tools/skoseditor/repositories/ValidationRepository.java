package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.tools.skoseditor.error.AlreadyExistsException;
import com.ontotext.tools.skoseditor.error.DoesNotExistException;
import org.openrdf.model.URI;

public interface ValidationRepository {

    void validateExists(URI id) throws DoesNotExistException;

    void validateDoesNotExist(URI id) throws AlreadyExistsException;

}
