package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.openpolicy.error.NotFoundException;
import com.ontotext.tools.skoseditor.repositories.ValidationRepository;
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
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                boolean exists = connection.hasStatement(id, null, null, false);
                if (!exists)
                    throw new NotFoundException();
            } catch (RepositoryException re) {
                throw new IllegalStateException("Failed to import concepts.", re);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void validateDoesNotExist(URI id) throws NotFoundException {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                boolean exists = connection.hasStatement(id, null, null, false);
                if (exists)
                    throw new NotFoundException();
            } catch (RepositoryException re) {
                throw new IllegalStateException("Failed to import concepts.", re);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }
}
