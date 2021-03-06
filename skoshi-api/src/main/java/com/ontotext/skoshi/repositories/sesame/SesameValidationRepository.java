package com.ontotext.skoshi.repositories.sesame;

import com.ontotext.skoshi.error.NotFoundException;
import com.ontotext.skoshi.repositories.ValidationRepository;
import com.ontotext.skoshi.error.AlreadyExistsException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class SesameValidationRepository implements ValidationRepository {

    private Repository repository;

    public SesameValidationRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void validateExists(URI id) throws NotFoundException {
        if (!exists(id)) throw new NotFoundException();
    }

    @Override
    public void validateDoesNotExist(URI id) throws AlreadyExistsException {
        if (exists(id)) throw new AlreadyExistsException();
    }

    @Override
    public boolean exists(URI id) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                boolean exists = connection.hasStatement(id, null, null, false);
                return exists;
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException("Failed to validate if object " + id + " exists.", re);
        }
    }
}
