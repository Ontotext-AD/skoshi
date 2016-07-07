package com.ontotext.skoshi.util.semanticstore;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;

public final class SemanticStoreUtils {
    private static final Logger log = LoggerFactory.getLogger(SemanticStoreUtils.class);

	private SemanticStoreUtils() {
	}

	public static void removeStatementsInContext(URI context, RepositoryConnection semanticStore)
			throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		Update update = semanticStore.prepareUpdate(QueryLanguage.SPARQL, "drop graph <" + context.stringValue() + ">");
		update.execute();
	}

	public static String getStatementsInNTriples(Iterable<Statement> statements) throws RDFHandlerException {
		Writer writer = new StringWriter();
		RDFWriter rdfWriter = new NTriplesWriter(writer);
		rdfWriter.startRDF();
		for (Statement statement : statements) {
			rdfWriter.handleStatement(statement);
		}
		rdfWriter.endRDF();
		return writer.toString();
	}

    /**
     * Closes a TupleQueryResult quietly.
     * Issuing a warning log message.
     * @param resultList - the TupleQueryResult to be closed; can be null
     */
    public static void closeQuietly(TupleQueryResult resultList) {
        if (resultList != null) try {
            resultList.close();
        } catch (QueryEvaluationException | NullPointerException e) {
            //NullPointerException is caught because of a bug - https://openrdf.atlassian.net/browse/SES-1978
            log.warn("Could not close TupleQueryResult", e);
        }
    }
}
