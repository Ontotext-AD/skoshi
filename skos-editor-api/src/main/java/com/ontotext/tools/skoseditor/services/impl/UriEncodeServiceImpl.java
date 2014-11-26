package com.ontotext.tools.skoseditor.services.impl;

import com.ontotext.tools.skoseditor.repositories.UriEncodeRepository;
import com.ontotext.tools.skoseditor.services.UriEncodeService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.HashMap;
import java.util.Map;

public class UriEncodeServiceImpl implements UriEncodeService {

    private UriEncodeRepository uriEncodeRepository;

    private Map<String, String> ns2prefix;
    private Map<String, String> prefix2ns;

    public UriEncodeServiceImpl(UriEncodeRepository uriEncodeRepository) {
        this.uriEncodeRepository = uriEncodeRepository;
        ns2prefix = new HashMap<>();
        prefix2ns = new HashMap<>();
        uriEncodeRepository.fillMaps(ns2prefix, prefix2ns);
    }

    @Override
    public String encode(URI id) {
        String prefix = ns2prefix.get(id.getNamespace());
        if (prefix == null) {
            prefix = addUnknownNamespace(id);
        }
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
        if (!prefix2ns.containsKey(prefix)) {
            throw new IllegalArgumentException("Couldn't find the prefix '" + prefix + "' in the mappings.");
        }
        String namespace = prefix2ns.get(prefix);
        URI uri = new ValueFactoryImpl().createURI(namespace, localName);
        return uri;
    }

    synchronized private String addUnknownNamespace(URI uri) {
        int size = ns2prefix.size();
        String namespace = uri.getNamespace();
        String prefix = "ns"+size;
        if (ns2prefix.containsKey(namespace)) {
            throw new IllegalStateException("A namespace mapping already exists for: " + namespace);
        } else {
            ns2prefix.put(namespace, prefix);
        }
        if (prefix2ns.containsKey(prefix)) {
            throw new IllegalStateException("A prefix mapping already exists for: " + prefix);
        } else {
            prefix2ns.put(prefix, namespace);
        }
        uriEncodeRepository.addNamespace(prefix, namespace);
        return prefix;
    }
}
