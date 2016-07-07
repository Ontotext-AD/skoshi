package com.ontotext.skoshi.util.semanticstore;

import org.openrdf.repository.RepositoryConnection;

public interface VoidQuery {
    void execute(RepositoryConnection connection) throws Exception;
}
