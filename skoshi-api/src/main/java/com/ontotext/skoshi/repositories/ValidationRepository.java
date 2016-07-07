package com.ontotext.skoshi.repositories;

import com.ontotext.skoshi.error.AlreadyExistsException;
import com.ontotext.skoshi.error.NotFoundException;
import org.openrdf.model.URI;

public interface ValidationRepository {

    void validateExists(URI id) throws NotFoundException;

    void validateDoesNotExist(URI id) throws AlreadyExistsException;

    boolean exists(URI id);

}
