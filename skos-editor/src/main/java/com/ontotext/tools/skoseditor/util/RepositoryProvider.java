package com.ontotext.tools.skoseditor.util;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;

public class RepositoryProvider {

    private RepositoryProvider() {}

    public static Repository newInstance(String dataFolder) {

        File dataDir = new File(FileUtils.getUserDirectoryPath() + "/.skoseditor/data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        Repository repo = new SailRepository( new MemoryStore(dataDir) );
        try {
            repo.initialize();
            repo.getConnection().setNamespace("skos", SKOS.NAMESPACE);
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to initialize repository.", e);
        }
        return repo;
    }

}
