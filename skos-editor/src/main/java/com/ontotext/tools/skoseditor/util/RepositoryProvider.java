package com.ontotext.tools.skoseditor.util;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;

public class RepositoryProvider {

    private RepositoryProvider() {}

    public Repository provide(String repositoryDataDir) throws RepositoryException {
        File dataDir = new File(repositoryDataDir);
        Repository repo = new SailRepository( new MemoryStore(dataDir) );
        repo.initialize();
        return repo;
    }

}
