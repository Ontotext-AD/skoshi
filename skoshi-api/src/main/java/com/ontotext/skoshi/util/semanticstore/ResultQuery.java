package com.ontotext.skoshi.util.semanticstore;

import org.openrdf.repository.RepositoryConnection;

public interface ResultQuery<T> {
    T executeQuery(RepositoryConnection connection) throws Exception;
}
