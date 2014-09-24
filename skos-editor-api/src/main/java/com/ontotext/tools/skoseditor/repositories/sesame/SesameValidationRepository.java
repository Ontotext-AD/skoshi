package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.tools.skoseditor.error.AlreadyExistsException;
import com.ontotext.tools.skoseditor.error.DoesNotExistException;
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
    public void validateExists(URI id) throws DoesNotExistException {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                boolean exists = connection.hasStatement(id, null, null, false);
                if (!exists)
                    throw new DoesNotExistException();
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
    public void validateDoesNotExist(URI id) throws AlreadyExistsException {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                boolean exists = connection.hasStatement(id, null, null, false);
                if (exists)
                    throw new AlreadyExistsException();
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
