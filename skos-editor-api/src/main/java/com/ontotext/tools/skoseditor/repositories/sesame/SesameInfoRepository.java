package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.openpolicy.error.DataAccessException;
import com.ontotext.tools.skoseditor.repositories.InfoRepository;
import org.apache.commons.io.output.StringBuilderWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;

import java.io.Writer;

public class SesameInfoRepository implements InfoRepository {

    private Repository repository;

    public SesameInfoRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String dumpRepo() {
        Writer writer = new StringBuilderWriter();
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.export(new TurtleWriter(writer));
            } finally {
                connection.close();
            }
        } catch (RepositoryException|RDFHandlerException re) {
            throw new DataAccessException("Failed to dump repo.", re);
        }
        return writer.toString();
    }
}
