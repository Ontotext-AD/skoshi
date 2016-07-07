package com.ontotext.skoshi.util.semanticstore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;


public class RepositoryConnectionProviderTest {

    private Repository repository;

    @Before
    public void setUpRepository() throws RepositoryException {
        repository = new SailRepository(new MemoryStore());
    }

    @After
    public void shutdownRepository() throws RepositoryException {
        repository.shutDown();
    }

    @Test
    public void testWithInMemoryRepository() throws RepositoryException {
        repository.initialize();
        RepositoryConnectionProvider connectionProvider = new RepositoryConnectionProvider(repository);

        RepositoryConnection repositoryConnection = null;
        try {
            repositoryConnection = connectionProvider.getConnection();
            Assert.assertTrue(repositoryConnection.isOpen());
        } catch (RepositoryException e) {
            throw new RuntimeException("The connection to the SailRepository failed", e);
        } finally {
            Assert.assertNotNull(repositoryConnection);
            RepositoryConnectionProvider.closeConnectionQuietly(repositoryConnection);
            try {
                Assert.assertFalse(repositoryConnection.isOpen());
            } catch (RepositoryException e) {
                throw new RuntimeException("Could not test that the RepositoryConnection is open", e);
            }
        }
    }
}
