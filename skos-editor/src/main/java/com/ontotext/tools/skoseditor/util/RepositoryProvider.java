package com.ontotext.tools.skoseditor.util;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;

public class RepositoryProvider {

    private RepositoryProvider() {}

    public static Repository newInstance(String dataFolder) {
        File dataDir = new File(dataFolder);
        Repository repo = new SailRepository( new MemoryStore(dataDir) );
        try {
            repo.initialize();
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to initialize repository.", e);
        }
        return repo;
    }

}
