package com.ontotext.skoshi.repositories.sesame;

import com.ontotext.skoshi.repositories.UriEncodeRepository;
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.Map;

public class SesameUriEncodeRepository implements UriEncodeRepository {

    private Repository repository;

    public SesameUriEncodeRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void fillMaps(Map<String, String> ns2prefix, Map<String, String> prefix2ns) {
        assert ns2prefix != null;
        assert ns2prefix.size() == 0;
        assert prefix2ns != null;
        assert prefix2ns.size() == 0;

        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Namespace> namespaces = connection.getNamespaces();
                while (namespaces.hasNext()) {
                    Namespace ns = namespaces.next();
                    ns2prefix.put(ns.getName(), ns.getPrefix());
                    prefix2ns.put(ns.getPrefix(), ns.getName());
                }
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void addNamespace(String prefix, String namespace) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                repository.getConnection().setNamespace(prefix, namespace);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }
}
