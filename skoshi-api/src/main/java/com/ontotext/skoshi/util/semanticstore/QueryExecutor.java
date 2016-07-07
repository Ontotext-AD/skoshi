package com.ontotext.skoshi.util.semanticstore;

import com.ontotext.skoshi.error.DataAccessException;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A convenience class for executing sparql queries against the rdf store.
 * Uses a handler (callback) to consume the results.
 * Very important is after the object is created to call execute().
 */
public abstract class QueryExecutor {

	private final Logger log = LoggerFactory.getLogger(QueryExecutor.class);

	private RepositoryConnection semanticStore;
    private String exceptionWrapperMessage;

    public QueryExecutor(RepositoryConnection semanticStore) {
        assert (semanticStore != null);
        this.semanticStore = semanticStore;
    }

	public QueryExecutor(RepositoryConnection semanticStore, final String exceptionWrapperMessage) {
		assert (semanticStore != null);
		this.semanticStore = semanticStore;
        this.exceptionWrapperMessage = exceptionWrapperMessage;
	}

	public abstract String constructQuery();

	public abstract void handleResult(BindingSet resultRow);

	public final void execute() throws DataAccessException {

		long time;
		String query = constructQuery();

		TupleQueryResult result = null;
		try {

			time = System.currentTimeMillis();
			traceCaller();
			log.debug("Executing query:\n{}", query);
			TupleQuery tupleQuery = semanticStore.prepareTupleQuery(QueryLanguage.SPARQL, query);
			result = tupleQuery.evaluate();
			log.debug("Query executed in {}", getTimeDiff(time));

			time = System.currentTimeMillis();
			log.trace("Processing query results");
			while (result.hasNext()) {
				BindingSet resultRow = result.next();
				handleResult(resultRow);
			}
			log.debug("Query results processed in {}", getTimeDiff(time));

		} catch (Exception e) {
            exceptionWrapperMessage = StringUtils.isEmpty(exceptionWrapperMessage) ?
                                        e.getMessage() :
                                        exceptionWrapperMessage;
            throw new DataAccessException(exceptionWrapperMessage, e);
		} finally {
			SemanticStoreUtils.closeQuietly(result);
		}
	}

	private void traceCaller() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		String className = caller.getClassName();
		className = className.substring(className.lastIndexOf('.') + 1, className.length());
		String methodName = caller.getMethodName();
		log.debug("Executing query from: {}.{}", className, methodName);
	}

	public static String getTimeDiff(long time) {

		StringBuilder timeDiff = new StringBuilder();
		time = System.currentTimeMillis() - time;
		int seconds = (int) (time / 1000);
		int millis = (int) (time % 1000);

		if (seconds > 0) {
			timeDiff.append(seconds).append("s");
		}
		if (millis > 0) {
			timeDiff.append(millis).append("ms");
		}
		if (timeDiff.length() == 0) {
			timeDiff.append("no time :)");
		}

		return timeDiff.toString();
	}

}
