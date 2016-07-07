package com.ontotext.skoshi.repositories;

import java.util.Map;

public interface UriEncodeRepository {

    void fillMaps(Map<String, String> ns2prefix, Map<String, String> prefix2ns);

    void addNamespace(String prefix, String namespace);
}
