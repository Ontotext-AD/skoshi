package com.ontotext.tools.skoseditor.services.impl;

import com.ontotext.tools.skoseditor.services.UriEncodeService;
import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UriEncodeServiceImpl implements UriEncodeService {

    private Logger log = LoggerFactory.getLogger(UriEncodeServiceImpl.class);

    private Repository repository;

    private Map<String, String> ns2prefix;
    private Map<String, String> prefix2ns;

    public UriEncodeServiceImpl(Repository repository) {
        ns2prefix = new HashMap<>();
        prefix2ns = new HashMap<>();

        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Namespace> namespaces = repository.getConnection().getNamespaces();
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
    public String encode(URI id) {
        String prefix = ns2prefix.get(id.getNamespace());
        String localName = id.getLocalName();
        String value = prefix + "~" + localName;
        return value;
    }

    @Override
    public URI decode(String id) {
        String[] parts = id.split("~");
        if (parts == null || parts.length != 2) {
            throw new IllegalArgumentException("Illegal format for id: " + id);
        }
        String prefix = parts[0];
        String localName = parts[1];
        String namespace = prefix2ns.get(prefix);
        URI uri = new ValueFactoryImpl().createURI(namespace, localName);
        return uri;
    }
}
