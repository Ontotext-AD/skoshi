package com.ontotext.skoshi.util.semanticstore;

import com.ontotext.skoshi.error.DataAccessException;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.util.Assert;

/**
 * Utility class helpful in working with {@link org.openrdf.repository.RepositoryConnection} objects.
 *
 * <ul>
 *  <caption>The class provides the following services:</caption>
 *  <li> to pass a {@link org.openrdf.repository.RepositoryConnection} object to an executing object and then closes it automatically</li>
 *  <li> {@link RuntimeException} - runtime exceptions are rethrown from the classes methods as they are</li>
 *  <li> {@link Exception} - checked exceptions are wrapped into {@link com.ontotext.openpolicy.error.DataAccessException}</li>
 * </ul>
 */
public class QueryExecutorUtils {

    private RepositoryConnectionProvider connectionProvider;

    /**
     * The {@link com.ontotext.skoshi.util.semanticstore.RepositoryConnectionProvider} is used to provide
     * {@link org.openrdf.repository.RepositoryConnection} objects.
     * @param connectionProvider - provider connected to a running repository
     */
    public QueryExecutorUtils(RepositoryConnectionProvider connectionProvider) {
        Assert.notNull(connectionProvider);
        this.connectionProvider = connectionProvider;
    }

    /**
     * The method passes a fresh RepositoryConnection object to the queryExecutor.
     * And closes the connection afterwards.
     * The method rethrows each runtime exception. And wraps each checked exception into a {@link com.ontotext.openpolicy.error.DataAccessException}
     * @param queryExecutor - Implementation of ResultQuery interface
     * @return T - returns the result of {@link com.ontotext.openpolicy.semanticstoreutils.ResultQuery#executeQuery(org.openrdf.repository.RepositoryConnection)}
     * @throws DataAccessException - if a checked exception arises it will be wrapped as DataAccessException
     */
    public final <T> T executeQuery(ResultQuery<T> queryExecutor) throws DataAccessException {
        return executeQuery(queryExecutor, null);
    }

    /**
     * The method passes a fresh RepositoryConnection object to the queryExecutor.
     * And closes the connection afterwards.
     * The method rethrows each runtime exception. And wraps each checked exception into a {@link com.ontotext.openpolicy.error.DataAccessException}
     * If exceptionMessage is a valid string value it will be used as a message for the wrapped DataAccessException
     * @param queryExecutor - Implementation of ResultQuery interface
     * @param exceptionMessage - the message will be used as a message to the DataAccessException which wraps any checked excepton.
     * @return T - returns the result of {@link com.ontotext.openpolicy.semanticstoreutils.ResultQuery#executeQuery(org.openrdf.repository.RepositoryConnection)}
     * @throws DataAccessException - if a checked exception arises it will be wrapped as DataAccessException
     */
    public final <T> T executeQuery(ResultQuery<T> queryExecutor, String exceptionMessage) throws DataAccessException {
        RepositoryConnection connection = null;
        try {
            connection = connectionProvider.getConnection();
            return queryExecutor.executeQuery(connection);
        } catch (DataAccessException e) {
            throw new DataAccessException(getExceptionMessage(e, exceptionMessage), e.getCause());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(getExceptionMessage(e, exceptionMessage), e);
        } finally {
            RepositoryConnectionProvider.closeConnectionQuietly(connection);
        }
    }

    /**
     * The method passes a fresh RepositoryConnection object to the queryExecutor.
     * And closes the connection afterwards.
     * The method rethrows each runtime exception. And wraps each checked exception into a {@link com.ontotext.openpolicy.error.DataAccessException}
     * @param voidQuery - Implementation of VoidQuery interface
     * @throws DataAccessException - if a checked exception arises it will be wrapped as DataAccessException
     */
    public final void execute(VoidQuery voidQuery) throws DataAccessException {
       execute(voidQuery, null);
    }

    /**
     * The method passes a fresh RepositoryConnection object to the queryExecutor.
     * And closes the connection afterwards.
     * The method rethrows each runtime exception. And wraps each checked exception into a {@link com.ontotext.openpolicy.error.DataAccessException}
     * If exceptionMessage is a valid string value it will be used as a message for the wrapped DataAccessException
     * @param voidQuery - Implementation of VoidQuery interface
     * @param exceptionMessage - the message will be used as a message to the DataAccessException which wraps any checked excepton.
     * @throws DataAccessException - if a checked exception arises it will be wrapped as DataAccessException
     */
    public final void execute(VoidQuery voidQuery, String exceptionMessage) throws DataAccessException {
        RepositoryConnection connection = null;
        try {
            connection = connectionProvider.getConnection();
            voidQuery.execute(connection);
        } catch (DataAccessException e) {
            throw new DataAccessException(getExceptionMessage(e, exceptionMessage), e.getCause());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(getExceptionMessage(e, exceptionMessage), e);
        } finally {
            RepositoryConnectionProvider.closeConnectionQuietly(connection);
        }
    }

    private String getExceptionMessage(Exception e, String exceptionMessage) throws DataAccessException {
        return StringUtils.isEmpty(exceptionMessage) ?
                e.getMessage() :
                exceptionMessage;
    }
}
