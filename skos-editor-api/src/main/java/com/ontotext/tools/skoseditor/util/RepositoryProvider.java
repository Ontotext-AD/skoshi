package com.ontotext.tools.skoseditor.util;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class RepositoryProvider {

    private static final Logger log = LoggerFactory.getLogger(RepositoryProvider.class);

    private static Repository REPO;

    private RepositoryProvider() {}

    public static Repository get(String dataFolderPath) {

        if (REPO == null) {

            System.setProperty("org.openrdf.repository.debug", "true");

            if (StringUtils.isEmpty(dataFolderPath)) {
                dataFolderPath = System.getProperty("user.home") + "/.skosedit/data";
            }

            log.debug("Sesame Repository data folder: " + dataFolderPath);

            File dataDir = new File(dataFolderPath);

            boolean dataDirExists = dataDir.exists();

            if (!dataDirExists) {
                dataDir.mkdirs();
            }
            REPO = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(dataDir)));
            try {
                REPO.initialize();
                RepositoryConnection connection = REPO.getConnection();

                try {
                    log.debug("Adding SKOS as axioms.");
                    connection.setNamespace("skos", SKOS.NAMESPACE);

                    URL skosUrl = RepositoryProvider.class.getClassLoader().getResource("kb/skos.rdf");
                    if (!new File(skosUrl.toURI()).exists())
                        throw new IllegalStateException("Could not find skos.rdf in the classpath.");
                    connection.add(skosUrl, SKOS.NAMESPACE, RDFFormat.RDFXML);

                    URL skosXUrl = RepositoryProvider.class.getClassLoader().getResource("kb/skos-x.ttl");
                    if (!new File(skosXUrl.toURI()).exists())
                        throw new IllegalStateException("Could not find skos.rdf in the classpath.");
                    connection.add(skosXUrl, SKOS.NAMESPACE, RDFFormat.TURTLE);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }
            } catch (URISyntaxException use) {
                throw new IllegalStateException("Invalid file location.", use);
            } catch (RepositoryException re) {
                throw new IllegalStateException("Failed to initialize repository.", re);
            } catch (RDFParseException | IOException rpe) {
                throw new IllegalStateException("Failed to parse rdf.", rpe);
            }
        }

        return REPO;
    }

    public static void main(String[] args) throws Exception {
        Repository r = get(null);
        System.out.println("OK");

        RepositoryConnection connection = r.getConnection();

        RepositoryResult<Statement> statements = connection.getStatements(new URIImpl(SKOS.NAMESPACE + "Test-Concept"), null, null, true);
        while (statements.hasNext()) {
            System.out.println("--> " + statements.next());
        }
    }

}
