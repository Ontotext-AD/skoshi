package semanticstoreutils;

import com.ontotext.skoshi.error.DataAccessException;
import com.ontotext.skoshi.util.semanticstore.QueryExecutor;
import org.junit.Test;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryExecutorTest extends RepositoryTestParent {

    @Test
    public void testQuery() throws RepositoryException {
        final AtomicInteger i = new AtomicInteger(0);
        executorUtils.execute(connection -> new QueryExecutor(connection) {

			@Override
			public String constructQuery() {
				return "SELECT * WHERE {?person ?predicate ?name} order by ?name";
			}

			@Override
			public void handleResult(BindingSet resultRow) {
				String name = resultRow.getBinding("name").getValue().stringValue();
				int callTime = i.incrementAndGet();
				switch (callTime) {
					case  1 : assertEquals("duke", name);
							  break;
					case  2 : assertEquals("john", name);
						break;
					case  3 : assertEquals("mike", name);
						break;
				}
			}
		}.execute());
    }

    @Test
    public void testExceptionFromHandleQuery() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection) {

				@Override
				public String constructQuery() {
					throw new RuntimeException("Exception thrown from query");
				}

				@Override
				public void handleResult(BindingSet resultRow) {
				}
			}.execute());
        } catch (RuntimeException e) {
            assertEquals("Exception thrown from query", e.getMessage());
        }
    }

    @Test
    public void testExceptionFromHandleResult() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection) {

				@Override
				public String constructQuery() {
					return "SELECT * WHERE {?person ?predicate ?name} order by ?name";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
					throw new RuntimeException("Exception thrown from handleResult");
				}
			}.execute());
        } catch (RuntimeException e) {
            assertEquals("Exception thrown from handleResult", e.getMessage());
        }
    }


    @Test
    public void testMalformedQueryException() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection) {

				@Override
				public String constructQuery() {
					return "SELECT * WHERE {?person ?predicate - broken query";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
				}
			}.execute());
        } catch (DataAccessException e) {
            assertTrue(e.getCause() instanceof org.openrdf.query.MalformedQueryException);
        }
    }

    @Test
    public void testMalformedQueryExceptionWithCustomMessage() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection) {

				@Override
				public String constructQuery() {
					return "SELECT * WHERE {?person ?predicate - broken query";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
				}
			}.execute(), "the-query-went-wrong-custom-message");
        } catch (DataAccessException e) {
            assertTrue(e.getCause() instanceof org.openrdf.query.MalformedQueryException);
            assertEquals("the-query-went-wrong-custom-message", e.getMessage());
        }
    }

    @Test
    public void testMalformedQueryExceptionWithCustomMessageFromAnonymousClass() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection, "anonymous class message") {

				@Override
				public String constructQuery() {
					return "SELECT * WHERE {?person ?predicate - broken query";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
				}
			}.execute());
        } catch (DataAccessException e) {
            assertTrue(e.getCause() instanceof org.openrdf.query.MalformedQueryException);
            assertEquals("anonymous class message", e.getMessage());
        }
    }

    @Test
    public void testMalformedQueryExceptionWithCustomMessageFromAnonymousClassOverwritten() throws RepositoryException {
        try {
            executorUtils.execute(connection -> new QueryExecutor(connection, "anonymous class message") {

				@Override
				public String constructQuery() {
					return "SELECT * WHERE {?person ?predicate - broken query";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
				}
			}.execute(), "overwriting anonymous class message");
        } catch (DataAccessException e) {
            assertTrue(e.getCause() instanceof org.openrdf.query.MalformedQueryException);
            assertEquals("overwriting anonymous class message", e.getMessage());
        }
    }
}
