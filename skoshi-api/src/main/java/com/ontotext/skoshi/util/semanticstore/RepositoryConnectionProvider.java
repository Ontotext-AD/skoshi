package com.ontotext.skoshi.util.semanticstore;

import com.ontotext.skoshi.error.DataAccessException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * <p>Class providing {@link org.openrdf.repository.RepositoryConnection} objects.
 * Each invocation of {@link #getConnection()} provides a new {@link org.openrdf.repository.RepositoryConnection}.
 * There is no caching or pooling.</p>
 */
public class RepositoryConnectionProvider {
    private static final Logger log = LoggerFactory.getLogger(RepositoryConnectionProvider.class);

    private Repository repository;

    /**
     * The provided repository is used to provide connections via {@link #getConnection()}
     * @param repository - a {@link org.openrdf.repository.Repository} object representing a running repository
     * @throws IllegalArgumentException if the repository parameter is null
     */
    public RepositoryConnectionProvider(Repository repository) {
        Assert.notNull(repository, "Repository is null");
        this.repository = repository;
        log.trace("Creating RepositoryConnectionProvider for provided repository");
    }

    /**
     * Creates a {@link org.openrdf.repository.http.HTTPRepository} with the provided parameters.
     * This repository is used to provide connections via {@link #getConnection()}
     * @param serverAddress - A valid sesame server address
     * @param repositoryId - A valid repository id
     * @throws IllegalArgumentException if some of the parameters is empty
     */
    @SuppressWarnings(value = {"unused"})//Used in spring configuration
    public RepositoryConnectionProvider(String serverAddress, String repositoryId) {
        Assert.hasText(serverAddress, "Sesame server address is empty");
        Assert.hasText(repositoryId, "Sesame repository ID is empty");
        this.repository = new HTTPRepository(serverAddress, repositoryId);
        log.trace("Creating RepositoryConnectionProvider for HTTPRepository(server#repository - {}#{})", serverAddress, repositoryId);
    }

    /**
     * Provides connections to the underlying {@link org.openrdf.repository.Repository} object.
     * @return {@link org.openrdf.repository.RepositoryConnection} objects
     * @throws DataAccessException - if the connection object cannot be created
     */
    public RepositoryConnection getConnection() throws DataAccessException {
        try {
            log.trace("Creating new RepositoryConnection");
            return repository.getConnection();
        } catch (RepositoryException e) {
            throw new DataAccessException("Repository connection could not be created", e);
        }
    }

    /**
     * Closing the connection throws a {@link org.openrdf.repository.RepositoryException}.
     * This method is an utility method that logs {@link org.openrdf.repository.RepositoryException} thrown when closing.
     * The method will not throw exception if the provided exception is null or a real object.
     * @param connection {@link org.openrdf.repository.RepositoryException} object or null.
     */
    public static void closeConnectionQuietly(RepositoryConnection connection) throws DataAccessException {
        try {
            log.trace("Closing a RepositoryConnection.");
            if (connection != null) connection.close();
        } catch (RepositoryException e) {
            log.warn("Could not close RepositoryConnection", e);
        }
    }
}
