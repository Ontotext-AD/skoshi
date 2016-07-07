package semanticstoreutils;

import com.ontotext.skoshi.error.DataAccessException;
import com.ontotext.skoshi.util.semanticstore.ResultQuery;
import info.aduna.iteration.Iterations;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryExecutorUtilsTest extends RepositoryTestParent {

	private RepositoryConnection outerQueryConnection;

	@Test
    public void testVoidQuery() throws RepositoryException {
        executorUtils.execute(connection -> {
			final RepositoryResult<Statement> statements = connection.getStatements(null, FOAF.NAME, null, true);
			assertEquals(3, Iterations.asList(statements).size());
			assertTrue(connection.isOpen());
			outerQueryConnection = connection;
		});
        assertFalse(outerQueryConnection.isOpen());
    }

    @Test
    public void testVoidQueryRuntimeException() {
        try {
            executorUtils.execute(connection -> {
				throw new IllegalStateException("Runtime");
			});
        } catch (IllegalStateException e) {
            assertEquals("Runtime", e.getMessage());
        }
    }

    @Test
    public void testVoidQueryCheckedException() {
        try {
            executorUtils.execute(connection -> {
				throw new IOException("Checked");
			});
        } catch (DataAccessException e) {
            assertEquals("Checked", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void testVoidQueryCheckedExceptionMessage() {
        try {
            executorUtils.execute(connection -> {
				throw new IOException("Checked");
			}, "Some checked exception had happened");
        } catch (DataAccessException e) {
            assertEquals("Some checked exception had happened", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("Checked", e.getCause().getMessage());
        }
    }

    @Test
    public void testResultQuery() throws RepositoryException {
       String person1Name = executorUtils.executeQuery(connection -> {
		   ValueFactory f = connection.getValueFactory();
		   URI person1URI = f.createURI(FOAF.NAMESPACE, "person1");
		   assertTrue(connection.isOpen());
		   outerQueryConnection = connection;
		   return connection.getStatements(person1URI, FOAF.NAME, null, true).next().getObject().stringValue();
	   });
       assertEquals("john", person1Name);
       assertFalse(outerQueryConnection.isOpen());
    }

    @Test
    public void testResultQueryRuntimeException() {
        try {
            int i = executorUtils.executeQuery(new ResultQuery<Integer>() {
                @Override
                public Integer executeQuery(RepositoryConnection connection) throws Exception {
                    throw new IllegalStateException("Runtime");
                }
            });
        } catch (IllegalStateException e) {
            assertEquals("Runtime", e.getMessage());
        }
    }

    @Test
    public void testResultQueryCheckedException() {
        try {
            int i = executorUtils.executeQuery(new ResultQuery<Integer>() {
                @Override
                public Integer executeQuery(RepositoryConnection connection) throws Exception {
                    throw new IOException("Checked");
                }
            });
        } catch (DataAccessException e) {
            assertEquals("Checked", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void testResultQueryCheckedExceptionMessage() {
        try {
            int i = executorUtils.executeQuery(new ResultQuery<Integer>() {
                @Override
                public Integer executeQuery(RepositoryConnection connection) throws Exception {
                    throw new IOException("Checked");
                }
            }, "Some checked exception had happened");
        } catch (DataAccessException e) {
            assertEquals("Some checked exception had happened", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("Checked", e.getCause().getMessage());
        }
    }
}
