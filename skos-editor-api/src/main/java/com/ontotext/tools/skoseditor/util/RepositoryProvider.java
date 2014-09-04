package com.ontotext.tools.skoseditor.util;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;

public class RepositoryProvider {

    private RepositoryProvider() {}

    public static Repository newInstance(String dataFolderPath) {

        if (StringUtils.isEmpty(dataFolderPath)) {
            dataFolderPath = "~/.skosedit/data";
        }

        File dataDir = new File(dataFolderPath);

        boolean dataDirExists = dataDir.exists();

        if (!dataDirExists) {
            dataDir.mkdirs();
        }
        Repository repo = new SailRepository( new ForwardChainingRDFSInferencer(new MemoryStore(dataDir)) );
        try {
            repo.initialize();
            RepositoryConnection connection = repo.getConnection();
            if (!dataDirExists) {
                connection.setNamespace("skos", SKOS.NAMESPACE);
                connection.add(RepositoryProvider.class.getClassLoader().getResource("kb/skos.rdf"), SKOS.NAMESPACE, RDFFormat.RDFXML);
                connection.add(RepositoryProvider.class.getClassLoader().getResource("kb/skos-x.ttl"), SKOS.NAMESPACE, RDFFormat.TURTLE);
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException("Failed to initialize repository.", re);
        } catch (RDFParseException|IOException rpe) {
            throw new IllegalStateException("Failed to parse rdf.", rpe);
        }
        return repo;
    }

}
