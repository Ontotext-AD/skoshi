package com.ontotext.skoshi.util.semanticstore;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class RepositoryTestParent {

    protected static Repository repository;
    protected static QueryExecutorUtils executorUtils;

    @BeforeClass
    public static void setUpRepository() throws RepositoryException {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();

        RepositoryConnection connection = repository.getConnection();
        ValueFactory f = repository.getValueFactory();
        connection.add(f.createURI(FOAF.NAMESPACE, "person1"), FOAF.NAME, f.createLiteral("john", XMLSchema.STRING));
        connection.add(f.createURI(FOAF.NAMESPACE, "person2"), FOAF.NAME, f.createLiteral("mike", XMLSchema.STRING));
        connection.add(f.createURI(FOAF.NAMESPACE, "person3"), FOAF.NAME, f.createLiteral("duke", XMLSchema.STRING));
        connection.close();

        executorUtils = new QueryExecutorUtils(new RepositoryConnectionProvider(repository));
    }

    @AfterClass
    public static void clearRepository() throws RepositoryException {
        repository.shutDown();
    }

}
