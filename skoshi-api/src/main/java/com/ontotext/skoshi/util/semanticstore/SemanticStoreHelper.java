package com.ontotext.skoshi.util.semanticstore;

import com.ontotext.skoshi.error.DataAccessException;
import com.ontotext.skoshi.error.NotFoundException;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A convenience class for executing sparql queries against the rdf store.
 * Has the functionality to get directly a single value result and others.
 * Important is to close the result when it is retrieved with closableResult().
 * <br/><br/>
 * Example use: <br/><br/>
 * <strong>
 *     String query = "select ?label where { skos:concept1 rdfs:label ?label }";
 *     String label = new SemanticStoreHelper(repositoryConnection).query(query).singleValue("label").stringValue();
 * </strong>
 */
public class SemanticStoreHelper {

	private final Logger log = LoggerFactory.getLogger(SemanticStoreHelper.class);

	private final RepositoryConnection connection;
	private String sparqlQuery;

	public SemanticStoreHelper(RepositoryConnection connection) {
		this.connection = connection;
	}

	/**
     * Sets the query to be executed by this helper. <br/>
	 *
	 * @param sparqlQuery The SPARQL query.
	 * @return The SemanticStoreHelper itself
	 */
	public SemanticStoreHelper query(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
		return this;
	}

	/**
	 * Retrieves a single value. Intended for queries like:  <stron>skos:conceptX preferredLabel ?label</stron> where the relation is 1:1
	 *
	 * @param boundVariableName The name of the bound variable to be returned
	 * @return The value of the bound variable
	 * @throws NotFoundException
	 * @throws IllegalStateException
	 * @throws DataAccessException
	 */
	public Value singleValue(String boundVariableName) throws NotFoundException, IllegalStateException,
			DataAccessException {
		return singleResult().getBinding(boundVariableName).getValue();
	}

    /**
     * Retrieves the bindings from a single result for the query. If there are more results, only the first will be retrieved.
     * The result is closed internally.
     *
     * @return The bindings from a single result for the query.
     * @throws NotFoundException
     * @throws IllegalStateException
     * @throws DataAccessException
     */
	public BindingSet singleResult() throws NotFoundException, IllegalStateException, DataAccessException {
		traceCaller();
		log.trace("Retrieving single closableResult for query: \n{}", sparqlQuery);
		BindingSet singleResult = null;
		TupleQueryResult result = null;
		try {
			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
			result = tupleQuery.evaluate();
			if (result != null && result.hasNext()) {
				singleResult = result.next();
                if (!singleResult.iterator().hasNext()) {
                    throw new NotFoundException();
                }
			} else {
				throw new NotFoundException();
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			throw new DataAccessException(e);
		} finally {
			SemanticStoreUtils.closeQuietly(result);
		}
		log.trace("Result is: {}", singleResult);
		return singleResult;
	}

    /**
     * Retrieves a closable result object, that can be iterated. This
     * object has to be closed after the work is done.
     *
     * @return Closable result object. Has to be closed explicitly.
     * @throws IllegalStateException
     * @throws DataAccessException
     */
	public TupleQueryResult closableResult() throws IllegalStateException, DataAccessException {
		traceCaller();
		log.trace("Retrieving closableResult for query: \n{}", sparqlQuery);
		TupleQueryResult result = null;
		try {
			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
			result = tupleQuery.evaluate();
		} catch (MalformedQueryException | QueryEvaluationException e) {
			throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			throw new DataAccessException(e);
		}
		return result;
	}

	private void traceCaller() {
        if (log.isTraceEnabled()) {
            StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
            String className = caller.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
            String methodName = caller.getMethodName();
            log.trace("Executing query from: {}.{}", className, methodName);
        }
	}

    public int getCount() {
        if (!sparqlQuery.contains(SparqlQueryUtils.getFromCountGraph())) {
            throw new IllegalStateException("Trying to get count from a query that is not querying the count graph!");
        }
        int result;
        TupleQueryResult tupleQueryResult = null;
        try {
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
            tupleQueryResult = tupleQuery.evaluate();
            result = Integer.valueOf(tupleQueryResult.next().iterator().next().getValue().stringValue());
        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new IllegalStateException(e);
        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException(nfe);
        } finally {
            SemanticStoreUtils.closeQuietly(tupleQueryResult);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        Repository repository = new HTTPRepository("http://localhost:10082/graphdb", "openpolicy");
        RepositoryConnection connection = repository.getConnection();

        String query = "select *\n" + SparqlQueryUtils.getFromCountGraph() + "\nwhere{ ?s ?p ?o } limit 10";

        int count = new SemanticStoreHelper(connection).query(query).getCount();

        System.out.println("COUNT: " + count);

        connection.close();
    }

}
